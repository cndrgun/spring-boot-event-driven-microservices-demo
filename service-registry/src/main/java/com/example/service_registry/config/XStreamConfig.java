package com.example.service_registry.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XStreamConfig {
    @Bean
    public XStream xStream() {
        return new XStream(new PureJavaReflectionProvider());
    }
}
