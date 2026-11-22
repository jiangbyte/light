package io.jiangbyte.github.domain.account.adapter.repository;

import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountPageQuery;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.model.valobj.LoginMethod;
import io.jiangbyte.github.types.page.PageResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 账户仓储端口。实现负责按 AccountType 路由到对应前缀表。
 */
public interface IAccountRepository {
    Optional<AccountEntity> findById(AccountType type, long id);

    List<AccountEntity> findByIds(AccountType type, Collection<Long> ids);

    Optional<AccountEntity> findByLoginIdentifier(AccountType type, LoginMethod method, String identifier);

    boolean existsIdentifier(AccountType type, LoginMethod method, String identifier, Long excludeId);

    boolean existsAny(AccountType type);

    PageResult<AccountEntity> findPage(AccountType type, AccountPageQuery query);

    AccountEntity insert(AccountType type, AccountEntity account);

    AccountEntity update(AccountType type, AccountEntity account);

    void deleteByIds(AccountType type, Collection<Long> ids);
}
