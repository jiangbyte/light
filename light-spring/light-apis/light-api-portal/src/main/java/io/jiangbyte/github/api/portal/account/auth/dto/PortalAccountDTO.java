package io.jiangbyte.github.api.portal.account.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Portal 当前账户摘要，不暴露管理端运营字段。
 */
@Data
public class PortalAccountDTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}
