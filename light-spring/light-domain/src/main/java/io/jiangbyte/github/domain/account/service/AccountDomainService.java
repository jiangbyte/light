package io.jiangbyte.github.domain.account.service;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.domain.account.adapter.port.IAccountPasswordPort;
import io.jiangbyte.github.domain.account.adapter.port.IAccountSessionPort;
import io.jiangbyte.github.domain.account.adapter.repository.AccountIdentifierConflictException;
import io.jiangbyte.github.domain.account.adapter.repository.IAccountRepository;
import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountLoginResult;
import io.jiangbyte.github.domain.account.model.valobj.AccountPageQuery;
import io.jiangbyte.github.domain.account.model.valobj.AccountStatus;
import io.jiangbyte.github.domain.account.model.valobj.AccountToken;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.model.valobj.LoginMethod;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;
import io.jiangbyte.github.types.page.PageResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 账户领域服务，集中维护登录、状态、唯一性和管理端 CRUD 规则。
 */
@RequiredArgsConstructor
public class AccountDomainService {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_.-]{3,32}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{7,14}$");
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 64;

    private final IAccountRepository repository;
    private final IAccountPasswordPort passwordPort;
    private final IAccountSessionPort sessionPort;

    /**
     * 按指定登录标识校验密码并签发不透明令牌。
     */
    public AccountLoginResult login(AccountType type, LoginMethod method, String identifier, String rawPassword) {
        // 1. 校验登录域、标识类型和基础输入
        requireType(type);
        if (method == null) {
            throw illegalParameter("登录方式不能为空");
        }
        String normalizedIdentifier = normalizeRequired(identifier, identifierLabel(method));
        if (StrUtil.isBlank(rawPassword)) {
            throw unauthorized();
        }

        // 2. 查询账户并统一处理不存在、禁用和密码错误，避免泄露账户状态
        AccountEntity account = repository.findByLoginIdentifier(type, method, normalizedIdentifier)
                .orElseThrow(this::unauthorized);
        if (account.getStatus() != AccountStatus.ACTIVE
                || !passwordPort.matches(rawPassword, account.getPasswordHash())) {
            throw unauthorized();
        }

        // 3. 创建独立登录域会话并返回原始不透明令牌
        AccountToken token = sessionPort.login(type, account.getId(), account.getTokenVersion());
        return new AccountLoginResult(account, token);
    }

    /**
     * 读取当前登录账户；令牌无效、账户不存在或已禁用时拒绝访问。
     */
    public AccountEntity current(AccountType type) {
        // 1. 从对应登录域读取会话账户 ID
        requireType(type);
        long accountId = sessionPort.currentAccountId(type);

        // 2. 校验账户仍然存在且可用
        AccountEntity account = repository.findById(type, accountId)
                .orElseThrow(this::unauthorized);
        if (account.getStatus() != AccountStatus.ACTIVE) {
            // 3. 禁用账户的残留会话立即清理
            sessionPort.kickout(type, accountId);
            throw unauthorized();
        }
        // 4. 密码变更后旧令牌版本与账户版本不一致，立即注销当前会话
        if (sessionPort.currentTokenVersion(type) != account.getTokenVersion()) {
            sessionPort.logout(type);
            throw unauthorized();
        }
        return account;
    }

    public void logout(AccountType type) {
        requireType(type);
        sessionPort.logout(type);
    }

    /**
     * 分页查询指定账户域，并统一校验分页参数。
     */
    public PageResult<AccountEntity> list(AccountType type, AccountPageQuery query) {
        // 1. 校验账户域和领域查询对象
        requireType(type);
        if (query == null) {
            throw illegalParameter("分页查询条件不能为空");
        }

        // 2. 将查询条件对象直接交给仓储
        return repository.findPage(type, query);
    }

    public AccountEntity detail(AccountType type, long id) {
        requirePositiveId(id);
        return repository.findById(type, id).orElseThrow(() -> notFound(id));
    }

    /**
     * 创建账户；用户名必填，邮箱和手机号可按需为空。
     */
    public AccountEntity create(AccountType type, String username, String rawPassword, String nickname,
                                String email, String phone, String status) {
        // 1. 清洗并校验账户资料和密码
        requireType(type);
        String normalizedUsername = validateUsername(username);
        validatePassword(rawPassword);
        String normalizedNickname = StrUtil.trimToNull(nickname);
        String normalizedEmail = validateOptionalEmail(email);
        String normalizedPhone = validateOptionalPhone(phone);
        AccountStatus resolvedStatus = StrUtil.isBlank(status)
                ? AccountStatus.ACTIVE : AccountStatus.parseNullable(status);

        // 2. 在写库前检查三种登录标识的唯一性
        checkAllIdentifiers(type, null, normalizedUsername, normalizedEmail, normalizedPhone);

        // 3. 哈希密码并持久化新账户
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        AccountEntity account = new AccountEntity(null, normalizedUsername, passwordPort.encode(rawPassword),
                normalizedNickname, normalizedEmail, normalizedPhone, resolvedStatus, 0, now, now);
        return saveWithConflictTranslation(type, account, true);
    }

    /**
     * 更新账户；未传字段保持原值，空邮箱或空手机号表示清除。
     */
    public AccountEntity update(AccountType type, long id, long actorAccountId, String username,
                                String rawPassword, String nickname, String email, String phone, String status) {
        // 1. 读取原账户并合并本次变更
        requireType(type);
        requirePositiveId(id);
        AccountEntity current = repository.findById(type, id).orElseThrow(() -> notFound(id));
        String nextUsername = username == null ? current.getUsername() : validateUsername(username);
        String nextNickname = nickname == null ? current.getNickname() : StrUtil.trimToNull(nickname);
        String nextEmail = email == null ? current.getEmail() : validateOptionalEmail(email);
        String nextPhone = phone == null ? current.getPhone() : validateOptionalPhone(phone);
        AccountStatus nextStatus = StrUtil.isBlank(status)
                ? current.getStatus() : AccountStatus.parseNullable(status);

        // 2. 防止管理员通过更新操作禁用自己
        if (type == AccountType.ADMIN && id == actorAccountId && nextStatus == AccountStatus.DISABLED) {
            throw illegalParameter("不能禁用当前登录管理员");
        }

        // 3. 校验可选密码和三种登录标识的唯一性
        if (rawPassword != null) {
            validatePassword(rawPassword);
        }
        checkAllIdentifiers(type, id, nextUsername, nextEmail, nextPhone);

        // 4. 保存变更，并在改密或禁用时清理该账户全部会话
        String nextPasswordHash = rawPassword == null
                ? current.getPasswordHash() : passwordPort.encode(rawPassword);
        int nextTokenVersion = rawPassword == null
                ? current.getTokenVersion() : current.getTokenVersion() + 1;
        AccountEntity updated = new AccountEntity(id, nextUsername, nextPasswordHash, nextNickname,
                nextEmail, nextPhone, nextStatus, nextTokenVersion, current.getCreatedAt(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        AccountEntity saved = saveWithConflictTranslation(type, updated, false);
        if (rawPassword != null || nextStatus == AccountStatus.DISABLED) {
            sessionPort.kickout(type, id);
        }
        return saved;
    }

    /**
     * 物理删除账户，并立即清理该账户在对应登录域中的全部会话。
     */
    public void delete(AccountType type, Collection<Long> ids, long actorAccountId) {
        // 1. 去重并校验批量 ID
        requireType(type);
        List<Long> targetIds = normalizeIds(ids);

        // 2. 禁止批量请求中包含当前登录管理员
        if (type == AccountType.ADMIN && targetIds.contains(actorAccountId)) {
            throw illegalParameter("不能删除当前登录管理员");
        }

        // 3. 删除前确认全部账户存在，避免部分删除
        List<AccountEntity> accounts = repository.findByIds(type, targetIds);
        if (accounts.size() != targetIds.size()) {
            throw new BizException(ResponseCode.NOT_FOUND.getCode(), "部分账户不存在", 404);
        }

        // 4. 执行批量物理删除并清理各账户会话
        repository.deleteByIds(type, targetIds);
        targetIds.forEach(id -> sessionPort.kickout(type, id));
    }

    private AccountEntity saveWithConflictTranslation(AccountType type, AccountEntity account, boolean create) {
        try {
            return create ? repository.insert(type, account) : repository.update(type, account);
        } catch (AccountIdentifierConflictException ex) {
            throw new BizException(ResponseCode.CONFLICT, "用户名、邮箱或手机号已存在");
        }
    }

    private void checkAllIdentifiers(AccountType type, Long excludeId, String username, String email,
                                     String phone) {
        if (repository.existsIdentifier(type, LoginMethod.USERNAME, username, excludeId)
                || (email != null && repository.existsIdentifier(type, LoginMethod.EMAIL, email, excludeId))
                || (phone != null && repository.existsIdentifier(type, LoginMethod.PHONE, phone, excludeId))) {
            throw new BizException(ResponseCode.CONFLICT, "用户名、邮箱或手机号已存在");
        }
    }

    private String validateUsername(String username) {
        String value = normalizeRequired(username, "用户名");
        if (!USERNAME_PATTERN.matcher(value).matches()) {
            throw illegalParameter("用户名只能包含字母、数字、下划线、点和连字符，长度为 3 到 32");
        }
        return value;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw illegalParameter("密码长度必须在 8 到 64 之间");
        }
    }

    private String validateOptionalEmail(String email) {
        String value = StrUtil.trimToNull(email);
        if (value != null && !EMAIL_PATTERN.matcher(value).matches()) {
            throw illegalParameter("邮箱格式不正确");
        }
        return value;
    }

    private String validateOptionalPhone(String phone) {
        String value = StrUtil.trimToNull(phone);
        if (value != null && !PHONE_PATTERN.matcher(value).matches()) {
            throw illegalParameter("手机号格式不正确");
        }
        return value;
    }

    private String normalizeRequired(String value, String label) {
        String normalized = StrUtil.trimToNull(value);
        if (normalized == null) {
            throw illegalParameter(label + "不能为空");
        }
        return normalized;
    }

    private String identifierLabel(LoginMethod method) {
        return switch (method) {
            case USERNAME -> "用户名";
            case EMAIL -> "邮箱";
            case PHONE -> "手机号";
        };
    }

    private void requireType(AccountType type) {
        if (type == null) {
            throw illegalParameter("账户类型不能为空");
        }
    }

    private void requirePositiveId(long id) {
        if (id <= 0) {
            throw illegalParameter("账户 ID 必须大于 0");
        }
    }

    private List<Long> normalizeIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw illegalParameter("删除账户 ID 不能为空");
        }
        LinkedHashSet<Long> uniqueIds = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id == null) {
                throw illegalParameter("账户 ID 不能为空");
            }
            requirePositiveId(id);
            uniqueIds.add(id);
        }
        return List.copyOf(uniqueIds);
    }

    private BizException illegalParameter(String message) {
        return new BizException(ResponseCode.ILLEGAL_PARAMETER, message);
    }

    private BizException unauthorized() {
        return new BizException(ResponseCode.UNAUTHORIZED.getCode(), "登录凭证无效或账户不可用", 401);
    }

    private BizException notFound(long id) {
        return new BizException(ResponseCode.NOT_FOUND.getCode(), "账户不存在: " + id, 404);
    }
}
