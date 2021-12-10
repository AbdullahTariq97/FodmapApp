package com.sky.fodmap.service.service;

import com.sky.fodmap.service.models.FoodItem;
import com.sky.fodmap.service.repository.FoodItemRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRespository foodItemRespository;

    public List<String> getAllFoodGroups() {
        List<FoodItem> listFoodItems = foodItemRespository.findAll();
        List<String> listOfGFoodGroups = listFoodItems.stream()
                .map(FoodItem::getFoodGroup)
                .filter((item) -> !listFoodItems.contains(item)).collect(Collectors.toList());
        return listOfGFoodGroups;
    }

    public FoodItem getByGroupAndName(String groupName, String name) {
        return foodItemRespository.findByFoodGroupAndName(groupName,name);
    }
}
