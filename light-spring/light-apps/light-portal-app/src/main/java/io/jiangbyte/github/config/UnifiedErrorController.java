package io.jiangbyte.github.config;

import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.autoconfigure.error.AbstractErrorController;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 替换默认 /error 页：无论 Accept 为何，一律返回统一 Response JSON。
 */
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class UnifiedErrorController extends AbstractErrorController {

    public UnifiedErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    /**
     * 处理容器转发到 /error 的请求。
     */
    @RequestMapping
    public ResponseEntity<Response<Void>> error(HttpServletRequest request) {
        // 1. 从请求属性读取原始状态码
        HttpStatus status = getStatus(request);
        // 2. 映射为业务码并强制 JSON，杜绝 Whitelabel HTML
        ResponseCode responseCode = mapStatus(status);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Response.fail(responseCode));
    }

    /**
     * 将 HTTP 状态映射为对外业务码。
     */
    private ResponseCode mapStatus(HttpStatus status) {
        if (status == HttpStatus.NOT_FOUND) {
            return ResponseCode.NOT_FOUND;
        }
        if (status == HttpStatus.METHOD_NOT_ALLOWED) {
            return ResponseCode.METHOD_NOT_ALLOWED;
        }
        if (status.is4xxClientError()) {
            return ResponseCode.ILLEGAL_PARAMETER;
        }
        return ResponseCode.UN_ERROR;
    }

}
