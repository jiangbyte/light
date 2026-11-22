package io.jiangbyte.github.infrastructure.account.auth.adapter;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import io.jiangbyte.github.domain.account.adapter.port.IAccountSessionPort;
import io.jiangbyte.github.domain.account.model.valobj.AccountToken;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;

/**
 * 单个应用登录域的 Sa-Token 不透明令牌适配器。
 */
public class SaTokenAccountSessionAdapter implements IAccountSessionPort {
    private static final String TOKEN_VERSION_SESSION_KEY = "tokenVersion";

    private final AccountType accountType;
    private final StpLogic stpLogic;

    public SaTokenAccountSessionAdapter(AccountType accountType, StpLogic stpLogic) {
        this.accountType = accountType;
        this.stpLogic = stpLogic;
    }

    @Override
    public AccountToken login(AccountType type, long accountId, int tokenVersion) {
        // 1. 在目标登录域创建独立多会话令牌
        StpLogic logic = logic(type);
        logic.login(accountId);

        // 2. 将账户令牌版本保存在当前令牌会话中
        logic.getTokenSession().set(TOKEN_VERSION_SESSION_KEY, tokenVersion);

        // 3. 读取令牌原值及绝对/空闲超时时间
        SaTokenInfo info = logic.getTokenInfo();
        return new AccountToken(info.getTokenName(), info.getTokenValue(),
                info.getTokenTimeout(), info.getTokenActiveTimeout());
    }

    @Override
    public long currentAccountId(AccountType type) {
        // 1. 校验目标登录域是否已登录
        StpLogic logic = logic(type);
        logic.checkLogin();
        // 2. 当前令牌中的账户 ID 作为可靠身份来源
        return logic.getLoginIdAsLong();
    }

    @Override
    public int currentTokenVersion(AccountType type) {
        Object value = logic(type).getTokenSession().get(TOKEN_VERSION_SESSION_KEY);
        return value instanceof Number number ? number.intValue() : 0;
    }

    @Override
    public void logout(AccountType type) {
        logic(type).logout();
    }

    @Override
    public void kickout(AccountType type, long accountId) {
        if (type == accountType) {
            stpLogic.kickout(accountId);
        }
    }

    private StpLogic logic(AccountType type) {
        if (type != accountType) {
            throw new IllegalStateException("当前应用不支持登录域: " + type);
        }
        return stpLogic;
    }
}
