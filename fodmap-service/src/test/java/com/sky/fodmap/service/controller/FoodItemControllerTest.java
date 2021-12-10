package com.sky.fodmap.service.controller;

import com.sky.fodmap.service.controllers.FoodItemController;
import com.sky.fodmap.service.models.*;
import com.sky.fodmap.service.service.FoodItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodItemControllerTest {

    @Mock
    private FoodItemService foodItemService;

    @InjectMocks
    private FoodItemController foodItemController;

    @Test
    public void givenDataIsEmpty_whenAllFoodItemsRequested_shouldReturnEmptyList(){
        // When
        List<String> listOfFoodGroups  = foodItemController.getAllFoodGroups();

        // Then
        assertTrue(listOfFoodGroups.isEmpty());
    }

    @Test
    public void givenFoodItemsExist_whenAllFoodGroupsRequested_shouldReturnListOfFoodItems(){
        // Given
        List<String> listOfFoodGroups = List.of("FRUIT", "VEGITABLE", "DIARY", "PULSES_TOFU_AND_NUTS");
        when(foodItemService.getAllFoodGroups()).thenReturn(listOfFoodGroups);

        // when
        List<String> listReturedByController = foodItemController.getAllFoodGroups();
        assertEquals(listReturedByController, listOfFoodGroups);
    }

    @Test
    public void given_whenFoodItemRequestedByGroupAndName_shouldReturnFoodItem(){
        ProfileBuilder.builder().lactose().mannitol().build();
//         Given
        FoodItem foodItem = new FoodItem(FoodGroups.FRUIT.toString(), "apple granny smith", new FodmapDto(Colours.RED.toString(),
                Map.of("amount", "30g", "fructose", Colours.GREEN.toString(), "lactose", Colours.GREEN.toString(), "manitor"),

                Collections.emptyMap(),
                Map.of());

        // When
        FoodItem foodItemReturned = foodItemService.getByGroupAndName("fruit", "apple");

        // Then
        assertThat(foodItemReturned).isEqualTo()
    }
}
