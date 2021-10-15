package com.sky.fodmapApp.ft.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.sky.fodmapApp.ft.*")
public class ComponentSearchConfig {
    //Does this config class increase efficiency of spring to load beans quickly ?
}