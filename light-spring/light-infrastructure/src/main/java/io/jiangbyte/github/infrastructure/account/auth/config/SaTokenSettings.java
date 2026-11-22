package io.jiangbyte.github.infrastructure.account.auth.config;

import cn.dev33.satoken.config.SaTokenConfig;
import lombok.Data;

/**
 * Sa-Token 可绑定配置模型；具体值由各应用 YAML 提供。
 */
@Data
public class SaTokenSettings {
    private String tokenName = "Authorization";
    private String tokenPrefix = "";
    private String tokenStyle = "random-64";
    private long timeout = 7 * 24 * 60 * 60L;
    private long activeTimeout = 2 * 60 * 60L;
    private boolean autoRenew = true;
    private boolean concurrent = true;
    private boolean share = false;
    private int maxLoginCount = -1;
    private boolean readHeader = true;
    private boolean readBody = false;
    private boolean readCookie = false;
    private boolean writeHeader = false;
    private boolean print = false;
    private boolean log = false;

    public SaTokenConfig toConfig() {
        return new SaTokenConfig()
                .setTokenName(tokenName)
                .setTokenPrefix(tokenPrefix)
                .setTokenStyle(tokenStyle)
                .setTimeout(timeout)
                .setActiveTimeout(activeTimeout)
                .setAutoRenew(autoRenew)
                .setIsConcurrent(concurrent)
                .setIsShare(share)
                .setMaxLoginCount(maxLoginCount)
                .setIsReadHeader(readHeader)
                .setIsReadBody(readBody)
                .setIsReadCookie(readCookie)
                .setIsWriteHeader(writeHeader)
                .setIsPrint(print)
                .setIsLog(log);
    }
}
