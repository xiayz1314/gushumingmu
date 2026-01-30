package com.gushu.gushumingmu.Tree.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("ancient_tree")
public class AncientTree {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String treeCode;
    private String treeName;
    private String species;
    private Integer age;
    @TableField("age_estimate_type")
    private Integer ageEstimateType;
    private BigDecimal diameter;
    private BigDecimal height;
    @TableField("crown_width")
    private BigDecimal crownWidth;
    @TableField("health_status")
    private Integer healthStatus;
    @TableField("protection_level")
    private Integer protectionLevel;
    private BigDecimal longitude;
    private BigDecimal latitude;
    @TableField("location_detail")
    private String locationDetail;
    private String ownership;
    private String manager;
    @TableField("manager_phone")
    private String managerPhone;
    private String description;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("is_deleted")
    private Integer isDeleted;
}