package io.jiangbyte.github.api.portal.account.auth.dto;

import lombok.Data;

/**
 * Portal 登录成功响应。
 */
@Data
public class PortalLoginResponseDTO {
    private String tokenName;
    private String tokenValue;
    private long tokenTimeout;
    private long activeTimeout;
    private PortalAccountDTO account;
}
