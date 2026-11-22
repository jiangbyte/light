package io.jiangbyte.github.domain.account.service;

import io.jiangbyte.github.domain.account.adapter.port.IAccountPasswordPort;
import io.jiangbyte.github.domain.account.adapter.port.IAccountSessionPort;
import io.jiangbyte.github.domain.account.adapter.repository.IAccountRepository;
import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountLoginResult;
import io.jiangbyte.github.domain.account.model.valobj.AccountPageQuery;
import io.jiangbyte.github.domain.account.model.valobj.AccountStatus;
import io.jiangbyte.github.domain.account.model.valobj.AccountToken;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.model.valobj.LoginMethod;
import io.jiangbyte.github.types.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountDomainServiceTest {
    @Mock
    private IAccountRepository repository;
    @Mock
    private IAccountPasswordPort passwordPort;
    @Mock
    private IAccountSessionPort sessionPort;

    private AccountDomainService service;

    @BeforeEach
    void setUp() {
        service = new AccountDomainService(repository, passwordPort, sessionPort);
    }

    @Test
    void createHashesPasswordAndDefaultsToActive() {
        // 1. 准备三列唯一检查和密码哈希返回值
        when(repository.existsIdentifier(eq(AccountType.ADMIN), any(), any(), isNull())).thenReturn(false);
        when(passwordPort.encode("password123")).thenReturn("bcrypt-hash");
        when(repository.insert(eq(AccountType.ADMIN), any())).thenAnswer(invocation -> {
            AccountEntity created = invocation.getArgument(1);
            return new AccountEntity(10L, created.getUsername(), created.getPasswordHash(),
                    created.getNickname(), created.getEmail(), created.getPhone(), created.getStatus(),
                    created.getTokenVersion(), created.getCreatedAt(), created.getUpdatedAt());
        });

        // 2. 创建账户并校验脱敏前领域数据
        AccountEntity created = service.create(AccountType.ADMIN, "admin_user", "password123",
                "管理员", "admin@example.com", null, null);

        assertThat(created.getId()).isEqualTo(10L);
        assertThat(created.getPasswordHash()).isEqualTo("bcrypt-hash");
        assertThat(created.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(created.getTokenVersion()).isZero();
    }

    @Test
    void loginSupportsEachIdentifierThroughItsMatchingPort() {
        AccountEntity account = account(7L, AccountStatus.ACTIVE);
        AccountToken token = new AccountToken("Authorization", "opaque-value", 604800, 7200);
        when(repository.findByLoginIdentifier(AccountType.PORTAL, LoginMethod.EMAIL, "user@example.com"))
                .thenReturn(Optional.of(account));
        when(passwordPort.matches("password123", "bcrypt-hash")).thenReturn(true);
        when(sessionPort.login(AccountType.PORTAL, 7L, 0)).thenReturn(token);

        AccountLoginResult result = service.login(AccountType.PORTAL, LoginMethod.EMAIL,
                "user@example.com", "password123");

        assertThat(result.account().getId()).isEqualTo(7L);
        assertThat(result.token().tokenValue()).isEqualTo("opaque-value");
    }

    @Test
    void disabledAccountCannotLogin() {
        when(repository.findByLoginIdentifier(AccountType.ADMIN, LoginMethod.USERNAME, "admin"))
                .thenReturn(Optional.of(account(1L, AccountStatus.DISABLED)));

        assertThatThrownBy(() -> service.login(AccountType.ADMIN, LoginMethod.USERNAME, "admin", "password123"))
                .isInstanceOf(BizException.class)
                .extracting("code")
                .isEqualTo("0401");
        verify(sessionPort, never()).login(any(), eq(1L), anyInt());
    }

    @Test
    void duplicateIdentifierReturnsConflict() {
        when(repository.existsIdentifier(eq(AccountType.ADMIN), eq(LoginMethod.USERNAME),
                eq("admin_user"), isNull())).thenReturn(true);

        assertThatThrownBy(() -> service.create(AccountType.ADMIN, "admin_user", "password123",
                null, null, null, "ACTIVE"))
                .isInstanceOf(BizException.class)
                .extracting("code")
                .isEqualTo("0409");
    }

    @Test
    void adminCannotDisableSelf() {
        when(repository.findById(AccountType.ADMIN, 1L))
                .thenReturn(Optional.of(account(1L, AccountStatus.ACTIVE)));

        assertThatThrownBy(() -> service.update(AccountType.ADMIN, 1L, 1L, null,
                null, null, null, null, "DISABLED"))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("不能禁用当前登录管理员");
        verify(repository, never()).update(any(), any());
    }

    @Test
    void passwordChangeKicksOutAllSessions() {
        AccountEntity account = account(8L, AccountStatus.ACTIVE);
        when(repository.findById(AccountType.PORTAL, 8L)).thenReturn(Optional.of(account));
        when(repository.existsIdentifier(eq(AccountType.PORTAL), any(), any(), eq(8L))).thenReturn(false);
        when(passwordPort.encode("newPassword1")).thenReturn("new-hash");
        when(repository.update(eq(AccountType.PORTAL), any())).thenAnswer(invocation -> invocation.getArgument(1));

        AccountEntity updated = service.update(AccountType.PORTAL, 8L, 1L, null,
                "newPassword1", null, null, null, null);

        assertThat(updated.getPasswordHash()).isEqualTo("new-hash");
        assertThat(updated.getTokenVersion()).isEqualTo(1);
        verify(sessionPort).kickout(AccountType.PORTAL, 8L);
    }

    @Test
    void staleTokenVersionIsRejected() {
        LocalDateTime now = LocalDateTime.now();
        AccountEntity account = new AccountEntity(9L, "portal_user", "hash", null,
                null, null, AccountStatus.ACTIVE, 3, now, now);
        when(repository.findById(AccountType.PORTAL, 9L)).thenReturn(Optional.of(account));
        when(sessionPort.currentAccountId(AccountType.PORTAL)).thenReturn(9L);
        when(sessionPort.currentTokenVersion(AccountType.PORTAL)).thenReturn(2);

        assertThatThrownBy(() -> service.current(AccountType.PORTAL))
                .isInstanceOf(BizException.class)
                .extracting("code")
                .isEqualTo("0401");
        verify(sessionPort).logout(AccountType.PORTAL);
    }

    @Test
    void adminCannotDeleteSelf() {
        assertThatThrownBy(() -> service.delete(AccountType.ADMIN, List.of(1L), 1L))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("不能删除当前登录管理员");
        verify(repository, never()).deleteByIds(any(), any());
    }

    @Test
    void batchDeleteDeduplicatesAndDeletesAllAccounts() {
        when(repository.findByIds(AccountType.PORTAL, List.of(2L, 3L)))
                .thenReturn(List.of(account(2L, AccountStatus.ACTIVE), account(3L, AccountStatus.ACTIVE)));

        service.delete(AccountType.PORTAL, List.of(2L, 3L, 3L), 1L);

        verify(repository).deleteByIds(AccountType.PORTAL, List.of(2L, 3L));
        verify(sessionPort).kickout(AccountType.PORTAL, 2L);
        verify(sessionPort).kickout(AccountType.PORTAL, 3L);
    }

    @Test
    void pageQueryAppliesDefaultsNormalizationAndStatusParsing() {
        AccountPageQuery query = AccountPageQuery.of(null, null, "  admin  ", "disabled");

        assertThat(query.page()).isEqualTo(1);
        assertThat(query.pageSize()).isEqualTo(20);
        assertThat(query.keyword()).isEqualTo("admin");
        assertThat(query.status()).isEqualTo(AccountStatus.DISABLED);
    }

    @Test
    void pageQueryRejectsInvalidPageSize() {
        assertThatThrownBy(() -> AccountPageQuery.of(1, 101, null, null))
                .isInstanceOf(BizException.class)
                .extracting("code")
                .isEqualTo("0400");
    }

    private AccountEntity account(long id, AccountStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new AccountEntity(id, "admin_" + id, "bcrypt-hash", "用户", "user@example.com",
                "13800138000", status, 0, now, now);
    }
}
