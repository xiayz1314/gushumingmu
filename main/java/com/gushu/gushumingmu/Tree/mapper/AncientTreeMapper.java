package com.gushu.gushumingmu.Tree.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gushu.gushumingmu.Tree.Dto.AncientTreeDTO;
import com.gushu.gushumingmu.Tree.Dto.MapTreeDTO;
import com.gushu.gushumingmu.Tree.entity.AncientTree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AncientTreeMapper extends BaseMapper<AncientTree> {

    IPage<AncientTreeDTO> selectPageDTO(
            Page<?> page,
            @Param("treeName") String treeName,
            @Param("species") String species
    );

    AncientTreeDTO selectDTOById(@Param("id") Long id);

    List<MapTreeDTO> selectMapTrees();

    int countAll();

    List<Map<String, Object>> speciesDistribution(@Param("limit") int limit);

    List<Map<String, Object>> healthDistribution();

    List<Map<String, Object>> levelDistribution();

    List<Map<String, Object>> ageDistribution();

    List<Map<String, Object>> mapData();
}
