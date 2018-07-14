package ru.bakhuss.parser.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakhuss.parser.dao.AuthorBookPagesDao;
import ru.bakhuss.parser.dao.BookDao;
import ru.bakhuss.parser.model.Book;
import ru.bakhuss.parser.service.BookService;

@Service
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
public class BookServiceImpl implements BookService {
    private final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookDao bookDao;
    private final AuthorBookPagesDao authorBookPagesDao;

    @Autowired
    public BookServiceImpl(BookDao bookDao, AuthorBookPagesDao authorBookPagesDao) {
        this.bookDao = bookDao;
        this.authorBookPagesDao = authorBookPagesDao;
    }


    @Override
    @Transactional
    public boolean setBookHtml(Book book) {
        try {
            bookDao.save(book);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
