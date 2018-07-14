package ru.bakhuss.parser.service;

import ru.bakhuss.parser.model.Author;

public interface AuthorService {
    boolean setAuthorHtml(Author author);
    Long getAuthorCount();
}
