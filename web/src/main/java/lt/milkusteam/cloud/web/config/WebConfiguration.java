package lt.milkusteam.cloud.web.config;

import lt.milkusteam.cloud.core.config.CoreConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by gediminas on 3/30/16.
 */
@Configuration
@ComponentScan(basePackages = "lt.milkusteam.cloud.web")
@Import(value = {CoreConfiguration.class})
public class WebConfiguration {
}
