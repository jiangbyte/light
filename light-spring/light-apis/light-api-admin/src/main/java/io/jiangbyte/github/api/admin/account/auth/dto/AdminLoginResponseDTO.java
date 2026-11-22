package io.jiangbyte.github.api.admin.account.auth.dto;

import io.jiangbyte.github.api.admin.account.dto.AdminAccountDTO;
import lombok.Data;

/**
 * 管理端登录成功响应。
 */
@Data
public class AdminLoginResponseDTO {
    private String tokenName;
    private String tokenValue;
    private long tokenTimeout;
    private long activeTimeout;
    private AdminAccountDTO account;
}
