package com.gushu.gushumingmu.Tree.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gushu.gushumingmu.Tree.Dto.AncientTreeDTO;
import com.gushu.gushumingmu.Tree.Dto.MapTreeDTO;
import com.gushu.gushumingmu.Tree.entity.AncientTree;

import java.util.List;

public interface AncientTreeService extends IService<AncientTree> {

    IPage<AncientTreeDTO> pageQuery(
            int page,
            int size,
            String treeName,
            String species
    );

    AncientTreeDTO detail(Long id);

    void create(AncientTreeDTO dto);

    void update(Long id, AncientTreeDTO dto);

    void remove(Long id);

    List<MapTreeDTO> mapTrees();
}
