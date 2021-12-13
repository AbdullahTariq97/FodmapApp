package com.sky.fodmap.service.controllers;

import com.sky.fodmap.service.models.FoodItem;
import com.sky.fodmap.service.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/food-groups")
    public List<String> getAllFoodGroups(){
        return itemService.getAllFoodGroups();
    }

    @GetMapping("/get-by-group-and-name/{group}/{name}")
    public FoodItem getByGroupAndName(@PathVariable("group") String groupName, @PathVariable("name") String name){
        return itemService.getByGroupAndName(groupName.toLowerCase(), name.toLowerCase());
    }
}
