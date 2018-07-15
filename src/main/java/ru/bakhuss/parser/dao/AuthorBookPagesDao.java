package ru.bakhuss.parser.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bakhuss.parser.model.AuthorBookPages;

@Repository
public interface AuthorBookPagesDao extends CrudRepository<AuthorBookPages, Long> {
    AuthorBookPages findFirstByOrderByAuthorIdDesc();
}
