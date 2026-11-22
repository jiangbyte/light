package io.jiangbyte.github.api.admin.account.management.dto;

import io.jiangbyte.github.types.page.PageConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Admin 账户分页查询参数对象。
 */
@Data
public class AdminAccountPageRequestDTO {
    @Positive(message = "必须大于 0")
    private Integer page;
    @Positive(message = "必须大于 0")
    @Max(value = PageConstants.MAX_PAGE_SIZE,
            message = "不能超过 " + PageConstants.MAX_PAGE_SIZE)
    private Integer pageSize;
    @Size(max = 128, message = "长度不能超过 128")
    private String keyword;
    private String status;

}
