package es.us.dp1.l6_3_24_25.Petris.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

	@Autowired
	GenericIdToEntityConverter idToEntityConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {

        registry.addConverter(idToEntityConverter);
    }

}
