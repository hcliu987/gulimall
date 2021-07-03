package com.hc.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hc.common.utils.PageUtils;
import com.hc.common.utils.Query;

import com.hc.gulimall.product.dao.CategoryDao;
import com.hc.gulimall.product.entity.CategoryEntity;
import com.hc.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前的菜单是否被别的地方所引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //组装父子的属性结构
        //找到所有的一级分类
        List<CategoryEntity> collect = categoryEntities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, categoryEntities));
            return menu;
        }).sorted((m1, m2) -> {
            return m1.getSort() - m2.getSort();
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param entity
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity entity, List<CategoryEntity> all) {
        return all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == entity.getCatId();
        }).map(categoryEntity -> {
            //找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((m1, m2) -> {
            return (m1.getSort()==null?0:m1.getSort()) - (m2.getSort()==null?0:m2.getSort());
        }).collect(Collectors.toList());
    }

}