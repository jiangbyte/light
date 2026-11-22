package io.jiangbyte.github.api.portal.account.auth;

import io.jiangbyte.github.api.portal.account.auth.dto.AccountLoginRequestDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.PortalAccountDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.PortalLoginResponseDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.TransportKeyDTO;
import io.jiangbyte.github.types.response.Response;

/**
 * Portal 登录、当前用户和退出登录契约。
 */
public interface IPortalAuthService {
    Response<TransportKeyDTO> transportKey();

    Response<PortalLoginResponseDTO> loginByUsername(AccountLoginRequestDTO request);

    Response<PortalLoginResponseDTO> loginByEmail(AccountLoginRequestDTO request);

    Response<PortalLoginResponseDTO> loginByPhone(AccountLoginRequestDTO request);

    Response<PortalAccountDTO> me();

    Response<Void> logout();
}
