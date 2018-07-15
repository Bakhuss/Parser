package ru.bakhuss.parser.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
@Data
public class AuthorBookPages {

    @Id
    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "author_live_lib_id")
    private Long authorLiveLibId;

    @Version
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Integer version;

    @Column(name = "url_addition")
    private String urlAddition;

    @Column(name = "current_page")
    private Long currentPage;

    @Column(name = "check_all_pages")
    private Boolean checkAllPages;
}
