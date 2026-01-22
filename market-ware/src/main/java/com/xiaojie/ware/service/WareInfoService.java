package com.xiaojie.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.ware.entity.WareInfoEntity;
import com.xiaojie.ware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *

 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addrId);
}

