package com.sky.fodmap.service.repository;

import com.sky.fodmap.service.models.FoodItem;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemRespository extends CassandraRepository<FoodItem,String> {

    @Query("select * from fodmap.food_item where food_group = ?0 and name = ?1")
    FoodItem findByFoodGroupAndName(String foodGroup, String name);
}
