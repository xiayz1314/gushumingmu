package com.gushu.gushumingmu.Tree.Dto;

import lombok.Data;

@Data
public class BasicStatisticsDTO {
    private Long total;
    private Long level1;
    private Long danger;
    private Double avgAge;
    private Integer maxAge;
    private Integer minAge;
    private Long inspectToday;
}