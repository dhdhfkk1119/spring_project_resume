package com.join.spring_resume._core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "upload")
@Data
public class UploadProperties {
    private String rootDir;
    private String corpDir;
    private String memberDir;
}
