package io.jiangbyte.github.trigger.portal.account.auth.http;

import io.jiangbyte.github.api.portal.account.auth.IPortalAuthService;
import io.jiangbyte.github.api.portal.account.auth.dto.AccountLoginRequestDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.PortalAccountDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.PortalLoginResponseDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.TransportKeyDTO;
import io.jiangbyte.github.trigger.portal.account.auth.application.PortalAuthApplicationService;
import io.jiangbyte.github.types.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Portal 登录入口，只使用 GET/POST 且不包含路径变量。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PortalAuthController implements IPortalAuthService {
    private final PortalAuthApplicationService service;

    @Override
    @GetMapping("/transport-key")
    public Response<TransportKeyDTO> transportKey() {
        return service.transportKey();
    }

    @Override
    @PostMapping("/login/username")
    public Response<PortalLoginResponseDTO> loginByUsername(
            @Valid @RequestBody AccountLoginRequestDTO request) {
        return service.loginByUsername(request);
    }

    @Override
    @PostMapping("/login/email")
    public Response<PortalLoginResponseDTO> loginByEmail(
            @Valid @RequestBody AccountLoginRequestDTO request) {
        return service.loginByEmail(request);
    }

    @Override
    @PostMapping("/login/phone")
    public Response<PortalLoginResponseDTO> loginByPhone(
            @Valid @RequestBody AccountLoginRequestDTO request) {
        return service.loginByPhone(request);
    }

    @Override
    @GetMapping("/me")
    public Response<PortalAccountDTO> me() {
        return service.me();
    }

    @Override
    @PostMapping("/logout")
    public Response<Void> logout() {
        return service.logout();
    }
}
