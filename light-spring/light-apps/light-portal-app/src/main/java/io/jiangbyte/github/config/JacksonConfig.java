package io.jiangbyte.github.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.PropertyNamingStrategies;

/**
 * Jackson 全局约定：字段 snake_case；整数/小数等数字一律序列化为字符串。
 */
@Configuration
public class JacksonConfig {

    /**
     * 定制 JsonMapper：命名策略与数字写字符串。
     */
    @Bean
    public JsonMapperBuilderCustomizer lightJsonMapperBuilderCustomizer() {
        return builder -> {
            // 1. 字段名统一下划线风格，避免驼峰泄漏到对外 JSON
            builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            // 2. 所有数字类型（含小数、BigDecimal）写出为字符串
            builder.enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS);
        };
    }

}
