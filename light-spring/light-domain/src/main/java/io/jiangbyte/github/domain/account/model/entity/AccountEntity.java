package io.jiangbyte.github.domain.account.model.entity;

import io.jiangbyte.github.domain.account.model.valobj.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Admin 与 Portal 共用的账户实体；账户域仍由 AccountType 和独立表隔离。
 */
@Getter
@AllArgsConstructor
public final class AccountEntity {
    private final Long id;
    private final String username;
    private final String passwordHash;
    private final String nickname;
    private final String email;
    private final String phone;
    private final AccountStatus status;
    private final int tokenVersion;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
