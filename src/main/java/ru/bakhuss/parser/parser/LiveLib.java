package ru.bakhuss.parser.parser;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bakhuss.parser.ParserApplication;
import ru.bakhuss.parser.dao.AuthorDao;
import ru.bakhuss.parser.model.Author;
import ru.bakhuss.parser.service.AuthorService;

import java.io.IOException;

public class LiveLib {
    private final Logger log = LoggerFactory.getLogger(LiveLib.class);

    public Document getDocument(String writer) throws IOException {
        String url;
        url = "https://www.livelib.ru/author/" + writer;
        Document doc = Jsoup.connect(url).get();
        if (doc.title().startsWith("404")) {
            System.out.println("false: " + writer);
            return null;
        }
        return doc;
    }

    public void getAuthorHtml() {
        AuthorDao authorDao = ParserApplication.context.getBean(AuthorDao.class);
        Long startId = 1L;
        Author startAuth = authorDao.findFirstByOrderByIdDesc();
        if (startAuth != null)
            startId = (startAuth.getId() + 1);
        for (Long i = startId; i < 500000; i++) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String url;
            url = "https://www.livelib.ru/author/" + i;
            try {
                Document doc = Jsoup.connect(url).get();
                String[] locUrl = doc.location().split("/");
                String authId = locUrl[locUrl.length - 1].split("-")[0];

                if (authId.contains("ratelimitcaptcha")) {
                    log.info("i: " + i + "; ratelimitcaptcha: wait 5 min");
                    try {
                        Thread.sleep(300000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                    continue;
                }

                if (authorDao.existsByLiveLibId(Long.parseLong(authId))) {
                    log.info("liveLibId: " + authId + " exists");
                    continue;
                }
                if (authorDao.existsById(Long.parseLong(authId))) {
                    log.info("liveLibId: " + authId + " exists");
                    continue;
                }

                Author author = new Author();
                author.setId(i);
                author.setLiveLibId(Long.parseLong(authId));
                author.setBaseUrl(doc.baseUri());
                author.setHtml(doc.outerHtml());
                AuthorService authorService = ParserApplication.context.getBean(AuthorService.class);
                System.out.println(authorService.setAuthorHtml(author));
            } catch (HttpStatusException ex) {
                log.error("id: " + i + "; status code: " + ex.getStatusCode());
                if (ex.getStatusCode() == 503) {
                    log.error("Wait 5 min");
                    try {
                        Thread.sleep(300000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                    continue;
                }
            } catch (IOException e) {
                log.error("IOException error" + e);
            }
        }
        log.info(authorDao.findFirstByOrderByIdDesc().getId().toString());
    }
}
