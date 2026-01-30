package com.gushu.gushumingmu.Tree.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gushu.gushumingmu.Tree.Dto.AncientTreeDTO;
import com.gushu.gushumingmu.Tree.Dto.MapTreeDTO;
import com.gushu.gushumingmu.Tree.service.AncientTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ancient-trees")
@RequiredArgsConstructor
public class AncientTreeController {

    private final AncientTreeService ancientTreeService;

    @GetMapping("/page")
    public IPage<AncientTreeDTO> page(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String treeName,
            @RequestParam(required = false) String species
    ) {
        return ancientTreeService.pageQuery(page, size, treeName, species);
    }

    @GetMapping("/{id}")
    public AncientTreeDTO detail(@PathVariable Long id) {
        return ancientTreeService.detail(id);
    }

    @PostMapping
    public void create(@RequestBody AncientTreeDTO dto) {
        ancientTreeService.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @RequestBody AncientTreeDTO dto) {
        ancientTreeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ancientTreeService.remove(id);
    }

    @GetMapping("/map")
    public List<MapTreeDTO> mapTrees() {
        return ancientTreeService.mapTrees();
    }
}
