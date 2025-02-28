package sequence.sequence_member.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:/properties/env.properties", ignoreResourceNotFound = true) // env.properties 파일 소스 등록
public class PropertyConfig {
}
