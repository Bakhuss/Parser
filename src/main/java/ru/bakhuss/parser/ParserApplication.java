package ru.bakhuss.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.bakhuss.parser.parser.LiveLib;

@SpringBootApplication
public class ParserApplication extends SpringBootServletInitializer {

    public static ApplicationContext context;

    @Autowired
    public void context(ApplicationContext context) {
        ParserApplication.context = context;
    }

    @Bean
    public LiveLib liveLib() {
     return new LiveLib();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ParserApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ParserApplication.class, args);
        context.getBean(LiveLib.class).getAuthorHtml(1L,100L);
//        new LiveLib().getAuthorHtml(1L, 100L);
    }
}
