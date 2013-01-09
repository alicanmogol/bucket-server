create table chat_message (
  id                        bigint not null,
  date                      timestamp,
  from_username             varchar(255),
  to_username               varchar(255),
  message_id                varchar(255),
  message_content           varchar(255),
  message_status            integer,
  constraint pk_chat_message primary key (id))
;

create table chat_user (
  id                        bigint not null,
  user_username             varchar(255),
  user_password             varchar(255),
  user_name                 varchar(255),
  user_surname              varchar(255),
  user_email                varchar(255),
  session_id                varchar(255),
  constraint pk_chat_user primary key (id))
;

create sequence chat_message_seq;

create sequence chat_user_seq;



