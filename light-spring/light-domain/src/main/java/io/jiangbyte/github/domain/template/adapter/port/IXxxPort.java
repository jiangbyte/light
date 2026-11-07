package io.jiangbyte.github.domain.template.adapter.port;

/**
 * 外部依赖端口占位；由 infrastructure 提供实现。
 */
public interface IXxxPort {
    String fetchExternal(String id);
}
