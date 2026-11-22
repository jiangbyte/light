package io.jiangbyte.github.domain.account.adapter.port;

/**
 * 密码哈希端口，隔离领域层与具体密码算法实现。
 */
public interface IAccountPasswordPort {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
