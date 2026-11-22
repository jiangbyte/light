package io.jiangbyte.github.trigger.admin.account.auth.application;

import io.jiangbyte.github.api.admin.account.auth.dto.AccountLoginRequestDTO;
import io.jiangbyte.github.api.admin.account.auth.dto.AdminLoginResponseDTO;
import io.jiangbyte.github.api.admin.account.auth.dto.TransportKeyDTO;
import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import io.jiangbyte.github.domain.account.adapter.port.IPasswordTransportCryptoPort;
import io.jiangbyte.github.domain.account.model.valobj.AccountLoginResult;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.model.valobj.LoginMethod;
import io.jiangbyte.github.domain.account.service.AccountDomainService;
import io.jiangbyte.github.trigger.admin.account.mapper.AdminAccountMapper;
import io.jiangbyte.github.types.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 管理端登录用例编排与 API DTO 映射。
 */
@Service
@RequiredArgsConstructor
public class AdminAuthApplicationService {
    private final AccountDomainService accountDomainService;
    private final AdminAccountMapper accountMapper;
    private final IPasswordTransportCryptoPort passwordCryptoPort;

    public Response<TransportKeyDTO> transportKey() {
        TransportKeyDTO dto = new TransportKeyDTO();
        dto.setKey(passwordCryptoPort.publicKeyHex());
        return Response.success(dto);
    }

    public Response<AdminLoginResponseDTO> loginByUsername(AccountLoginRequestDTO request) {
        return login(LoginMethod.USERNAME, request);
    }

    public Response<AdminLoginResponseDTO> loginByEmail(AccountLoginRequestDTO request) {
        return login(LoginMethod.EMAIL, request);
    }

    public Response<AdminLoginResponseDTO> loginByPhone(AccountLoginRequestDTO request) {
        return login(LoginMethod.PHONE, request);
    }

    public Response<AdminAccountDTO> me() {
        return Response.success(accountMapper.toDto(accountDomainService.current(AccountType.ADMIN)));
    }

    public Response<Void> logout() {
        accountDomainService.logout(AccountType.ADMIN);
        return Response.success();
    }

    private Response<AdminLoginResponseDTO> login(LoginMethod method, AccountLoginRequestDTO request) {
        // 1. 在 Admin 登录域校验指定标识和密码
        AccountLoginResult result = accountDomainService.login(AccountType.ADMIN, method,
                request.getIdentifier(), passwordCryptoPort.decrypt(request.getPassword()));

        // 2. 仅返回令牌和脱敏账户摘要，不传播密码哈希
        return Response.success(accountMapper.toLoginResponse(result));
    }
}
