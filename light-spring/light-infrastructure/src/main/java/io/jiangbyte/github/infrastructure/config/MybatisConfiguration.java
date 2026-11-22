package io.jiangbyte.github.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描基础设施模块中的 MyBatis-Plus DAO。
 */
@Configuration
@MapperScan({
        "io.jiangbyte.github.infrastructure.account.persistence.dao",
        "io.jiangbyte.github.infrastructure.content.announcement.persistence.dao"
})
public class MybatisConfiguration {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 1. 创建 MyBatis-Plus 插件链
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 2. 按 MySQL 方言执行 count 与分页 SQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
