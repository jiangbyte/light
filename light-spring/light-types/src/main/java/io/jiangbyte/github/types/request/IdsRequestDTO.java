package io.jiangbyte.github.types.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 通用批量 ID 请求；单个操作时数组只包含一个 ID。
 */
@Data
public class IdsRequestDTO {
    @NotEmpty(message = "不能为空")
    @Size(max = 100, message = "单次最多操作 100 个资源")
    private List<@NotNull(message = "不能为空") @Positive(message = "必须大于 0") Long> ids;
}
