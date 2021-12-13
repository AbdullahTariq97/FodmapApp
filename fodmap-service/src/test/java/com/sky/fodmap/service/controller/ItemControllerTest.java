package com.sky.fodmap.service.controller;

import com.sky.fodmap.service.controllers.ItemController;
import com.sky.fodmap.service.enums.Colours;
import com.sky.fodmap.service.enums.FoodGroups;
import com.sky.fodmap.service.models.*;
import com.sky.fodmap.service.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController foodItemController;

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
        when(itemService.getAllFoodGroups()).thenReturn(listOfFoodGroups);

        // when
        List<String> listReturedByController = foodItemController.getAllFoodGroups();
        assertEquals(listReturedByController, listOfFoodGroups);
    }

    // We dont deal with null food item scenario at controller level
    // null item will never be sent to the control from the service. the serivce this throw not found runtime exception
    @Test
    public void whenFoodItemRequestedByGroupAndName_shouldReturnFoodItem(){
        // Given
        StratifiedData stratifiedDataRed = StratifiedData.builder().amountInGrams(165).fructan(Colours.RED.name()).lactose(Colours.GREEN.name()).manitol(Colours.GREEN.name())
                .sorbitol(Colours.RED.name()).gos(Colours.GREEN.name()).fructan(Colours.GREEN.name()).build();
        StratifiedData stratifiedDataAmber = StratifiedData.builder().amountInGrams(30).fructan(Colours.GREEN.name()).lactose(Colours.GREEN.name()).manitol(Colours.GREEN.name())
                .sorbitol(Colours.AMBER.name()).gos(Colours.GREEN.name()).fructan(Colours.GREEN.name()).build();
        StratifiedData stratifiedDataGreen = StratifiedData.builder().amountInGrams(25).fructan(Colours.GREEN.name()).lactose(Colours.GREEN.name()).manitol(Colours.GREEN.name())
                .sorbitol(Colours.GREEN.name()).gos(Colours.GREEN.name()).fructan(Colours.GREEN.name()).build();

        FodmapData fodmapData = new FodmapData(Colours.RED.name(), stratifiedDataGreen, stratifiedDataAmber, stratifiedDataRed);

        FoodItem foodItemReturnedByService = new FoodItem(FoodGroups.FRUIT.name().toLowerCase(), "apple, granny smith", fodmapData);
        when(itemService.getByGroupAndName("fruit","apple")).thenReturn(foodItemReturnedByService);

        // When
        FoodItem foodItemReturned = foodItemController.getByGroupAndName(FoodGroups.FRUIT.name().toLowerCase(), "apple");

        // Then
        assertEquals(foodItemReturned, foodItemReturnedByService);
    }
}
