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

    public void getAuthorHtml(Long fromAnd, Long beforeAnd) {
        AuthorDao authorDao = ParserApplication.context.getBean(AuthorDao.class);
        for (Long i = fromAnd; i <= beforeAnd; i++) {
            if (authorDao.existsByLiveLibId(i)) {
                log.info("liveLibId: " + i + " exists");
                try {
                    authorDao.save(emptyAuthor(i));
                } catch (Exception exc) {
                    log.error("Error saving empty author" + exc);
                    i--;
                }
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
                    try {
                        authorDao.save(emptyAuthor(i));
                    } catch (Exception exc) {
                        log.error("Error saving empty author" + exc);
                        i--;
                    }
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
                        i--;
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
/*
    public void getBookHtml() {

        Long authorId = 1L;
        Long liveLibId = 1L;
        Long currentPage = 1L;
        String urlAddition = "works";
        String url;

        AuthorBookPagesDao abpd = ParserApplication.context.getBean(AuthorBookPagesDao.class);
        System.out.println(abpd.count());
        AuthorBookPages authorBookPages = abpd.findFirstByOrderByAuthorIdDesc();
        if (abpd.count() != 0) {
            authorId = authorBookPages.getAuthorId();
            if (!authorBookPages.getCheckAllPages()) {
                liveLibId = authorBookPages.getAuthorLiveLibId();
            }
        }
        System.out.println("authorId: " + authorId);
        System.out.println("liveLibId: " + liveLibId);

        List<String> urlAdd = new ArrayList<>();
        urlAdd.add("works");
        urlAdd.add("alphabet");
        urlAddition = urlAdd.get(0);
        url = "https://www.livelib.ru/author/"
                + liveLibId + "/"
                + urlAddition
                + "/listview/smalllist/~" + currentPage;
        System.out.println(url);

        try {
//            Long authorId = 1L;
//            AuthorBookPagesDao abpd = ParserApplication.context.getBean(AuthorBookPagesDao.class);
//            System.out.println(abpd.count());
//            AuthorBookPages authorBookPages = abpd.findFirstByOrderByAuthorIdDesc();
//            if (abpd.count() != 0) {
//                authorId = authorBookPages.getAuthorId();
//            }
//            System.out.println("authorId: " + authorId);

            if (authorBookPages != null)
                if (!authorBookPages.getCheckAllPages()) {
                    writer = authorBookPages.getAuthorLiveLibId();
                    urlAddition = authorBookPages.getUrlAddition();
                    currentPage = authorBookPages.getCurrentPage() + 1;
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
            log.error("id: " + currentPage + "; status code: " + ex.getStatusCode());
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
*/
}