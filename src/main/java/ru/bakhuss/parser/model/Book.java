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
public class Book {

    @Id
    @Column(name = "live_lib_id")
    private Long liveLibId;

    @Version
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Integer version;

    @Column(name = "base_url")
    private String baseUrl;

    @Column
    private String html;


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "{liveLibId:" + getLiveLibId() +
                ";baseUrl:" + getBaseUrl() +
                ";html:" + getHtml() +
                "}";
    }
}
