package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.product.dao.AttrGroupDao;
import com.xiaojie.product.entity.AttrGroupEntity;
import com.xiaojie.product.service.AttrGroupService;
import org.springframework.stereotype.Service;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

}
