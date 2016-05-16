package lt.milkusteam.cloud.web;

import lt.milkusteam.cloud.web.config.WebConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * Created by gediminas on 4/4/16.
 */
@Configuration
@Import(WebConfiguration.class)
public class TestWebConfiguration {
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        return resolver;
    }
}
