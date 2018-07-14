package ru.bakhuss.parser.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakhuss.parser.dao.AuthorDao;
import ru.bakhuss.parser.model.Author;
import ru.bakhuss.parser.service.AuthorService;

@Service
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao authorDao;

    @Autowired
    public AuthorServiceImpl(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Override
    @Transactional
    public boolean setAuthorHtml(Author author) {
        try {
            authorDao.save(author);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAuthorCount() {
        return authorDao.count();
    }
}
