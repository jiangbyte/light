package io.jiangbyte.github.infrastructure.account.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jiangbyte.github.domain.account.adapter.repository.AccountIdentifierConflictException;
import io.jiangbyte.github.domain.account.adapter.repository.IAccountRepository;
import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountPageQuery;
import io.jiangbyte.github.domain.account.model.valobj.AccountStatus;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.model.valobj.LoginMethod;
import io.jiangbyte.github.infrastructure.account.persistence.dao.AdminAccountDao;
import io.jiangbyte.github.infrastructure.account.persistence.dao.PortalAccountDao;
import io.jiangbyte.github.infrastructure.account.persistence.dao.po.AccountPO;
import io.jiangbyte.github.infrastructure.account.persistence.dao.po.AdminAccountPO;
import io.jiangbyte.github.infrastructure.account.persistence.dao.po.PortalAccountPO;
import io.jiangbyte.github.types.page.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

/**
 * 按账户域路由到 light_admin_user 或 light_portal_user 的仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class AccountRepository implements IAccountRepository {
    private final AdminAccountDao adminDao;
    private final PortalAccountDao portalDao;

    @Override
    public Optional<AccountEntity> findById(AccountType type, long id) {
        return switch (type) {
            case ADMIN -> Optional.ofNullable(adminDao.selectById(id)).map(this::toEntity);
            case PORTAL -> Optional.ofNullable(portalDao.selectById(id)).map(this::toEntity);
        };
    }

    @Override
    public List<AccountEntity> findByIds(AccountType type, Collection<Long> ids) {
        return switch (type) {
            case ADMIN -> adminDao.selectByIds(ids).stream().map(this::toEntity).toList();
            case PORTAL -> portalDao.selectByIds(ids).stream().map(this::toEntity).toList();
        };
    }

    @Override
    public Optional<AccountEntity> findByLoginIdentifier(AccountType type, LoginMethod method, String identifier) {
        if (type == AccountType.ADMIN) {
            LambdaQueryWrapper<AdminAccountPO> wrapper = new LambdaQueryWrapper<>();
            applyIdentifier(wrapper, AdminAccountPO::getUsername, AdminAccountPO::getEmail,
                    AdminAccountPO::getPhone, method, identifier);
            return Optional.ofNullable(adminDao.selectOne(wrapper)).map(this::toEntity);
        }
        LambdaQueryWrapper<PortalAccountPO> wrapper = new LambdaQueryWrapper<>();
        applyIdentifier(wrapper, PortalAccountPO::getUsername, PortalAccountPO::getEmail,
                PortalAccountPO::getPhone, method, identifier);
        return Optional.ofNullable(portalDao.selectOne(wrapper)).map(this::toEntity);
    }

    @Override
    public boolean existsIdentifier(AccountType type, LoginMethod method, String identifier, Long excludeId) {
        if (identifier == null) {
            return false;
        }
        if (type == AccountType.ADMIN) {
            LambdaQueryWrapper<AdminAccountPO> wrapper = new LambdaQueryWrapper<>();
            applyIdentifier(wrapper, AdminAccountPO::getUsername, AdminAccountPO::getEmail,
                    AdminAccountPO::getPhone, method, identifier);
            wrapper.ne(excludeId != null, AdminAccountPO::getId, excludeId);
            return adminDao.selectCount(wrapper) > 0;
        }
        LambdaQueryWrapper<PortalAccountPO> wrapper = new LambdaQueryWrapper<>();
        applyIdentifier(wrapper, PortalAccountPO::getUsername, PortalAccountPO::getEmail,
                PortalAccountPO::getPhone, method, identifier);
        wrapper.ne(excludeId != null, PortalAccountPO::getId, excludeId);
        return portalDao.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsAny(AccountType type) {
        if (type == AccountType.ADMIN) {
            return adminDao.selectCount(new LambdaQueryWrapper<>()) > 0;
        }
        return portalDao.selectCount(new LambdaQueryWrapper<>()) > 0;
    }

    @Override
    public PageResult<AccountEntity> findPage(AccountType type, AccountPageQuery query) {
        if (type == AccountType.ADMIN) {
            LambdaQueryWrapper<AdminAccountPO> wrapper = new LambdaQueryWrapper<>();
            applyFilter(wrapper, AdminAccountPO::getUsername, AdminAccountPO::getNickname,
                    AdminAccountPO::getEmail, AdminAccountPO::getPhone, AdminAccountPO::getStatus,
                    AdminAccountPO::getId, query.keyword(), query.status());
            Page<AdminAccountPO> result = adminDao.selectPage(
                    new Page<>(query.page(), query.pageSize()), wrapper);
            return toPage(result.getRecords(), result.getTotal(), query);
        }
        LambdaQueryWrapper<PortalAccountPO> wrapper = new LambdaQueryWrapper<>();
        applyFilter(wrapper, PortalAccountPO::getUsername, PortalAccountPO::getNickname,
                PortalAccountPO::getEmail, PortalAccountPO::getPhone, PortalAccountPO::getStatus,
                PortalAccountPO::getId, query.keyword(), query.status());
        Page<PortalAccountPO> result = portalDao.selectPage(
                new Page<>(query.page(), query.pageSize()), wrapper);
        return toPage(result.getRecords(), result.getTotal(), query);
    }

    @Override
    public AccountEntity insert(AccountType type, AccountEntity account) {
        // 1. 将领域账户转换为目标表 PO
        if (type == AccountType.ADMIN) {
            AdminAccountPO po = new AdminAccountPO();
            copyToPo(account, po);
            // 2. 执行插入并将数据库唯一键冲突转换为领域可识别异常
            insertAdmin(po);
            return toEntity(po);
        }
        PortalAccountPO po = new PortalAccountPO();
        copyToPo(account, po);
        insertPortal(po);
        return toEntity(po);
    }

    @Override
    public AccountEntity update(AccountType type, AccountEntity account) {
        // 1. 使用显式 set 更新，确保空邮箱和空手机号能够真正写入 NULL
        if (type == AccountType.ADMIN) {
            updateAdmin(account);
            return adapterEntity(account);
        }
        updatePortal(account);
        return adapterEntity(account);
    }

    @Override
    public void deleteByIds(AccountType type, Collection<Long> ids) {
        if (type == AccountType.ADMIN) {
            adminDao.deleteByIds(ids);
            return;
        }
        portalDao.deleteByIds(ids);
    }

    private void insertAdmin(AdminAccountPO po) {
        try {
            adminDao.insert(po);
        } catch (DuplicateKeyException ex) {
            throw new AccountIdentifierConflictException(ex);
        }
    }

    private void insertPortal(PortalAccountPO po) {
        try {
            portalDao.insert(po);
        } catch (DuplicateKeyException ex) {
            throw new AccountIdentifierConflictException(ex);
        }
    }

    private void updateAdmin(AccountEntity account) {
        UpdateWrapper<AdminAccountPO> wrapper = new UpdateWrapper<>();
        applyUpdate(wrapper, account);
        try {
            adminDao.update(null, wrapper);
        } catch (DuplicateKeyException ex) {
            throw new AccountIdentifierConflictException(ex);
        }
    }

    private void updatePortal(AccountEntity account) {
        UpdateWrapper<PortalAccountPO> wrapper = new UpdateWrapper<>();
        applyUpdate(wrapper, account);
        try {
            portalDao.update(null, wrapper);
        } catch (DuplicateKeyException ex) {
            throw new AccountIdentifierConflictException(ex);
        }
    }

    private void applyUpdate(UpdateWrapper<? extends AccountPO> wrapper, AccountEntity account) {
        wrapper.eq("id", account.getId())
                .set("username", account.getUsername())
                .set("password_hash", account.getPasswordHash())
                .set("nickname", account.getNickname())
                .set("email", account.getEmail())
                .set("phone", account.getPhone())
                .set("status", account.getStatus().name())
                .set("token_version", account.getTokenVersion())
                .set("updated_at", account.getUpdatedAt());
    }

    private <T extends AccountPO> void applyIdentifier(LambdaQueryWrapper<T> wrapper,
                                                       com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> username,
                                                       com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> email,
                                                       com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> phone,
                                                       LoginMethod method, String identifier) {
        switch (method) {
            case USERNAME -> wrapper.eq(username, identifier);
            case EMAIL -> wrapper.eq(email, identifier);
            case PHONE -> wrapper.eq(phone, identifier);
        }
    }

    private <T extends AccountPO> void applyFilter(LambdaQueryWrapper<T> wrapper,
                                                   com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> username,
                                                   com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> nickname,
                                                   com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> email,
                                                   com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> phone,
                                                   com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> status,
                                                   com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> id,
                                                   String keyword, AccountStatus accountStatus) {
        if (keyword != null) {
            wrapper.and(condition -> condition
                    .like(username, keyword)
                    .or().like(nickname, keyword)
                    .or().like(email, keyword)
                    .or().like(phone, keyword));
        }
        wrapper.eq(accountStatus != null, status, accountStatus == null ? null : accountStatus.name());
        wrapper.orderByDesc(id);
    }

    private PageResult<AccountEntity> toPage(List<? extends AccountPO> records, long total,
                                             AccountPageQuery query) {
        List<AccountEntity> items = records.stream().map(this::toEntity).toList();
        return new PageResult<>(items, total, query.page(), query.pageSize());
    }

    private AccountEntity toEntity(AccountPO po) {
        return new AccountEntity(po.getId(), po.getUsername(), po.getPasswordHash(), po.getNickname(),
                po.getEmail(), po.getPhone(), AccountStatus.valueOf(po.getStatus()),
                po.getTokenVersion() == null ? 0 : po.getTokenVersion(),
                po.getCreatedAt(), po.getUpdatedAt());
    }

    private AccountEntity adapterEntity(AccountEntity account) {
        return new AccountEntity(account.getId(), account.getUsername(), account.getPasswordHash(),
                account.getNickname(), account.getEmail(), account.getPhone(), account.getStatus(),
                account.getTokenVersion(), account.getCreatedAt(), account.getUpdatedAt());
    }

    private void copyToPo(AccountEntity account, AccountPO po) {
        po.setId(account.getId());
        po.setUsername(account.getUsername());
        po.setPasswordHash(account.getPasswordHash());
        po.setNickname(account.getNickname());
        po.setEmail(account.getEmail());
        po.setPhone(account.getPhone());
        po.setStatus(account.getStatus().name());
        po.setTokenVersion(account.getTokenVersion());
        po.setCreatedAt(account.getCreatedAt());
        po.setUpdatedAt(account.getUpdatedAt());
    }
}
