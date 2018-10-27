create table author (
       id bigint not null,
        base_url varchar(255),
        html varchar (300000),
        live_lib_id bigint,
        version integer,
        primary key (id)
    );

    create table author_book_pages (
       author_id bigint not null,
        author_live_lib_id bigint,
        check_all_pages boolean,
        current_page bigint,
        url_addition varchar(255),
        version integer,
        primary key (author_id)
    );

    create table book (
       live_lib_id bigint not null,
        base_url varchar(255),
        html varchar(300000),
        version integer,
        primary key (live_lib_id)
    );