package io.jiangbyte.github.api.portal;

import io.jiangbyte.github.api.portal.dto.XxxRequestDTO;
import io.jiangbyte.github.api.portal.dto.XxxResponseDTO;

/**
 * Portal契约占位接口；落地时改名并由 trigger 实现。
 */
public interface IXxxService {
    XxxResponseDTO query(XxxRequestDTO request);
}
