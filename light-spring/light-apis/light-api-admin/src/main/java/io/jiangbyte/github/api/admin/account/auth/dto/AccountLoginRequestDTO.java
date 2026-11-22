package io.jiangbyte.github.api.admin.account.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理端登录请求；password 必须是 SM2 C1C3C2 Base64 密文。
 */
@Data
public class AccountLoginRequestDTO {
    @NotBlank(message = "不能为空")
    private String identifier;
    @NotBlank(message = "不能为空")
    private String password;
}
