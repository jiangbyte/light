package io.jiangbyte.github.infrastructure.account.auth.adapter;

import io.jiangbyte.github.domain.account.adapter.port.IAccountPasswordPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt 密码实现，使用成本因子 12 提升离线破解成本。
 */
public class BCryptAccountPasswordAdapter implements IAccountPasswordPort {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return encoder.matches(rawPassword, passwordHash);
    }
}
