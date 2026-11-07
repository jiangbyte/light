package io.jiangbyte.github.infrastructure.adapter.repository;

import io.jiangbyte.github.domain.template.adapter.repository.IXxxRepository;
import io.jiangbyte.github.domain.template.model.entity.XxxEntity;

/**
 * 仓储实现占位：演示端口如何落到基础设施。
 */
public class XxxRepository implements IXxxRepository {
    @Override
    public XxxEntity findById(String id) {
        // 1. 访问 DAO/缓存（示例直接构造）
        // 2. 转为领域对象返回
        XxxEntity entity = new XxxEntity();
        entity.setId(id);
        return entity;
    }
}
