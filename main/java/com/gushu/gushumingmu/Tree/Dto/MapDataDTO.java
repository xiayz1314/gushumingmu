package com.gushu.gushumingmu.Tree.Dto;

import lombok.Data;

import java.util.List;

@Data
public class MapDataDTO {
    private Long total;
    private List<MapTreeDTO> records;
}
