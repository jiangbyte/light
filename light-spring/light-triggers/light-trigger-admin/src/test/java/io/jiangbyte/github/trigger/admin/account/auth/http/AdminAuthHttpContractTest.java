package io.jiangbyte.github.trigger.admin.account.auth.http;

import io.jiangbyte.github.api.admin.account.auth.dto.AdminLoginResponseDTO;
import io.jiangbyte.github.trigger.admin.account.auth.application.AdminAuthApplicationService;
import io.jiangbyte.github.types.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminAuthHttpContractTest {
    @Mock
    private AdminAuthApplicationService authService;

    @Test
    void loginUsesPostBodyAndNoPathParameter() throws Exception {
        when(authService.loginByUsername(any())).thenReturn(Response.success(new AdminLoginResponseDTO()));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AdminAuthController(authService)).build();

        mockMvc.perform(post("/api/auth/login/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifier":"admin","password":"password123"}
                                """))
                .andExpect(status().isOk());

        verify(authService).loginByUsername(any());
    }

    @Test
    void putMethodIsNotExposed() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AdminAuthController(authService)).build();

        mockMvc.perform(put("/api/auth/me")).andExpect(status().isMethodNotAllowed());
    }

}
