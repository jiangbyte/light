package io.jiangbyte.github.api.admin.account.management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新账户请求；password 为可选 SM2 C1C3C2 Base64 密文。
 */
@Data
public class UpdateAdminAccountRequestDTO {
    @NotNull(message = "不能为空")
    private Long id;
    @Size(min = 3, max = 32, message = "长度必须在 3 到 32 之间")
    private String username;
    @Size(max = 2048, message = "密文长度不能超过 2048")
    private String password;
    @Size(max = 64, message = "长度不能超过 64")
    private String nickname;
    @Size(max = 128, message = "长度不能超过 128")
    private String email;
    @Size(max = 32, message = "长度不能超过 32")
    private String phone;
    private String status;
}
