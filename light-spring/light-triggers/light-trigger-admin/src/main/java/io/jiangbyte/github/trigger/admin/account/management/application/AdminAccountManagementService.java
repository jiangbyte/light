package io.jiangbyte.github.trigger.admin.account.management.application;

import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import io.jiangbyte.github.api.admin.account.management.dto.AdminAccountPageRequestDTO;
import io.jiangbyte.github.api.admin.account.management.dto.CreateAdminAccountRequestDTO;
import io.jiangbyte.github.api.admin.account.management.dto.UpdateAdminAccountRequestDTO;
import io.jiangbyte.github.domain.account.adapter.port.IPasswordTransportCryptoPort;
import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountPageQuery;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.service.AccountDomainService;
import io.jiangbyte.github.trigger.admin.account.mapper.AdminAccountMapper;
import io.jiangbyte.github.types.page.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端维护 Admin/Portal 两类账户的用例编排。
 */
@Service
@RequiredArgsConstructor
public class AdminAccountManagementService {
    private final AccountDomainService accountDomainService;
    private final AdminAccountMapper accountMapper;
    private final IPasswordTransportCryptoPort passwordCryptoPort;

    public PageResult<AccountEntity> list(AccountType type, AdminAccountPageRequestDTO request) {
        // 1. 先校验当前管理员仍有效
        accountDomainService.current(AccountType.ADMIN);
        // 2. 将 API 查询参数对象转换为领域查询对象后执行分页
        AccountPageQuery query = AccountPageQuery.of(request.getPage(), request.getPageSize(),
                request.getKeyword(), request.getStatus());
        return accountDomainService.list(type, query);
    }

    public AccountEntity detail(AccountType type, long id) {
        // 1. 校验管理端登录态
        accountDomainService.current(AccountType.ADMIN);
        // 2. 查询目标账户域中的详情
        return accountDomainService.detail(type, id);
    }

    public AccountEntity create(AccountType type, CreateAdminAccountRequestDTO request) {
        // 1. 确认操作者已登录管理端
        accountDomainService.current(AccountType.ADMIN);
        // 2. 在指定账户域创建账户
        return accountDomainService.create(type, request.getUsername(),
                passwordCryptoPort.decrypt(request.getPassword()),
                request.getNickname(), request.getEmail(), request.getPhone(), request.getStatus());
    }

    public AccountEntity update(AccountType type, UpdateAdminAccountRequestDTO request) {
        // 1. 从当前 Admin 会话解析操作者，防止禁用或删除自己
        AccountEntity actor = accountDomainService.current(AccountType.ADMIN);
        // 2. 执行目标账户更新，密码为空时保持原值
        String rawPassword = request.getPassword() == null
                ? null : passwordCryptoPort.decrypt(request.getPassword());
        return accountDomainService.update(type, request.getId(), actor.getId(), request.getUsername(),
                rawPassword, request.getNickname(), request.getEmail(), request.getPhone(),
                request.getStatus());
    }

    public void delete(AccountType type, List<Long> ids) {
        // 1. 从当前 Admin 会话解析操作者
        AccountEntity actor = accountDomainService.current(AccountType.ADMIN);
        // 2. 批量物理删除目标账户并踢出其全部会话
        accountDomainService.delete(type, ids, actor.getId());
    }

    public PageResult<AdminAccountDTO> toPageDto(PageResult<AccountEntity> page) {
        return accountMapper.toPageDto(page);
    }

    public AdminAccountDTO toDto(AccountEntity account) {
        return accountMapper.toDto(account);
    }

}
