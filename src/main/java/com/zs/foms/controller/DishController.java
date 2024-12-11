package com.zs.foms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.foms.common.R;
import com.zs.foms.dto.DishDto;
import com.zs.foms.entity.Category;
import com.zs.foms.entity.Dish;
import com.zs.foms.entity.DishFlavor;
import com.zs.foms.service.CategoryService;
import com.zs.foms.service.DishFlavorService;
import com.zs.foms.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
@Api(tags = "B端-菜品管理接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    @Operation(summary = "新增菜品")
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品详情")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    @GetMapping("/chart")
    @Operation(summary = "根据分类返回菜品数量")
    public R<Map<String, Integer>> getDishChart(){

        // 从数据库获取所有的 dish 数据
        List<Dish> dishList = dishService.list();

        // 从数据库获取所有的 category 数据
        List<Category> categoryList = categoryService.list();

        // 将 category 转换为 Map，key 为 category_id，value 为分类名称
        Map<Long, String> categoryMap = categoryList.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 统计每种分类的菜品数量
        Map<Long, Integer> categoryCountMap = dishList.stream()
                .collect(Collectors.groupingBy(Dish::getCategoryId, Collectors.reducing(0, e -> 1, Integer::sum)));

        // 将 category_id 替换为分类名称
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : categoryCountMap.entrySet()) {
            String categoryName = categoryMap.get(entry.getKey());
            if (categoryName != null) {
                result.put(categoryName, entry.getValue());
            }
        }

        System.out.println("-----------------chart");
        System.out.println(result);
        // 返回结果
        return R.success(result);

    }



    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    @Operation(summary = "修改菜品信息")
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        System.out.println("--------------------"+dishDto.getStatus());
        System.out.println("--------------------"+dishDto.getId());

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    @PostMapping("/status/{id}")
    @Operation(summary = "修改状态")
//    public R<String> dishStatus(@RequestBody DishDto dishDto){
//        log.info(dishDto.toString());
//
//        dishService.dishStatus(dishDto);
//
//        return R.success("修改状态成功");
//    }
    public R<String> dishStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("Received id: {}, status: {}", id, status);

        // 调用服务逻辑，更新状态
        DishDto dishDto = new DishDto();
        dishDto.setId(id);
        dishDto.setStatus(Integer.valueOf(status));
        dishService.dishStatus(dishDto);

        return R.success("修改状态成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }*/

    @GetMapping("/list")
    @Operation(summary = "根据条件查询菜品列表")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

    @DeleteMapping
    @Operation(summary = "根据id删除")
    public R<String> delete(@RequestParam Long id){
        System.out.println("---------------controller:"+id);
        log.info("删除Dish，id为：{}",id);

        //categoryService.removeById(id);
        dishService.remove(id);

        return R.success("Dish删除成功");
    }

}
