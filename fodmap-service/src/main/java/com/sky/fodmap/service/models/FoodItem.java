package com.sky.fodmap.service.models;

import com.datastax.driver.core.DataType;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("food_item")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem {

    @PrimaryKeyColumn(name = "food_group",ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String foodGroup;

    @PrimaryKeyColumn(name = "name", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String name;

    @Column("overall_rating")
    private String overallRating;

    @CassandraType(type = DataType.Name.UDT, userTypeName = "data" )
    private AggregatedData data;
}
