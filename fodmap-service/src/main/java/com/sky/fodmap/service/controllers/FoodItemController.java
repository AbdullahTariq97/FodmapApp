package com.sky.fodmap.service.controllers;

import com.sky.fodmap.service.models.FoodItem;
import com.sky.fodmap.service.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    @GetMapping("/food-groups")
    public List<String> getAllFoodGroups(){
        return foodItemService.getAllFoodGroups();
    }

    @GetMapping("/get-by-group-and-name/{group}/{name}")
    public FoodItem getByGroupAndName(@PathVariable("group") String groupName, @PathVariable("name") String name){
        return foodItemService.getByGroupAndName(groupName, name);
    }
}
