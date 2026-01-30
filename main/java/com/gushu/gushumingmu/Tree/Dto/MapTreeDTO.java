package com.gushu.gushumingmu.Tree.Dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MapTreeDTO {
    private Long id;
    private String treeName;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer protectionLevel;
}
