package com.xiaojie.ware.feign;

import com.xiaojie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("market-member")
public interface MemberFeignService {
    @GetMapping("member/memberreceiveaddress/info/{id}")
    R info(@PathVariable("id") Long id);
}
