package com.sky.fodmap.service.models;

import com.datastax.driver.core.DataType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Map;

@Table("food_item")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem {

    @JsonProperty("food_group")
    @PrimaryKeyColumn(name = "food_group",ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String foodGroup;

    @PrimaryKeyColumn(name = "name", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String name;

    @JsonProperty("overall_rating")
    @Column("overall_rating")
    private String overallRating;

    @CassandraType(type = DataType.Name.MAP, userTypeName = "stratified_data" )
    private Map<String,StratifiedData> data;
}
