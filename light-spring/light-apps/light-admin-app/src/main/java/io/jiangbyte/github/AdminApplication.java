package io.jiangbyte.github;

import io.jiangbyte.github.config.StartupAccessUrlPrinter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 管理端启动入口。
 */
@Slf4j
@SpringBootApplication
public class AdminApplication {
    public static void main(String[] args) {
        // 1. 启动 Spring 容器
        ConfigurableApplicationContext context = SpringApplication.run(AdminApplication.class, args);
        // 2. 打印本地 / 局域网 / API Doc 访问地址
        StartupAccessUrlPrinter.print(context.getEnvironment());
    }
}
