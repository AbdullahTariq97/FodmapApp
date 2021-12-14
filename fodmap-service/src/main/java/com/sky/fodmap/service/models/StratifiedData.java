package com.sky.fodmap.service.models;

import com.datastax.driver.core.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType("stratified_data")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StratifiedData {

    @CassandraType(type = DataType.Name.INT, userTypeName = "amount" )
    private int amount;
    @CassandraType(type = DataType.Name.TEXT, userTypeName = "fructose" )
    private String fructose;
    @CassandraType(type = DataType.Name.TEXT, userTypeName = "lactose" )
    private String lactose;
    @CassandraType(type = DataType.Name.TEXT, userTypeName = "manitol" )
    private String manitol;
    @CassandraType(type = DataType.Name.TEXT, userTypeName = "sorbitol" )
    private String sorbitol;
    @CassandraType(type = DataType.Name.TEXT, userTypeName = "gos" )
    private String gos;
    @CassandraType(type = DataType.Name.TEXT, userTypeName = "fructan" )
    private String fructan;

//    public static StratifiedDtoBuilder builder(){
//        return new StratifiedDtoBuilder();
//    }
//
//    private StratifiedData(StratifiedDtoBuilder stratifiedDtoBuilder){
//
//        this.amount = stratifiedDtoBuilder.amount;
//        this.fructose = stratifiedDtoBuilder.fructose;
//        this.lactose = stratifiedDtoBuilder.lactose;
//        this.manitol = stratifiedDtoBuilder.manitol;
//        this.sorbitol = stratifiedDtoBuilder.sorbitol;
//        this.gos = stratifiedDtoBuilder.gos;
//        this.fructan = stratifiedDtoBuilder.fructan;
//    }
//
//    public static class StratifiedDtoBuilder {
//
//        private int amount;
//        private String fructose;
//        private String lactose;
//        private String manitol;
//        private String sorbitol;
//        private String gos;
//        private String fructan;
//
//        public StratifiedDtoBuilder(){
//        }
//
//        public StratifiedDtoBuilder amountInGrams(int grams){
//            this.amount = grams;
//            return this;
//        }
//
//        public StratifiedDtoBuilder fructose(String rating){
//            this.fructose = rating;
//            return this;
//        }
//
//        public StratifiedDtoBuilder lactose(String rating){
//            this.lactose = rating;
//            return this;
//        }
//
//        public StratifiedDtoBuilder manitol(String rating){
//            this.manitol = rating;
//            return this;
//        }
//
//        public StratifiedDtoBuilder sorbitol(String rating){
//            this.sorbitol = rating;
//            return this;
//        }
//
//        public StratifiedDtoBuilder gos(String rating){
//            this.gos = rating;
//            return this;
//        }
//
//        public StratifiedDtoBuilder fructan(String rating){
//            this.fructan = rating;
//            return this;
//        }
//
//        public StratifiedData build(){
//            // private constructor can be accessed
//            return new StratifiedData(this);
//        }
//    }
}
