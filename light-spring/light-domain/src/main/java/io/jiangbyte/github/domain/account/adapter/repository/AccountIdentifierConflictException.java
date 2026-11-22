package io.jiangbyte.github.domain.account.adapter.repository;

/**
 * 仓储层发现用户名、邮箱或手机号唯一键冲突时抛出。
 */
public class AccountIdentifierConflictException extends RuntimeException {
    public AccountIdentifierConflictException(Throwable cause) {
        super(cause);
    }
}
