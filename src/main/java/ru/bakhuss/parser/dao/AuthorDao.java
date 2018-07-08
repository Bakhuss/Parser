package ru.bakhuss.parser.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bakhuss.parser.model.Author;

@Repository
public interface AuthorDao extends CrudRepository<Author, Long> {
    Author findFirstByOrderByIdDesc();
    boolean existsByLiveLibId(Long liveLibId);
}
