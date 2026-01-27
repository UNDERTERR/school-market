package com.xiaojie.search.controller;

import com.xiaojie.common.utils.R;
import com.xiaojie.search.service.SearchService;
import com.xiaojie.search.vo.SearchParam;
import com.xiaojie.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("search/product")
public class SearchController {
    @Autowired
    private SearchService searchService;
    /**
     * 商品搜索API
     * @param searchParam 搜索参数
     * @return 搜索结果
     */
    @GetMapping("/list")
    public R searchProducts(SearchParam searchParam) {
        SearchResult result = searchService.getSearchResult(searchParam);
        return R.ok().put("data", result);
    }
}
