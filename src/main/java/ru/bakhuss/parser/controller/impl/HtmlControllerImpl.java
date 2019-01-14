package ru.bakhuss.parser.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bakhuss.parser.controller.HtmlController;
import ru.bakhuss.parser.parser.LiveLib;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/html", produces = APPLICATION_JSON_VALUE)
public class HtmlControllerImpl implements HtmlController {
    private final Logger log = LoggerFactory.getLogger(HtmlControllerImpl.class);

    @Override
    @RequestMapping(value = "/author")
    public String getAuthorHtml(@RequestParam Long fromAnd,
                                @RequestParam Long beforeAnd) {
        new Thread(() -> {
            log.info("start: fromAnd = " + fromAnd + " beforeAnd = " + beforeAnd);
            new LiveLib().getAuthorHtml(fromAnd, beforeAnd);
            log.info("end: fromAnd = " + fromAnd + " beforeAnd = " + beforeAnd);
        }).start();
        return "start";
    }
}
