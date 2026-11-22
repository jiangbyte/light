package io.jiangbyte.github.types.page;

import java.util.List;

/**
 * 跨业务复用的分页结果。
 */
public record PageResult<T>(List<T> items, long total, int page, int pageSize) {
}
