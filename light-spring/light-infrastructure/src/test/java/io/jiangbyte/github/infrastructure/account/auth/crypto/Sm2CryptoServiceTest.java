package io.jiangbyte.github.infrastructure.account.auth.crypto;

import io.jiangbyte.github.infrastructure.account.auth.config.Sm2Settings;
import io.jiangbyte.github.types.exception.BizException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Sm2CryptoServiceTest {
    @Test
    void encryptsAndDecryptsPasswordWithSm2C1C3C2() {
        Sm2CryptoService cryptoService = new Sm2CryptoService(new Sm2Settings());
        String plaintext = "Admin@123456";

        String ciphertext = cryptoService.encrypt(plaintext);

        assertThat(cryptoService.decrypt(ciphertext)).isEqualTo(plaintext);
        assertThat(cryptoService.publicKeyBase64()).isNotBlank();
        assertThat(cryptoService.publicKeyHex()).startsWith("04");
    }

    @Test
    void rejectsPlaintextOrInvalidCiphertext() {
        Sm2CryptoService cryptoService = new Sm2CryptoService(new Sm2Settings());

        assertThatThrownBy(() -> cryptoService.decrypt("Admin@123456"))
                .isInstanceOf(BizException.class)
                .extracting("code")
                .isEqualTo("0400");
        assertThatThrownBy(() -> cryptoService.decrypt("not-base64"))
                .isInstanceOf(BizException.class)
                .extracting("code")
                .isEqualTo("0400");
    }
}
