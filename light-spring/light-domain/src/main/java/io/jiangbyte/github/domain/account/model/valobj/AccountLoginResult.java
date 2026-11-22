package io.jiangbyte.github.domain.account.model.valobj;

import io.jiangbyte.github.domain.account.model.entity.AccountEntity;

/**
 * 登录成功后的账户与会话信息。
 */
public record AccountLoginResult(AccountEntity account, AccountToken token) {
}
