package io.jiangbyte.github.infrastructure.account.auth.crypto;

import cn.hutool.core.util.StrUtil;
import io.jiangbyte.github.domain.account.adapter.port.IPasswordTransportCryptoPort;
import io.jiangbyte.github.infrastructure.account.auth.config.Sm2Settings;
import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;

/**
 * SM2 C1C3C2 密码传输加解密。
 */
@Slf4j
public class Sm2CryptoService implements IPasswordTransportCryptoPort {
    private static final String BC = BouncyCastleProvider.PROVIDER_NAME;
    private static final String CURVE = "sm2p256v1";

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final ECDomainParameters domainParameters;
    private final ECPrivateKeyParameters privateKeyParameters;
    private final SecureRandom secureRandom = new SecureRandom();

    static {
        if (Security.getProvider(BC) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public Sm2CryptoService(Sm2Settings settings) {
        boolean privateBlank = StrUtil.isBlank(settings.getPrivateKey());
        boolean publicBlank = StrUtil.isBlank(settings.getPublicKey());
        if (privateBlank && publicBlank) {
            KeyPair keyPair = generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
            domainParameters = domainParameters();
            privateKeyParameters = privateParameters();
            log.warn("未配置 SM2 密钥，当前实例已生成临时密钥；生产环境必须配置固定密钥对");
            return;
        }
        if (privateBlank || publicBlank) {
            throw new IllegalStateException("SM2 公钥和私钥必须同时配置");
        }
        privateKey = parsePrivateKey(settings.getPrivateKey());
        publicKey = parsePublicKey(settings.getPublicKey());
        domainParameters = domainParameters();
        privateKeyParameters = privateParameters();
    }

    @Override
    public String publicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    @Override
    public String publicKeyHex() {
        ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
        return HexFormat.of().formatHex(ecPublicKey.getQ().getEncoded(false));
    }

    @Override
    public String decrypt(String ciphertext) {
        if (StrUtil.isBlank(ciphertext)) {
            throw invalidCiphertext();
        }
        try {
            byte[] encrypted = Base64.getDecoder().decode(StrUtil.trim(ciphertext));
            if (encrypted.length == 0) {
                throw invalidCiphertext();
            }
            if (encrypted[0] != 0x04) {
                byte[] withPrefix = new byte[encrypted.length + 1];
                withPrefix[0] = 0x04;
                System.arraycopy(encrypted, 0, withPrefix, 1, encrypted.length);
                encrypted = withPrefix;
            }
            SM2Engine engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
            engine.init(false, privateKeyParameters);
            byte[] decrypted = engine.processBlock(encrypted, 0, encrypted.length);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw invalidCiphertext();
        }
    }

    String encrypt(String plaintext) {
        try {
            ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
            ECPublicKeyParameters parameters = new ECPublicKeyParameters(
                    ecPublicKey.getQ(), domainParameters);
            SM2Engine engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
            engine.init(true, new ParametersWithRandom(parameters, secureRandom));
            byte[] encrypted = engine.processBlock(
                    plaintext.getBytes(StandardCharsets.UTF_8), 0,
                    plaintext.getBytes(StandardCharsets.UTF_8).length);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new IllegalStateException("SM2 加密失败", ex);
        }
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", BC);
            generator.initialize(new ECGenParameterSpec(CURVE), secureRandom);
            return generator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("SM2 密钥生成失败", ex);
        }
    }

    private PrivateKey parsePrivateKey(String value) {
        try {
            byte[] encoded = Base64.getDecoder().decode(StrUtil.trim(value));
            return KeyFactory.getInstance("EC", BC)
                    .generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (Exception ex) {
            throw new IllegalStateException("SM2 私钥格式无效", ex);
        }
    }

    private PublicKey parsePublicKey(String value) {
        try {
            byte[] encoded = Base64.getDecoder().decode(StrUtil.trim(value));
            return KeyFactory.getInstance("EC", BC)
                    .generatePublic(new X509EncodedKeySpec(encoded));
        } catch (Exception ex) {
            throw new IllegalStateException("SM2 公钥格式无效", ex);
        }
    }

    private ECDomainParameters domainParameters() {
        var curve = GMNamedCurves.getByName(CURVE);
        return new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());
    }

    private ECPrivateKeyParameters privateParameters() {
        ECPrivateKey ecPrivateKey = (ECPrivateKey) privateKey;
        return new ECPrivateKeyParameters(ecPrivateKey.getD(), domainParameters);
    }

    private BizException invalidCiphertext() {
        return new BizException(ResponseCode.ILLEGAL_PARAMETER, "请求参数无效");
    }
}
