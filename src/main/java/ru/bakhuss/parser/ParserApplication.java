package ru.bakhuss.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.bakhuss.parser.parser.LiveLib;

@SpringBootApplication
public class ParserApplication {

    public static ApplicationContext context;

    @Autowired
    public void context(ApplicationContext context) {
        ParserApplication.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(ParserApplication.class, args);
        new LiveLib().getAuthorHtml(1717L, 2000L);
    }
}
