package io.jiangbyte.github.trigger.admin.account.management.http;

import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import io.jiangbyte.github.api.admin.account.management.IAdminAccountService;
import io.jiangbyte.github.api.admin.account.management.dto.AdminAccountPageRequestDTO;
import io.jiangbyte.github.api.admin.account.management.dto.CreateAdminAccountRequestDTO;
import io.jiangbyte.github.api.admin.account.management.dto.DeleteAccountRequestDTO;
import io.jiangbyte.github.api.admin.account.management.dto.UpdateAdminAccountRequestDTO;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.trigger.admin.account.management.application.AdminAccountManagementService;
import io.jiangbyte.github.types.response.Response;
import io.jiangbyte.github.types.page.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 Admin 账户 CRUD，全部接口只使用 GET/POST。
 */
@RestController
@RequestMapping("/api/accounts/admins")
@RequiredArgsConstructor
public class AdminUserController implements IAdminAccountService {
    private final AdminAccountManagementService service;

    @Override
    @GetMapping("/list")
    public Response<PageResult<AdminAccountDTO>> list(
            @Valid @ModelAttribute AdminAccountPageRequestDTO request) {
        return Response.success(service.toPageDto(service.list(AccountType.ADMIN, request)));
    }

    @Override
    @GetMapping("/detail")
    public Response<AdminAccountDTO> detail(@RequestParam Long id) {
        return Response.success(service.toDto(service.detail(AccountType.ADMIN, id)));
    }

    @Override
    @PostMapping("/create")
    public Response<AdminAccountDTO> create(@Valid @RequestBody CreateAdminAccountRequestDTO request) {
        return Response.success(service.toDto(service.create(AccountType.ADMIN, request)));
    }

    @Override
    @PostMapping("/update")
    public Response<AdminAccountDTO> update(@Valid @RequestBody UpdateAdminAccountRequestDTO request) {
        return Response.success(service.toDto(service.update(AccountType.ADMIN, request)));
    }

    @Override
    @PostMapping("/delete")
    public Response<Void> delete(@Valid @RequestBody DeleteAccountRequestDTO request) {
        service.delete(AccountType.ADMIN, request.getIds());
        return Response.success();
    }
}
