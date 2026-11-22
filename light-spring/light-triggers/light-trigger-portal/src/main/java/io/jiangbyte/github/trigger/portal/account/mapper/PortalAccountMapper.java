package io.jiangbyte.github.trigger.portal.account.mapper;

import io.jiangbyte.github.api.portal.account.auth.dto.PortalAccountDTO;
import io.jiangbyte.github.api.portal.account.auth.dto.PortalLoginResponseDTO;
import io.jiangbyte.github.domain.account.model.entity.AccountEntity;
import io.jiangbyte.github.domain.account.model.valobj.AccountLoginResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Portal 账户领域对象到 API DTO 的编译期映射。
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PortalAccountMapper {
    PortalAccountDTO toDto(AccountEntity account);

    @Mapping(target = "tokenName", source = "token.tokenName")
    @Mapping(target = "tokenValue", source = "token.tokenValue")
    @Mapping(target = "tokenTimeout", source = "token.tokenTimeout")
    @Mapping(target = "activeTimeout", source = "token.activeTimeout")
    PortalLoginResponseDTO toLoginResponse(AccountLoginResult result);

}
