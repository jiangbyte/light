package io.jiangbyte.github.domain.account.adapter.port;

import io.jiangbyte.github.domain.account.model.valobj.AccountToken;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;

/**
 * 不透明登录会话端口，由 Sa-Token 基础设施实现。
 */
public interface IAccountSessionPort {
    AccountToken login(AccountType type, long accountId, int tokenVersion);

    long currentAccountId(AccountType type);

    int currentTokenVersion(AccountType type);

    void logout(AccountType type);

    void kickout(AccountType type, long accountId);
}
