package com.xiaojie.search.service;

import com.xiaojie.search.vo.SearchParam;
import com.xiaojie.search.vo.SearchResult;

public interface SearchService {
    SearchResult getSearchResult(SearchParam searchParam);
}
