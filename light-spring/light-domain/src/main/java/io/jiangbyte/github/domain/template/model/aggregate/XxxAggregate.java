package io.jiangbyte.github.domain.template.model.aggregate;

import io.jiangbyte.github.domain.template.model.entity.XxxEntity;

/**
 * 聚合占位样例；在此维护一致性边界内的协作对象。
 */
public class XxxAggregate {
    private XxxEntity entity;

    public XxxEntity getEntity() { return entity; }
    public void setEntity(XxxEntity entity) { this.entity = entity; }
}
