package io.jiangbyte.github.domain.template.adapter.repository;

import io.jiangbyte.github.domain.template.model.entity.XxxEntity;

/**
 * 仓储端口占位；由 infrastructure 提供实现。
 */
public interface IXxxRepository {
    XxxEntity findById(String id);
}
