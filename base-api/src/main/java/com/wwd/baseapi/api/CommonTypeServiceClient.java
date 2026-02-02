package com.wwd.baseapi.api;

import com.wwd.baseapi.dto.CommonTypeDTO;
import com.wwd.baseapi.dto.CommonTypeQueryDTO;
import com.wwd.common.constant.ServiceNamesConstant;
import com.wwd.common.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.baseapi.api.CommonTypeSericeClient
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
@FeignClient(
        name = ServiceNamesConstant.BASE_SERVICE,
        contextId = ServiceNamesConstant.BASE_SERVICE,
        path = "/api/base/commonType"
)
public interface CommonTypeServiceClient {

    @GetMapping("/{typeId}")
    Result<CommonTypeDTO> getById(@PathVariable("typeId") Long typeId);

    @GetMapping("/group/{typeGroup}")
    Result<List<CommonTypeDTO>> getByGroup(@PathVariable("typeGroup") String typeGroup);

    @GetMapping("/group/{typeGroup}/code/{typeCode}")
    Result<CommonTypeDTO> getByGroupAndCode(
            @PathVariable("typeGroup") String typeGroup,
            @PathVariable("typeCode") String typeCode);

    @PostMapping("/list")
    Result<List<CommonTypeDTO>> list(@RequestBody CommonTypeQueryDTO query);

    @GetMapping("/tree/{typeGroup}")
    Result<List<Map<String, Object>>> getTree(@PathVariable("typeGroup") String typeGroup);

    @GetMapping("/mapping/{typeGroup}")
    Result<Map<String, String>> getMapping(@PathVariable("typeGroup") String typeGroup);

    @PostMapping("/batch-names/{typeGroup}")
    Result<Map<String, String>> batchGetNames(
            @PathVariable("typeGroup") String typeGroup,
            @RequestBody List<String> codes);

    @PostMapping
    Result<Long> create(@RequestBody CommonTypeDTO dto);

    @PutMapping("/{typeId}")
    Result<Boolean> update(
            @PathVariable("typeId") Long typeId,
            @RequestBody CommonTypeDTO dto);

    @DeleteMapping("/{typeId}")
    Result<Boolean> delete(@PathVariable("typeId") Long typeId);

    @PutMapping("/{typeId}/status/{status}")
    Result<Boolean> updateStatus(
            @PathVariable("typeId") Long typeId,
            @PathVariable("status") Integer status);

}
