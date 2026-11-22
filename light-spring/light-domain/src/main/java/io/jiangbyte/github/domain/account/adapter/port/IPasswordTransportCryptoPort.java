package io.jiangbyte.github.domain.account.adapter.port;

/**
 * 密码传输加密端口，负责公钥发布和密文解密。
 */
public interface IPasswordTransportCryptoPort {
    String publicKeyBase64();

    String publicKeyHex();

    String decrypt(String ciphertext);
}
