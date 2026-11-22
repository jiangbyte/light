package io.jiangbyte.github.account.auth.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import cn.dev33.satoken.stp.StpLogic;
import io.jiangbyte.github.domain.account.adapter.port.IAccountSessionPort;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.infrastructure.account.auth.adapter.SaTokenAccountSessionAdapter;
import io.jiangbyte.github.infrastructure.account.auth.adapter.SaTokenJsonAdapter;
import io.jiangbyte.github.infrastructure.account.auth.config.SaTokenSettings;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Locale;

/**
 * 为当前应用初始化独立的 Sa-Token 登录域和 Redis 会话存储。
 */
public final class SaTokenAccountSessionFactory {
    private SaTokenAccountSessionFactory() {
    }

    /**
     * 使用应用自己的配置创建会话端口；登录域名由账户类型决定。
     */
    public static IAccountSessionPort create(SaTokenSettings settings,
                                             RedisConnectionFactory connectionFactory,
                                             AccountType accountType) {
        String logicName = accountType.name().toLowerCase(Locale.ROOT);

        SaManager.setConfig(settings.toConfig());

        SaTokenDaoForRedisTemplate dao = new SaTokenDaoForRedisTemplate();
        dao.init(connectionFactory);
        SaManager.setSaTokenDao(dao);
        SaManager.setSaJsonTemplate(new SaTokenJsonAdapter());

        StpLogic loginLogic = new StpLogic(logicName);
        SaManager.putStpLogic(loginLogic);
        return new SaTokenAccountSessionAdapter(accountType, loginLogic);
    }
}
