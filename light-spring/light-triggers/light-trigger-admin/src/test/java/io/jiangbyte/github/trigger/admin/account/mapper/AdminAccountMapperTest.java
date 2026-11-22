package io.jiangbyte.github.trigger.admin.account.mapper;

import io.jiangbyte.github.api.admin.account.auth.dto.AdminLoginResponseDTO;
import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountLoginResult;
import io.jiangbyte.github.domain.account.model.valobj.AccountStatus;
import io.jiangbyte.github.domain.account.model.valobj.AccountToken;
import io.jiangbyte.github.types.page.PageResult;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdminAccountMapperTest {
    private final AdminAccountMapper mapper = Mappers.getMapper(AdminAccountMapper.class);

    @Test
    void mapsAccountPageAndLoginResponse() {
        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 8, 12, 0);
        LocalDateTime updatedAt = createdAt.plusMinutes(5);
        AccountEntity account = new AccountEntity(42L, "admin_user", "hash", "管理员",
                "admin@example.com", "13800138000", AccountStatus.DISABLED, 0, createdAt, updatedAt);

        AdminAccountDTO dto = mapper.toDto(account);
        PageResult<AdminAccountDTO> page = mapper.toPageDto(
                new PageResult<>(List.of(account), 1, 1, 20));
        AdminLoginResponseDTO login = mapper.toLoginResponse(new AccountLoginResult(account,
                new AccountToken("Authorization", "opaque-token", 604800, 7200)));

        assertThat(dto.getId()).isEqualTo(42L);
        assertThat(dto.getStatus()).isEqualTo("DISABLED");
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(page.items()).singleElement().extracting(AdminAccountDTO::getId).isEqualTo(42L);
        assertThat(page.total()).isEqualTo(1);
        assertThat(login.getTokenValue()).isEqualTo("opaque-token");
        assertThat(login.getActiveTimeout()).isEqualTo(7200);
        assertThat(login.getAccount().getUsername()).isEqualTo("admin_user");
    }
}
