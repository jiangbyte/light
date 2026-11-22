package io.jiangbyte.github.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 两端共享的 Jackson 约定：字段 snake_case；数字字符串化；时间统一为 yyyy-MM-dd HH:mm:ss。
 */
@Configuration
public class JacksonConfig {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 定制 JsonMapper：命名策略、数字写字符串和统一时间格式。
     */
    @Bean
    public JsonMapperBuilderCustomizer lightJsonMapperBuilderCustomizer() {
        return builder -> {
            // 1. 字段名统一下划线风格，避免驼峰泄漏到对外 JSON
            builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            // 2. 所有数字类型（含小数、BigDecimal）写出为字符串
            builder.enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS);
            // 3. 所有 LocalDateTime 统一使用 yyyy-MM-dd HH:mm:ss 读写
            builder.addModule(new SimpleModule("light-local-date-time")
                    .addSerializer(LocalDateTime.class, new LocalDateTimeStringSerializer())
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeStringDeserializer()));
        };
    }

    /**
     * 秒精度本地日期时间序列化器。
     */
    private static final class LocalDateTimeStringSerializer extends ValueSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator generator, SerializationContext context)
                throws JacksonException {
            if (value == null) {
                generator.writeNull();
                return;
            }
            generator.writeString(value.format(DATE_TIME_FORMATTER));
        }
    }

    /**
     * 秒精度本地日期时间反序列化器。
     */
    private static final class LocalDateTimeStringDeserializer extends ValueDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
                throws JacksonException {
            String value = parser.getString();
            try {
                return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException ex) {
                return context.reportInputMismatch(LocalDateTime.class,
                        "时间格式必须为 yyyy-MM-dd HH:mm:ss");
            }
        }
    }

}
