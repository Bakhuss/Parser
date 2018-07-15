package ru.bakhuss.parser.parser;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bakhuss.parser.ParserApplication;
import ru.bakhuss.parser.dao.AuthorBookPagesDao;
import ru.bakhuss.parser.dao.AuthorDao;
import ru.bakhuss.parser.model.Author;
import ru.bakhuss.parser.model.AuthorBookPages;
import ru.bakhuss.parser.service.AuthorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public void getAuthorHtml(Long fromAnd, Long beforeAnd) {
        AuthorDao authorDao = ParserApplication.context.getBean(AuthorDao.class);
//        Long startId = 1L;
//        Author startAuth = authorDao.findFirstByOrderByIdDesc();
//        if (startAuth != null)
//            startId = (startAuth.getId() + 1);
//        for (Long i = startId; i < 500000; i++) {
        for (Long i = fromAnd; i <= beforeAnd; i++) {
            if (authorDao.existsByLiveLibId(i)) {
                log.info("liveLibId: " + i + " exists");
                emptyAuthor(i);
                continue;
            }
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
                    emptyAuthor(i);
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

                if (ex.getStatusCode() == 404) {
                    try {
                        authorDao.save(emptyAuthor(i));
                    } catch (Exception exc) {
                        log.error("Error saving empty author" + exc);
                    }
                }
            } catch (IOException e) {
                log.error("IOException error" + e);
                i--;
                continue;
            }
        }
        log.info("end id: " + authorDao.findFirstByOrderByIdDesc().getId().toString());
    }

    private Author emptyAuthor(Long i) {
        Author a = new Author();
        a.setId(i);
        a.setLiveLibId(null);
        a.setBaseUrl(null);
        a.setHtml(null);
        return a;
    }

    public void getBookHtml() {
        Long writer = 268L;
        Long page = 2L;
        String urlAddition = "";
        String url;
        List<String> urlAdd = new ArrayList<>();
        urlAdd.add("works");
        urlAdd.add("alphabet");
        urlAddition = urlAdd.get(0);
        url = "https://www.livelib.ru/author/"
                + writer + "/"
                + urlAddition
                + "/listview/smalllist/~" + page;

        try {
            Long startId = 1L;
            AuthorBookPagesDao abpd = ParserApplication.context.getBean(AuthorBookPagesDao.class);
            System.out.println(abpd.count());
            AuthorBookPages startAuthorBookPages = abpd.findFirstByOrderByAuthorIdDesc();
            if (abpd.count() != 0) {
                startId = startAuthorBookPages.getAuthorId();
            }
            System.out.println("startId: " + startId);

            if (startAuthorBookPages != null)
                if (!startAuthorBookPages.getCheckAllPages()) {
                    writer = startAuthorBookPages.getAuthorLiveLibId();
                    urlAddition = startAuthorBookPages.getUrlAddition();
                    page = startAuthorBookPages.getCurrentPage() + 1;
                }

//            AuthorDao authorDao = ParserApplication.context.getBean(AuthorDao.class);
//            System.out.println("count: " + authorDao.count());
//            System.out.println(authorDao.find(92L).getLiveLibId());


            Document doc = Jsoup.connect(url).get();
//            String[] locUrl = doc.location().split("/");
//            System.out.println(doc.baseUri());
//            String nextPage = doc.getElementsByAttributeValueContaining("id", "list-page-next")
//                    .attr("id");
//            System.out.println("nextPage: " + nextPage.split("-")[4]);


        } catch (HttpStatusException ex) {
            log.error("id: " + page + "; status code: " + ex.getStatusCode());
            if (ex.getStatusCode() == 503) {
                log.error("Wait 5 min");
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            log.error("IOException error" + e);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Not next page");
        }
    }
}
