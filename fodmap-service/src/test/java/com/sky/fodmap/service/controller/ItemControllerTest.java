package com.sky.fodmap.service.controller;

import com.sky.fodmap.service.controllers.ItemController;
import com.sky.fodmap.service.enums.Colours;
import com.sky.fodmap.service.enums.FoodGroups;
import com.sky.fodmap.service.models.FoodItem;
import com.sky.fodmap.service.models.StratifiedData;
import com.sky.fodmap.service.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

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
        FoodItem itemReturnedByService = FoodItem.builder().foodGroup(FoodGroups.FRUIT.name()).name("apple, granny smith").data(
                Map.of(Colours.RED.name(),StratifiedData.builder().amountInGrams(165).fructose("red").lactose("green").manitol("green").sorbitol("red").gos("green").fructan("green").build(),
                        Colours.GREEN.name(), StratifiedData.builder().amountInGrams(30).fructose("green").lactose("green").manitol("green").sorbitol("red").gos("green").fructan("green").build(),
                        Colours.AMBER.name(),StratifiedData.builder().amountInGrams(25).fructose("green").lactose("green").manitol("green").sorbitol("green").gos("green").fructan("green").build())).build();

        when(itemService.getByGroupAndName("fruit","apple, granny smith")).thenReturn(itemReturnedByService);

        // When
        FoodItem foodItemReturned = foodItemController.getByGroupAndName(FoodGroups.FRUIT.name().toLowerCase(), "apple, granny smith");

        // Then
        assertEquals(foodItemReturned, itemReturnedByService);
    }
}
