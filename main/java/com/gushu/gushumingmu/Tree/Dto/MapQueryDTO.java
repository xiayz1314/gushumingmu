package com.gushu.gushumingmu.Tree.Dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MapQueryDTO {
    @NotNull(message = "最小经度不能为空")
    private BigDecimal minLng;

    @NotNull(message = "最大经度不能为空")
    private BigDecimal maxLng;

    @NotNull(message = "最小纬度不能为空")
    private BigDecimal minLat;

    @NotNull(message = "最大纬度不能为空")
    private BigDecimal maxLat;

    private String protectionLevel;
    private String healthStatus;
    private String species;
    private Integer minAge;
    private Integer maxAge;
    private String sortField = "id";
    private String sortOrder = "ASC";
    private Integer page = 0;
    private Integer size = 500;
}