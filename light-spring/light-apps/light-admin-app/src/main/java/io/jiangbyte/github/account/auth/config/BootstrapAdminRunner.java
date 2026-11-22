package io.jiangbyte.github.account.auth.config;

import io.jiangbyte.github.domain.account.adapter.repository.IAccountRepository;
import io.jiangbyte.github.domain.account.model.valobj.AccountType;
import io.jiangbyte.github.domain.account.service.AccountDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 仅当管理员表为空时，从环境配置创建首个管理员。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BootstrapAdminRunner implements ApplicationRunner {
    private final IAccountRepository accountRepository;
    private final AccountDomainService accountDomainService;
    @Value("${light.bootstrap-admin.username:}")
    private String username;
    @Value("${light.bootstrap-admin.password:}")
    private String password;
    @Value("${light.bootstrap-admin.email:}")
    private String email;
    @Value("${light.bootstrap-admin.phone:}")
    private String phone;

    @Override
    public void run(ApplicationArguments args) {
        // 1. 已有管理员时不覆盖现有账户
        if (accountRepository.existsAny(AccountType.ADMIN)) {
            return;
        }

        // 2. 环境变量不完整时跳过初始化并明确提示
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            log.warn("管理员表为空，且未配置 LIGHT_BOOTSTRAP_ADMIN_USERNAME/PASSWORD，跳过首个管理员初始化");
            return;
        }

        // 3. 复用正常创建规则生成 BCrypt 密码和账户记录
        accountDomainService.create(AccountType.ADMIN, username, password, "管理员",
                email, phone, "ACTIVE");
        log.info("已根据环境变量初始化首个管理员: {}", username);
    }
}
