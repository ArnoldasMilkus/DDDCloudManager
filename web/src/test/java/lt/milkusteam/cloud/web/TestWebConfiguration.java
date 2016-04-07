package lt.milkusteam.cloud.web;

import lt.milkusteam.cloud.web.config.WebConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by gediminas on 4/4/16.
 */
@Configuration
@Import(WebConfiguration.class)
public class TestWebConfiguration {
}
