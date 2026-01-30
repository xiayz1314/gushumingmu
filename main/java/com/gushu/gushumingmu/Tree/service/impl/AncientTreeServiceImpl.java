package com.gushu.gushumingmu.Tree.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gushu.gushumingmu.Tree.Dto.AncientTreeDTO;
import com.gushu.gushumingmu.Tree.Dto.MapTreeDTO;
import com.gushu.gushumingmu.Tree.entity.AncientTree;
import com.gushu.gushumingmu.Tree.mapper.AncientTreeMapper;
import com.gushu.gushumingmu.Tree.service.AncientTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AncientTreeServiceImpl
        extends ServiceImpl<AncientTreeMapper, AncientTree>
        implements AncientTreeService {

    private final AncientTreeMapper mapper;

    @Override
    public IPage<AncientTreeDTO> pageQuery(int page, int size, String treeName, String species) {
        Page<AncientTree> p = new Page<>(page, size);
        return mapper.selectPageDTO(p, treeName, species);
    }

    @Override
    public AncientTreeDTO detail(Long id) {
        return mapper.selectDTOById(id);
    }

    @Override
    public void create(AncientTreeDTO dto) {
        AncientTree entity = new AncientTree();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
    }

    @Override
    public void update(Long id, AncientTreeDTO dto) {
        AncientTree entity = getById(id);
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    public void remove(Long id) {
        removeById(id);
    }

    @Override
    public List<MapTreeDTO> mapTrees() {
        return mapper.selectMapTrees();
    }
}
