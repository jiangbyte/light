package io.jiangbyte.github.infrastructure.account.auth.config;

import lombok.Data;

/**
 * SM2 密钥配置模型；实际值由各应用 YAML 或环境变量提供。
 */
@Data
public class Sm2Settings {
    private String privateKey;
    private String publicKey;
}
