package com.hc.gulimall.search.controller;

import com.hc.common.es.SkuEsModel;
import com.hc.common.exception.BizCodeEnum;
import com.hc.common.utils.R;
import com.hc.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequestMapping(value = "/search/save")
@RestController
public class ElasticSaveController {
    @Autowired
    private ProductSaveService productSaveService;


    @PostMapping(value = "/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean status = false;

        try {
            status = productSaveService.productStatusUp(skuEsModels);
        }catch (IOException e){
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }
        if(status){
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }else {
            return R.ok();
        }

    }
}
