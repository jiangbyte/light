package io.jiangbyte.github.infrastructure.account.auth.adapter;

import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.json.SaJsonTemplate;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * 使用 Boot 4 自带的 Jackson 3 为 Sa-Token Redis 会话提供 JSON 编解码。
 */
public class SaTokenJsonAdapter implements SaJsonTemplate {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String objectToJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JacksonException ex) {
            throw new SaJsonConvertException(ex);
        }
    }

    @Override
    public <T> T jsonToObject(String jsonString, Class<T> clazz) {
        if (jsonString == null) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (JacksonException ex) {
            throw new SaJsonConvertException(ex);
        }
    }
}
