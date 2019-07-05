
create table if not exists app_origin(
    id bigserial not null constraint app_origin_pkey primary key,
    name varchar(255) not null
);
alter table app_origin owner to clyde;


create table if not exists app_style(
    id bigserial not null constraint app_style_pkey primary key,
    name varchar(255) not null
);
alter table app_style owner to clyde;


create table if not exists app_genre(
    id bigserial not null constraint app_genre_pkey primary key,
    name varchar(255) not null
);
alter table app_genre owner to clyde;


create table if not exists app_book
(
    id bigserial not null constraint app_book_pkey primary key,
    title varchar(255) not null,
    author varchar(255) not null,
    price int not null,
    vendor_code varchar(255) not null constraint uk_app_book_vendor_code unique,
    origin_id bigint not null constraint fk_book_app_origin references app_origin,
    style_id bigint not null constraint fk_book_app_style references app_style,
    genre_id bigint not null constraint fk_book_app_genre references app_genre
);
alter table app_book owner to clyde;


create table if not exists app_user(
    id bigserial not null constraint app_user_pkey primary key,
    user_uuid varchar(255) not null,
    username varchar(255),
    email varchar(255),
    passwordHash varchar(255),
    role varchar(255),
    address varchar(255),
    phone_number varchar(255)
);
alter table app_user owner to clyde;


create table if not exists app_order_item(
    amount int not null,
    status varchar(32) not null,
    book_id bigint not null constraint fk_order_app_book references app_book,
    user_id bigint not null constraint fk_order_app_user references app_user,
    constraint app_order_pkey primary key(book_id, user_id)
);
alter table app_order_item owner to clyde;


create table if not exists app_key_element(
    id bigserial not null constraint app_key_element_pkey primary key,
    element smallint not null,
    element_index smallint not null
);
alter table app_key_element owner to clyde;
