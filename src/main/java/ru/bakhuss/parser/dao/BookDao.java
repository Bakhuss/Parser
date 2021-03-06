package ru.bakhuss.parser.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bakhuss.parser.model.Book;

@Repository
public interface BookDao extends CrudRepository<Book, Long> {
}
