package io.jiangbyte.github.trigger.admin.account.auth.http;

import io.jiangbyte.github.api.admin.account.auth.IAdminAuthService;
import io.jiangbyte.github.api.admin.account.auth.dto.AccountLoginRequestDTO;
import io.jiangbyte.github.api.admin.account.auth.dto.AdminLoginResponseDTO;
import io.jiangbyte.github.api.admin.account.auth.dto.TransportKeyDTO;
import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import io.jiangbyte.github.trigger.admin.account.auth.application.AdminAuthApplicationService;
import io.jiangbyte.github.types.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端登录入口，只使用 GET/POST 且不包含路径变量。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AdminAuthController implements IAdminAuthService {
    private final AdminAuthApplicationService service;

    @Override
    @GetMapping("/transport-key")
    public Response<TransportKeyDTO> transportKey() {
        return service.transportKey();
    }

    @Override
    @PostMapping("/login/username")
    public Response<AdminLoginResponseDTO> loginByUsername(
            @Valid @RequestBody AccountLoginRequestDTO request) {
        return service.loginByUsername(request);
    }

    @Override
    @PostMapping("/login/email")
    public Response<AdminLoginResponseDTO> loginByEmail(
            @Valid @RequestBody AccountLoginRequestDTO request) {
        return service.loginByEmail(request);
    }

    @Override
    @PostMapping("/login/phone")
    public Response<AdminLoginResponseDTO> loginByPhone(
            @Valid @RequestBody AccountLoginRequestDTO request) {
        return service.loginByPhone(request);
    }

    @Override
    @GetMapping("/me")
    public Response<AdminAccountDTO> me() {
        return service.me();
    }

    @Override
    @PostMapping("/logout")
    public Response<Void> logout() {
        return service.logout();
    }
}
