package com.sky.fodmap.service.exception;

import com.sky.fodmap.service.controllers.ItemController;
import com.sky.fodmap.service.controllers.ReadinessController;
import com.sky.fodmap.service.enums.FoodGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private ItemController itemController;

    @Mock
    private ReadinessController readinessController;

    private MockMvc mockMvc;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setup(){
        globalExceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(itemController, readinessController).setControllerAdvice(globalExceptionHandler).build();
    }

    @Test
    public void whenNotFoundExceptionPassed_shouldReturnSuitableResponse(){
        ResponseEntity<String> responseEntity = globalExceptionHandler.handleNotFoundException(new NotFoundException("food group", FoodGroups.FRUIT.name().toLowerCase()));
        assertThat(responseEntity).extracting("body").isEqualTo("Food item with food group fruit not found");
    }

    @Test
    public void givenItemControllerThrowsException_whenEndpointPolled_shouldReturnResponsedMapperByGlobalExceptionHandler() throws Exception {
        // Given
        when(itemController.getByGroupAndName(FoodGroups.DAIRY.name().toLowerCase(), "milk"))
                .thenThrow(new NotFoundException("food group", "dairy"));

        // When and then
        mockMvc.perform(get("/user/get-by-group-and-name/dairy/milk"))
                .andExpect(status().is(404))
                .andExpect(content().string("Food item with food group dairy not found"));
    }
}
