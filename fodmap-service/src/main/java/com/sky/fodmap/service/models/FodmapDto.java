package com.sky.fodmap.service.models;

import com.datastax.driver.core.DataType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.util.Map;

@UserDefinedType("fodmap_data")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FodmapDto {

   @CassandraType(type = DataType.Name.TEXT)
   @Column("overall_rating")
   private String overallRating;

   // when repo converts data from cassandra to java object. field are empty
   // then the objects in returned back to front end and convert to json using object mapper
   // field which are empty are ignored in the json

   @JsonInclude(JsonInclude.Include.NON_EMPTY)
   @CassandraType(type = DataType.Name.MAP)
   @Column("stratified_green")
   private Map<String,String> stratifiedGreen;

   @JsonInclude(JsonInclude.Include.NON_EMPTY)
   @CassandraType(type = DataType.Name.MAP)
   @Column("stratified_amber")
   private Map<String,String> stratifiedAmber;

   @JsonInclude(JsonInclude.Include.NON_EMPTY)
   @CassandraType(type = DataType.Name.MAP)
   @Column("stratified_red")
   private Map<String,String> stratifiedRed;
}
