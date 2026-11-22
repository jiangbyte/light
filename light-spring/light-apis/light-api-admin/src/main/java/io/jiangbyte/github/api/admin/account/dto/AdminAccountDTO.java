package io.jiangbyte.github.api.admin.account.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端账户摘要；永远不包含密码哈希。
 */
@Data
public class AdminAccountDTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
