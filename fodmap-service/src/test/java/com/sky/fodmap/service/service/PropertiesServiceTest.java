package com.sky.fodmap.service.service;

import com.sky.fodmap.service.models.ApplicationPropertiesDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertiesServiceTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private PropertiesService propertiesService;

    @Test
    public void shouldReturnAppropriatePropertiesServiceDto(){
        // Given
        when(environment.getProperty("app.name")).thenReturn("app name");
        when(environment.getProperty("app.version")).thenReturn("app version");
        when(environment.getActiveProfiles()).thenReturn(new String[]{"profile1","profile2"});

        // When
        ApplicationPropertiesDTO applicationPropertiesDTO = propertiesService.getProperties();

        // Then
        assertThat(applicationPropertiesDTO).extracting("name","environment", "version")
                .containsExactly("app name", "[profile1, profile2]","app version");
    }
}
