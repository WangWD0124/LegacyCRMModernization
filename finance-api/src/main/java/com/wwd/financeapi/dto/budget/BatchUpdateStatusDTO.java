package com.wwd.financeapi.dto.budget;

import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.financeapi.dto.budget.BatchUpdateStatusDTO
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-21
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-21     wangwd7          v1.0.0               创建
 */
@Data
public class BatchUpdateStatusDTO {

    private List<Long> ids;
    private String status;
}
