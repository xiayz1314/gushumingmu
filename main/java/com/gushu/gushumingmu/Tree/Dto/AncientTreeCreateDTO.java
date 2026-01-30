package com.gushu.gushumingmu.Tree.Dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AncientTreeCreateDTO {
    private String treeCode;
    private String treeName;
    private String species;
    private Integer age;
    private Integer ageEstimateType;
    private BigDecimal diameter;
    private BigDecimal height;
    private BigDecimal crownWidth;
    private Integer healthStatus;
    private Integer protectionLevel;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String locationDetail;
    private String ownership;
    private String manager;
    private String managerPhone;
    private String description;
}