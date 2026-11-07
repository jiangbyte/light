package io.jiangbyte.github.infrastructure.adapter.port;

import io.jiangbyte.github.domain.template.adapter.port.IXxxPort;

/**
 * 出站端口实现占位。
 */
public class XxxPort implements IXxxPort {
    @Override
    public String fetchExternal(String id) {
        return "external-" + id;
    }
}
