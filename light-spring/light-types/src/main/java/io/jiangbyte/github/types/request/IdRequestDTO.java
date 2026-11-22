package io.jiangbyte.github.types.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 通用单个 ID 请求。
 */
@Data
public class IdRequestDTO {
    @NotNull(message = "不能为空")
    @Positive(message = "必须大于 0")
    private Long id;
}
