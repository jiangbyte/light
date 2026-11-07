package io.jiangbyte.github.domain.template.service;

import io.jiangbyte.github.domain.template.adapter.repository.IXxxRepository;
import io.jiangbyte.github.domain.template.model.entity.XxxEntity;

/**
 * 领域服务占位；注入真实端口后编写业务步骤。
 */
public class XxxService {
    private final IXxxRepository repository;

    public XxxService(IXxxRepository repository) {
        this.repository = repository;
    }

    public XxxEntity load(String id) {
        // 1. 读取聚合/实体
        // 2. 执行规则校验（示例省略）
        return repository.findById(id);
    }
}
