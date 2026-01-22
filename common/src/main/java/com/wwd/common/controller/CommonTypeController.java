package com.wwd.common.controller;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.controller.CommonTypeController
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-22
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-22     wangwd7          v1.0.0               创建
 */

import com.wwd.common.entity.CommonType;
import com.wwd.common.service.CommonTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/common/commonType")
public class CommonTypeController {

    @Autowired
    private CommonTypeService commonTypeService;

    /**
     * 根据分组获取类型
     */
    @GetMapping("/group/{typeGroup}")
    public ResponseEntity<Map<String, Object>> getByGroup(@PathVariable String typeGroup) {
        List<CommonType> types = commonTypeService.getTypesByGroup(typeGroup);
        return ResponseEntity.ok(createSuccessResponse(types));
    }

    /**
     * 根据分组和代码获取单个类型
     */
    @GetMapping("/group/{typeGroup}/code/{typeCode}")
    public ResponseEntity<Map<String, Object>> getByGroupAndCode(
            @PathVariable String typeGroup,
            @PathVariable String typeCode) {
        CommonType type = commonTypeService.getTypeByGroupAndCode(typeGroup, typeCode);
        return ResponseEntity.ok(createSuccessResponse(type));
    }

    /**
     * 新增类型
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addType(@RequestBody CommonType type) {
        boolean success = commonTypeService.addType(type);
        Map<String, Object> response = createSuccessResponse(null);
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
