package com.wwd.base.controller;

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

import com.wwd.base.entity.CommonType;
import com.wwd.base.service.CommonTypeService;
import com.wwd.baseapi.dto.CommonTypeDTO;
import com.wwd.baseapi.dto.CommonTypeQueryDTO;
import com.wwd.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/base/commonType")
@RequiredArgsConstructor
public class CommonTypeController {

    private final CommonTypeService commonTypeService;

    @GetMapping("/{typeId}")
    public Result<CommonTypeDTO> getById(@PathVariable Long typeId) {
        CommonType type = commonTypeService.getById(typeId);
        if (type == null || type.getStatus() == 2) {
            return Result.error(404, "类型不存在");
        }
        return Result.success(convertToDTO(type));
    }

    @GetMapping("/group/{typeGroup}")
    public Result<List<CommonTypeDTO>> getByGroup(@PathVariable String typeGroup) {
        List<CommonType> types = commonTypeService.getTypesByGroup(typeGroup);
        List<CommonTypeDTO> dtos = types.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(dtos);
    }

    @GetMapping("/group/{typeGroup}/code/{typeCode}")
    public Result<CommonTypeDTO> getByGroupAndCode(
            @PathVariable String typeGroup,
            @PathVariable String typeCode) {
        CommonType type = commonTypeService.getByGroupAndCode(typeGroup, typeCode);
        if (type == null) {
            return Result.error(404, "类型不存在");
        }
        return Result.success(convertToDTO(type));
    }

    @PostMapping("/list")
    public Result<List<CommonTypeDTO>> list(@RequestBody CommonTypeQueryDTO query) {
        List<CommonType> types = commonTypeService.listTypes(query);
        List<CommonTypeDTO> dtos = types.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Result.success(dtos);
    }

    @GetMapping("/tree/{typeGroup}")
    public Result<List<Map<String, Object>>> getTree(@PathVariable String typeGroup) {
        List<Map<String, Object>> tree = commonTypeService.getTypeTree(typeGroup);
        return Result.success(tree);
    }

    @GetMapping("/mapping/{typeGroup}")
    public Result<Map<String, String>> getMapping(@PathVariable String typeGroup) {
        Map<String, String> mapping = commonTypeService.getTypeMapping(typeGroup);
        return Result.success(mapping);
    }

    @PostMapping("/batch-names/{typeGroup}")
    public Result<Map<String, String>> batchGetNames(
            @PathVariable String typeGroup,
            @RequestBody List<String> codes) {
        Map<String, String> mapping = commonTypeService.batchGetNames(typeGroup, codes);
        return Result.success(mapping);
    }

    @PostMapping
    public Result<Long> create(@RequestBody CommonTypeDTO dto) {
        CommonType type = convertToEntity(dto);
        Long typeId = commonTypeService.createType(type);
        return Result.success(typeId);
    }

    @PutMapping("/{typeId}")
    public Result<Boolean> update(
            @PathVariable Long typeId,
            @RequestBody CommonTypeDTO dto) {
        CommonType type = convertToEntity(dto);
        boolean success = commonTypeService.updateType(typeId, type);
        return Result.success(success);
    }

    @DeleteMapping("/{typeId}")
    public Result<Boolean> delete(@PathVariable Long typeId) {
        boolean success = commonTypeService.deleteType(typeId);
        return Result.success(success);
    }

    @PutMapping("/{typeId}/status/{status}")
    public Result<Boolean> updateStatus(
            @PathVariable Long typeId,
            @PathVariable Integer status) {
        boolean success = commonTypeService.updateTypeStatus(typeId, status);
        return Result.success(success);
    }

    private CommonTypeDTO convertToDTO(CommonType entity) {
        CommonTypeDTO dto = new CommonTypeDTO();
        BeanUtils.copyProperties(entity, dto);

        // 如果有父级，获取父级名称
        if (entity.getParentId() != null) {
            CommonType parent = commonTypeService.getById(entity.getParentId());
            if (parent != null && parent.getStatus() != 2) {
                dto.setParentName(parent.getTypeName());
            }
        }

        return dto;
    }

    private CommonType convertToEntity(CommonTypeDTO dto) {
        CommonType entity = new CommonType();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
