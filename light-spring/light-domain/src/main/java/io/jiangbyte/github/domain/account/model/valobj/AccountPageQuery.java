package io.jiangbyte.github.domain.account.model.valobj;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;

/**
 * 账户分页查询条件，集中处理默认值、规范化与参数校验。
 */
public record AccountPageQuery(int page, int pageSize, String keyword, AccountStatus status) {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    public AccountPageQuery {
        if (page < 1 || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new BizException(ResponseCode.ILLEGAL_PARAMETER,
                    "page 必须大于 0，pageSize 必须在 1 到 100 之间");
        }
        keyword = StrUtil.trimToNull(keyword);
    }

    /**
     * 从 API 查询参数创建领域查询对象。
     */
    public static AccountPageQuery of(Integer page, Integer pageSize, String keyword, String status) {
        return new AccountPageQuery(
                page == null ? DEFAULT_PAGE : page,
                pageSize == null ? DEFAULT_PAGE_SIZE : pageSize,
                keyword,
                AccountStatus.parseNullable(status)
        );
    }
}
