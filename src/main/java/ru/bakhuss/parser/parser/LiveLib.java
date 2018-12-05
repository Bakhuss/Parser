package ru.bakhuss.parser.parser;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bakhuss.parser.ParserApplication;
import ru.bakhuss.parser.dao.AuthorBookPagesDao;
import ru.bakhuss.parser.dao.AuthorDao;
import ru.bakhuss.parser.dao.BookDao;
import ru.bakhuss.parser.model.Author;
import ru.bakhuss.parser.model.AuthorBookPages;
import ru.bakhuss.parser.model.Book;
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

    public void getBookBaseUrl(Long fromAnd, Long beforeAnd) {
        Long authorId = 1L;
        Long liveLibId = 1L;
        Long currentPage = 1L;
        String urlAddition = "works";
        String url = "";

        printValues(fromAnd, beforeAnd, authorId, liveLibId, currentPage, urlAddition, url);

        AuthorBookPagesDao abpd = ParserApplication.context.getBean(AuthorBookPagesDao.class);
        System.out.println(abpd.count());
        AuthorDao authorDao = ParserApplication.context.getBean(AuthorDao.class);
        System.out.println(authorDao.count());

        AuthorBookPages authorBookPages = abpd.findFirstByOrderByAuthorIdDesc();
//        if (abpd.count() != 0) {
//            authorId = authorBookPages.getAuthorId();
//            if (!authorBookPages.getCheckAllPages()) {
//                liveLibId = authorBookPages.getAuthorLiveLibId();
//            }
//        }

        printValues(fromAnd, beforeAnd, authorId, liveLibId, currentPage, urlAddition, url);


        List<String> urlAdd = new ArrayList<>();
        urlAdd.add("works");
        urlAdd.add("alphabet");
        urlAddition = urlAdd.get(0);
        url = "https://www.livelib.ru/author/"
                + liveLibId + "/"
                + urlAddition
                + "/listview/smalllist/~"
                + currentPage;
        System.out.println(url);

        printValues(fromAnd, beforeAnd, authorId, liveLibId, currentPage, urlAddition, url);

        try {
            AuthorDao ad = ParserApplication.context.getBean(AuthorDao.class);
            Document document = Jsoup.connect(url).get();
            System.out.println("title: " + document.title());
            System.out.println("size: " + document.html().length());

            Elements booksUrls = document.getElementsByClass("brow-book-name");
            System.out.println("booksUrls size: " + booksUrls.size());
            BookDao bd = ParserApplication.context.getBean(BookDao.class);
            for (Element e : booksUrls) {
                String href = e.attr("href");
                System.out.println("string: " + href);
                Book book = new Book();
                book.setBaseUrl(href);
                String bookLiveLibId = href.split("/")[2].split("-")[0];
                book.setLiveLibId(Long.parseLong(bookLiveLibId));
                Book newBook = bd.save(book);
            }

            String[] locUrl = document.location().split("/");
            System.out.println(document.baseUri());
            String nextPage = document
                    .getElementsByAttributeValueContaining("id", "list-page-next")
                    .attr("id");
            System.out.println("nextPage: " + nextPage.split("-")[4]);



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

    private void printValues(Long fromAnd, Long beforeAnd, Long authorId, Long liveLibId, Long currentPage, String urlAddition, String url) {
        log.info("fromAnd: " + fromAnd + " | " + "beforeAnd: " + beforeAnd);
        log.info("authorId: " + authorId);
        log.info("liveLibId: " + liveLibId);
        log.info("currentPage: " + currentPage);
        log.info("urlAddition: " + urlAddition);
        log.info("url: " + url);
    }
}