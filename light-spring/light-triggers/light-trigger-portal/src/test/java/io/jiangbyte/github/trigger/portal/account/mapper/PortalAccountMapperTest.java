package io.jiangbyte.github.trigger.portal.account.mapper;

import io.jiangbyte.github.api.portal.account.auth.dto.PortalAccountDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.PortalLoginResponseDTO;
import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountLoginResult;
import io.jiangbyte.github.domain.account.model.valobj.AccountStatus;
import io.jiangbyte.github.domain.account.model.valobj.AccountToken;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PortalAccountMapperTest {
    private final PortalAccountMapper mapper = Mappers.getMapper(PortalAccountMapper.class);

    @Test
    void mapsPortalAccountAndLoginResponse() {
        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 8, 12, 0);
        AccountEntity account = new AccountEntity(7L, "portal_user", "hash", "门户用户",
                "user@example.com", "13900139000", AccountStatus.ACTIVE, 0, createdAt, createdAt);

        PortalAccountDTO dto = mapper.toDto(account);
        PortalLoginResponseDTO login = mapper.toLoginResponse(new AccountLoginResult(account,
                new AccountToken("Authorization", "opaque-token", 604800, 7200)));

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getUsername()).isEqualTo("portal_user");
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(login.getTokenValue()).isEqualTo("opaque-token");
        assertThat(login.getAccount().getPhone()).isEqualTo("13900139000");
    }
}
