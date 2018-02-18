package diploma.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.datasource")
public class ConfigProperties extends Properties {
}
