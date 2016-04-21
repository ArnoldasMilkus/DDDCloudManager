package lt.milkusteam.cloud.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
@Configuration
@ComponentScan
@EnableWebMvc
public class FileUploadConfiguration {
    public static String ROOT = "upload-dir";
}