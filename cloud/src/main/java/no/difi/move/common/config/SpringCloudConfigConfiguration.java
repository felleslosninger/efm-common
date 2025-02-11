package no.difi.move.common.config;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;

@Configuration
public class SpringCloudConfigConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, Resource> resourceConverter() {
        return new Converter<String, Resource>() {
            @Override
            public Resource convert(String s) {
                return applicationContext.getResource(s);
            }
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.applicationContext = ac;
    }

}
