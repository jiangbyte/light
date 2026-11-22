package io.jiangbyte.github.domain.account.model.valobj;

/**
 * Sa-Token 签发的会话信息；tokenValue 是不透明原始令牌。
 */
public record AccountToken(String tokenName, String tokenValue, long tokenTimeout, long activeTimeout) {
}
