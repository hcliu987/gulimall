package com.hc.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hc.gulimall.product.entity.ProductAttrValueEntity;
import com.hc.gulimall.product.service.ProductAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hc.gulimall.product.entity.AttrEntity;
import com.hc.gulimall.product.service.AttrService;
import com.hc.common.utils.PageUtils;
import com.hc.common.utils.R;



/**
 * 商品属性
 *
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 20:09:31
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;


    @Autowired
    private ProductAttrValueService productAttrValueService;

    ///product/attr/base/listforspu/{spuId}

    /**
     * 获取spu规格
     * @param spuId
     * @return
     */
    public  R baseAttrlistforspu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entities =    productAttrValueService.baseAttrListforspu(spuId);
        return R.ok().put("data", entities);
    }

@GetMapping("/{attrType}/list/{catelogId}")
public  R baseAttrList(@RequestParam Map<String,Object> params,
                       @PathVariable("catelogId") Long catelogId,
                       @PathVariable("attrType") String type){
    PageUtils page =   attrService.queryBaseAttrPage(params,catelogId,type);
    return R.ok().put("page",page);
}


    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
   // @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrEntity attr = attrService.getById(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrEntity attr){
		attrService.save(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrEntity attr){
		attrService.updateById(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
