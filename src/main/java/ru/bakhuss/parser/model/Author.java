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
public class Author {

    @Id
    private Long id;

    @Version
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Integer version;

    @Column(name = "live_lib_id")
    private Long liveLibId;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(length = 200000)
    private String html;


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "{id:" + getId() +
                ";baseUrl:" + getBaseUrl() +
                ";html:" + getHtml() +
                "}";
    }
}
