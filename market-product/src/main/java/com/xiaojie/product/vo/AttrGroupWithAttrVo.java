package com.xiaojie.product.vo;

import com.xiaojie.product.entity.AttrEntity;
import com.xiaojie.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrVo extends AttrGroupEntity {
    private List<AttrEntity> attrs;
}
