package io.jiangbyte.github.api.admin.account.management.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 物理批量删除账户请求；单个删除时数组只包含一个 ID。
 */
@Data
public class DeleteAccountRequestDTO {
    @NotEmpty(message = "不能为空")
    @Size(max = 100, message = "单次最多删除 100 个账户")
    private List<@NotNull(message = "不能为空") @Positive(message = "必须大于 0") Long> ids;
}
