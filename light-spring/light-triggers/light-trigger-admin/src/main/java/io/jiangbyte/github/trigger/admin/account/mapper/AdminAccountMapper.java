package io.jiangbyte.github.trigger.admin.account.mapper;

import io.jiangbyte.github.api.admin.account.auth.dto.AdminLoginResponseDTO;
import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountLoginResult;
import io.jiangbyte.github.types.page.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Admin 账户领域对象到 API DTO 的编译期映射。
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AdminAccountMapper {
    AdminAccountDTO toDto(AccountEntity account);

    List<AdminAccountDTO> toDtoList(List<AccountEntity> accounts);

    PageResult<AdminAccountDTO> toPageDto(PageResult<AccountEntity> page);

    @Mapping(target = "tokenName", source = "token.tokenName")
    @Mapping(target = "tokenValue", source = "token.tokenValue")
    @Mapping(target = "tokenTimeout", source = "token.tokenTimeout")
    @Mapping(target = "activeTimeout", source = "token.activeTimeout")
    AdminLoginResponseDTO toLoginResponse(AccountLoginResult result);

}
