package io.jiangbyte.github.api.admin;

import io.jiangbyte.github.api.admin.dto.XxxRequestDTO;
import io.jiangbyte.github.api.admin.dto.XxxResponseDTO;

/**
 * 管理端契约占位接口；落地时改名并由 trigger 实现。
 */
public interface IXxxService {
    XxxResponseDTO query(XxxRequestDTO request);
}
