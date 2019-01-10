package ru.bakhuss.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ParserApplication extends SpringBootServletInitializer {

    public static ApplicationContext context;

    @Autowired
    public void context(ApplicationContext context) {
        ParserApplication.context = context;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ParserApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ParserApplication.class, args);
    }
}
