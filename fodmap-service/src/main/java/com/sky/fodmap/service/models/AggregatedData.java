package com.sky.fodmap.service.models;

import com.datastax.driver.core.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType("aggregated_data")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedData {

    @CassandraType(type = DataType.Name.UDT, userTypeName = "red" )
    private StratifiedData red;

    @CassandraType(type = DataType.Name.UDT, userTypeName = "amber" )
    private StratifiedData amber;

    @CassandraType(type = DataType.Name.UDT, userTypeName = "green" )
    private StratifiedData green;

}
