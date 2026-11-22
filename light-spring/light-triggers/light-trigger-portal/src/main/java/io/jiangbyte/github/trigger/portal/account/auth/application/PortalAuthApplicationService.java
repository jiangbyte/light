package io.jiangbyte.github.trigger.portal.account.auth.application;

import io.jiangbyte.github.api.portal.account.auth.dto.AccountLoginRequestDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.PortalAccountDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.PortalLoginResponseDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.TransportKeyDTO;
import io.jiangbyte.github.domain.account.adapter.port.IPasswordTransportCryptoPort;
import io.jiangbyte.github.domain.account.model.valobj.AccountLoginResult;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.model.valobj.LoginMethod;
import io.jiangbyte.github.domain.account.service.AccountDomainService;
import io.jiangbyte.github.trigger.portal.account.mapper.PortalAccountMapper;
import io.jiangbyte.github.types.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Portal 登录用例编排与 API DTO 映射。
 */
@Service
@RequiredArgsConstructor
public class PortalAuthApplicationService {
    private final AccountDomainService accountDomainService;
    private final PortalAccountMapper accountMapper;
    private final IPasswordTransportCryptoPort passwordCryptoPort;

    public Response<TransportKeyDTO> transportKey() {
        TransportKeyDTO dto = new TransportKeyDTO();
        dto.setKey(passwordCryptoPort.publicKeyHex());
        return Response.success(dto);
    }

    public Response<PortalLoginResponseDTO> loginByUsername(AccountLoginRequestDTO request) {
        return login(LoginMethod.USERNAME, request);
    }

    public Response<PortalLoginResponseDTO> loginByEmail(AccountLoginRequestDTO request) {
        return login(LoginMethod.EMAIL, request);
    }

    public Response<PortalLoginResponseDTO> loginByPhone(AccountLoginRequestDTO request) {
        return login(LoginMethod.PHONE, request);
    }

    public Response<PortalAccountDTO> me() {
        return Response.success(accountMapper.toDto(accountDomainService.current(AccountType.PORTAL)));
    }

    public Response<Void> logout() {
        accountDomainService.logout(AccountType.PORTAL);
        return Response.success();
    }

    private Response<PortalLoginResponseDTO> login(LoginMethod method, AccountLoginRequestDTO request) {
        // 1. 在 Portal 登录域校验标识和密码
        AccountLoginResult result = accountDomainService.login(AccountType.PORTAL, method,
                request.getIdentifier(), passwordCryptoPort.decrypt(request.getPassword()));

        // 2. 只向 Portal 暴露令牌和精简账户信息
        return Response.success(accountMapper.toLoginResponse(result));
    }
}
