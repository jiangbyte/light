package io.jiangbyte.github.types.page;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 跨业务复用的分页默认值和边界。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageConstants {
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}
