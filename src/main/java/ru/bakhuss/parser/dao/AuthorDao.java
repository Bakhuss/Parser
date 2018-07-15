package ru.bakhuss.parser.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bakhuss.parser.model.Author;

import java.util.List;

@Repository
public interface AuthorDao extends CrudRepository<Author, Long> {
    Author findFirstByOrderByIdDesc();
    boolean existsByLiveLibId(Long liveLibId);

    @Query("select a from Author a where a.id= :id")
    Author find(Long id);

    List<Author> findAllByIdLessThanEqual(Long id);
}
