package ru.bakhuss.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ParserApplication {

    public static ApplicationContext context;

    @Autowired
    public void context(ApplicationContext context) {
        ParserApplication.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(ParserApplication.class, args);
//        Scanner sc = new Scanner(System.in);
//        System.out.println("fromAnd: ");
//        Long fromAnd = sc.nextLong();
//        System.out.println("beforeAnd: ");
//        Long beforeAnd = sc.nextLong();
//        new LiveLib().getBookBaseUrl(1L, 1L);
    }
}