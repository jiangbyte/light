package io.jiangbyte.github.trigger.portal.http;

import io.jiangbyte.github.api.portal.IXxxService;
import io.jiangbyte.github.api.portal.dto.XxxRequestDTO;
import io.jiangbyte.github.api.portal.dto.XxxResponseDTO;
import io.jiangbyte.github.types.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Portal HTTP 示例；复制后改名并实现真实逻辑。
 */
@RestController
@RequestMapping("/portal/xxx")
public class XxxController implements IXxxService {

    @Override
    @GetMapping("/query")
    public XxxResponseDTO query(XxxRequestDTO request) {
        // 1. 校验入参（示例直接返回占位数据）
        // 2. 调用 domain 服务
        // 3. 组装精简 DTO
        XxxResponseDTO dto = new XxxResponseDTO();
        dto.setId(request == null ? null : request.getId());
        dto.setName("portal-xxx-placeholder");
        return dto;
    }

    @GetMapping("/demo")
    public Response<XxxResponseDTO> demo(XxxRequestDTO request) {
        return Response.success(query(request));
    }
}
