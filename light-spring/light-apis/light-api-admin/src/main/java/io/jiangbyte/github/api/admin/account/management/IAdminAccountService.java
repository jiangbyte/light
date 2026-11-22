package io.jiangbyte.github.api.admin.account.management;

import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import io.jiangbyte.github.api.admin.account.management.dto.AdminAccountPageRequestDTO;
import io.jiangbyte.github.api.admin.account.management.dto.CreateAdminAccountRequestDTO;
import io.jiangbyte.github.api.admin.account.management.dto.UpdateAdminAccountRequestDTO;
import io.jiangbyte.github.types.request.IdsRequestDTO;
import io.jiangbyte.github.types.response.Response;
import io.jiangbyte.github.types.page.PageResult;

/**
 * 管理端账户 CRUD 契约。
 */
public interface IAdminAccountService {
    Response<PageResult<AdminAccountDTO>> page(AdminAccountPageRequestDTO request);

    Response<AdminAccountDTO> detail(Long id);

    Response<AdminAccountDTO> create(CreateAdminAccountRequestDTO request);

    Response<AdminAccountDTO> update(UpdateAdminAccountRequestDTO request);

    Response<Void> delete(IdsRequestDTO request);
}
