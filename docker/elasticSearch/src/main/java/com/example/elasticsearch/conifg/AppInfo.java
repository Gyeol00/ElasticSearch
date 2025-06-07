package com.example.elasticsearch.conifg;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppInfo {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.application.version}")
    private String appVersion;

    private String headerLogo;

}
