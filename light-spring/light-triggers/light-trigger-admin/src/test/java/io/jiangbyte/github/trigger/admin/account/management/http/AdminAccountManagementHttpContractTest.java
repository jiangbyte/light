package io.jiangbyte.github.trigger.admin.account.management.http;

import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import io.jiangbyte.github.api.admin.account.management.dto.AdminAccountPageRequestDTO;
import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountStatus;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.trigger.admin.account.management.application.AdminAccountManagementService;
import io.jiangbyte.github.types.page.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminAccountManagementHttpContractTest {
    @Mock
    private AdminAccountManagementService managementService;

    @Test
    void detailUsesQueryParameterInsteadOfPathVariable() throws Exception {
        AccountEntity account = account();
        AdminAccountDTO dto = new AdminAccountDTO();
        dto.setId(42L);
        when(managementService.detail(AccountType.ADMIN, 42L)).thenReturn(account);
        when(managementService.toDto(account)).thenReturn(dto);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AdminUserController(managementService)).build();

        mockMvc.perform(get("/api/accounts/admins/detail").param("id", "42"))
                .andExpect(status().isOk());

        verify(managementService).detail(AccountType.ADMIN, 42L);
    }

    @Test
    void listBindsQueryParametersToRequestObject() throws Exception {
        AccountEntity account = account();
        AdminAccountDTO dto = new AdminAccountDTO();
        dto.setId(42L);
        PageResult<AccountEntity> entityPage = new PageResult<>(List.of(account), 1, 2, 10);
        PageResult<AdminAccountDTO> dtoPage = new PageResult<>(List.of(dto), 1, 2, 10);
        when(managementService.list(eq(AccountType.ADMIN), any(AdminAccountPageRequestDTO.class)))
                .thenReturn(entityPage);
        when(managementService.toPageDto(entityPage)).thenReturn(dtoPage);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AdminUserController(managementService)).build();

        mockMvc.perform(get("/api/accounts/admins/list")
                        .param("page", "2")
                        .param("page_size", "10")
                        .param("keyword", "admin")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk());

        ArgumentCaptor<AdminAccountPageRequestDTO> captor =
                ArgumentCaptor.forClass(AdminAccountPageRequestDTO.class);
        verify(managementService).list(eq(AccountType.ADMIN), captor.capture());
        assertThat(captor.getValue().getPage()).isEqualTo(2);
        assertThat(captor.getValue().getPageSize()).isEqualTo(10);
        assertThat(captor.getValue().getKeyword()).isEqualTo("admin");
        assertThat(captor.getValue().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void deleteAcceptsIdArray() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AdminUserController(managementService)).build();

        mockMvc.perform(post("/api/accounts/admins/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ids":["2","3"]}
                                """))
                .andExpect(status().isOk());

        verify(managementService).delete(AccountType.ADMIN, List.of(2L, 3L));
    }

    private AccountEntity account() {
        LocalDateTime now = LocalDateTime.now();
        return new AccountEntity(42L, "admin_user", "hash", null, null, null,
                AccountStatus.ACTIVE, 0, now, now);
    }
}
