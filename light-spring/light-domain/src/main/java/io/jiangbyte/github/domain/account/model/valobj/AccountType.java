package io.jiangbyte.github.domain.account.model.valobj;

/**
 * 账户所属的登录域。两个域使用独立数据表和独立会话逻辑，不能互相换用令牌。
 */
public enum AccountType {
    ADMIN,
    PORTAL
}
