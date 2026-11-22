package io.jiangbyte.github.trigger.portal.account.auth.http;

import io.jiangbyte.github.api.portal.account.auth.dto.PortalLoginResponseDTO;
import io.jiangbyte.github.trigger.portal.account.auth.application.PortalAuthApplicationService;
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
class PortalAuthHttpContractTest {
    @Mock
    private PortalAuthApplicationService authService;

    @Test
    void portalLoginUsesPostBody() throws Exception {
        when(authService.loginByEmail(any())).thenReturn(Response.success(new PortalLoginResponseDTO()));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PortalAuthController(authService)).build();

        mockMvc.perform(post("/api/auth/login/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identifier":"user@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk());

        verify(authService).loginByEmail(any());
    }

    @Test
    void portalDoesNotExposePutMethod() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PortalAuthController(authService)).build();

        mockMvc.perform(put("/api/auth/me")).andExpect(status().isMethodNotAllowed());
    }
}
