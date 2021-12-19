package com.sky.fodmap.service.service;

import com.sky.fodmap.service.exception.NotFoundException;
import com.sky.fodmap.service.models.FoodItem;
import com.sky.fodmap.service.repository.ItemRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRespository itemRespository;

    public List<String> getAllFoodGroups() {
        List<FoodItem> listFoodItems = itemRespository.findAll();
        List<String> listOfGFoodGroups = listFoodItems.stream()
                .map(FoodItem::getFoodGroup)
                .distinct()
                .collect(Collectors.toList());
        return listOfGFoodGroups;
    }

    public FoodItem getByGroupAndName(String foodGroup, String name) {
        List<FoodItem> listOfItemMatchingFoodName = itemRespository.findByFoodGroup(foodGroup);

        if(listOfItemMatchingFoodName.isEmpty()){
            throw new NotFoundException("food group", foodGroup);
        } else {
            return listOfItemMatchingFoodName.stream().filter(item -> item.getName().equals(name)).findFirst()
                    .orElseThrow(() -> new NotFoundException("food name", name));
        }
    }
}
