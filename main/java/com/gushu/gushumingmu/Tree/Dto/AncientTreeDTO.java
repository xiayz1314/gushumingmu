package com.gushu.gushumingmu.Tree.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AncientTreeDTO {
    private Long id;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}