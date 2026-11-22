package io.jiangbyte.github.api.admin.account.auth;

import io.jiangbyte.github.api.admin.account.auth.dto.AccountLoginRequestDTO;
import io.jiangbyte.github.api.admin.account.auth.dto.AdminLoginResponseDTO;
import io.jiangbyte.github.api.admin.account.auth.dto.TransportKeyDTO;
import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import io.jiangbyte.github.types.response.Response;

/**
 * 管理端登录、当前用户和退出登录契约。
 */
public interface IAdminAuthService {
    Response<TransportKeyDTO> transportKey();

    Response<AdminLoginResponseDTO> loginByUsername(AccountLoginRequestDTO request);

    Response<AdminLoginResponseDTO> loginByEmail(AccountLoginRequestDTO request);

    Response<AdminLoginResponseDTO> loginByPhone(AccountLoginRequestDTO request);

    Response<AdminAccountDTO> me();

    Response<Void> logout();
}
