package com.xiaojie.search.service.impl;

import com.xiaojie.search.service.SearchService;
import com.xiaojie.search.vo.SearchParam;
import com.xiaojie.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Slf4j
@Service("SearchService")
public class SearchServiceImpl implements SearchService {
    @Override
    public SearchResult getSearchResult(SearchParam searchParam) {

        //TODO
        return null;
    }
}
