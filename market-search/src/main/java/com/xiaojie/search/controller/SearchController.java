package com.xiaojie.search.controller;

import com.xiaojie.common.utils.R;
import com.xiaojie.search.service.SearchService;
import com.xiaojie.search.vo.SearchParam;
import com.xiaojie.search.vo.SearchResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("search/product")
@Tag(name = "商品搜索", description = "商品搜索相关接口")
public class SearchController {
    @Autowired
    private SearchService searchService;
/**
     * 商品搜索API
     * @param searchParam 搜索参数
     * @return 搜索结果
     */
    @GetMapping("/list")
    @Operation(summary = "商品搜索", description = "根据关键词、分类、品牌等条件搜索商品")
    public R searchProducts(@Parameter(description = "搜索参数，包含关键词、筛选条件、排序等") SearchParam searchParam) {
        SearchResult result = searchService.getSearchResult(searchParam);
        return R.ok().put("data", result);
    }
}
