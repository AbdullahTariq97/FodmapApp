package com.sky.fodmap.service.service;

import com.sky.fodmap.service.enums.Colours;
import com.sky.fodmap.service.enums.FoodGroups;
import com.sky.fodmap.service.exception.NotFoundException;
import com.sky.fodmap.service.models.FoodItem;
import com.sky.fodmap.service.models.StratifiedData;
import com.sky.fodmap.service.repository.ItemRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRespository itemRespository;

    @InjectMocks
    private ItemService itemService;

    @Test
    public void whenFoodGroupsRequested_shouldReturnUniqueListOfFoodGroups(){
        // Given
        List<FoodItem> listOfFoodItems = List.of(
        FoodItem.builder().foodGroup(FoodGroups.FRUIT.name()).name("apricot").build(),
        FoodItem.builder().foodGroup(FoodGroups.FRUIT.name()).name("banana").build(),
        FoodItem.builder().foodGroup(FoodGroups.VEGITABLE.name()).name("asparagus").build());

        when(itemRespository.findAll()).thenReturn(listOfFoodItems);

        // When
        List<String> listOfFoodGroups = itemService.getAllFoodGroups();


        // Then
        Map<String,Integer> countMap = new HashMap<>();

        for(String element: listOfFoodGroups){
            if(countMap.containsKey(element)){
                countMap.put(element, countMap.get(element) + 1);
            } else {
                countMap.put(element, 1);
            }
        }

        assertTrue(listOfFoodItems.stream().map(item -> item.getFoodGroup()).allMatch(foodGroups -> countMap.containsKey(foodGroups)));
        countMap.forEach((k,v) -> assertTrue(countMap.get(k) < 2));
        assertEquals(listOfFoodGroups.stream().distinct().collect(Collectors.toList()).size(), listOfFoodGroups.size());
    }

    // should throw not found exception if

    @Test
    public void givenTheItemExists_whenFoodItemRequestedByGroupAndName_shouldReturnFoodItem(){
        // Given
        FoodItem itemReturnedByRepo= FoodItem.builder().foodGroup(FoodGroups.VEGITABLE.name()).name("apple, granny smith").data(
                Map.of(Colours.RED.name(),StratifiedData.builder().amountInGrams(165).fructose("red").lactose("green").manitol("green").sorbitol("red").gos("green").fructan("green").build(),
                        Colours.RED.name(),StratifiedData.builder().amountInGrams(30).fructose("green").lactose("green").manitol("green").sorbitol("red").gos("green").fructan("green").build(),
                        Colours.RED.name(),StratifiedData.builder().amountInGrams(25).fructose("green").lactose("green").manitol("green").sorbitol("green").gos("green").fructan("green").build())).build();

        when(itemRespository.findByFoodGroup(FoodGroups.FRUIT.name().toLowerCase())).thenReturn(List.of(itemReturnedByRepo));

        // When
        FoodItem itemReturnedByService = itemService.getByGroupAndName(FoodGroups.FRUIT.name().toLowerCase(), "apple, granny smith");

        // Then
        assertEquals(itemReturnedByService, itemReturnedByRepo);
    }

    @Test
    public void givenTheItemWithFoodGroupDoesNotExist_whenFoodItemRequestedByGroupAndName_shouldThrowSuitableNotFoundException(){
        // Given
        when(itemRespository.findByFoodGroup(FoodGroups.FRUIT.name().toLowerCase())).thenReturn(Collections.emptyList());

        // When and Then
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> itemService.getByGroupAndName(FoodGroups.FRUIT.name().toLowerCase(), "apple, granny smith"));
        assertThat(notFoundException).extracting("fieldNotFound", "fieldValue").containsExactly("food group",FoodGroups.FRUIT.name().toLowerCase());
    }

    @Test
    public void givenItemWithFoodGroupExistsButNotNamet_whenFoodItemRequestedByGroupAndName_shouldThrowSuitableNotFoundException(){
        // Given
        FoodItem itemReturnedByRepo = FoodItem.builder().foodGroup(FoodGroups.VEGITABLE.name()).name("apple, granny smith").data(
                Map.of(Colours.RED.name(),StratifiedData.builder().amountInGrams(165).fructose("red").lactose("green").manitol("green").sorbitol("red").gos("green").fructan("green").build(),
                        Colours.RED.name(),StratifiedData.builder().amountInGrams(30).fructose("green").lactose("green").manitol("green").sorbitol("red").gos("green").fructan("green").build(),
                        Colours.RED.name(),StratifiedData.builder().amountInGrams(25).fructose("green").lactose("green").manitol("green").sorbitol("green").gos("green").fructan("green").build())).build();

        when(itemRespository.findByFoodGroup(FoodGroups.FRUIT.name().toLowerCase())).thenReturn(List.of(itemReturnedByRepo));

        // When and Then
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> itemService.getByGroupAndName(FoodGroups.FRUIT.name().toLowerCase(), "apricot"));
        assertThat(notFoundException).extracting("fieldNotFound", "fieldValue").containsExactly("food name", "apricot");
    }
}
