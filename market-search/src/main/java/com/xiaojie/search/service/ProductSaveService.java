package com.xiaojie.search.service;

import com.xiaojie.common.to.es.SkuESModel;

import java.io.IOException;
import java.util.List;




public interface ProductSaveService {

    boolean saveProductAsIndices(List<SkuESModel> skuEsModels) throws IOException;
}
