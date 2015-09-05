use fsm;
drop table if exists UserAccount;
drop table if exists User;
create table User
(
   id                   integer not null auto_increment,
   name                 varchar(50),
   tel                  varchar(50),
   email                varchar(50),
   loginId              varchar(255),
   password             varchar(255) not null,
   accountid            integer,
   appendixId			integer,
   createtime           BIGINT not null,
   privilege            int not null comment '00：普通用户
            01：vip1',
   primary key (id)
);
create table UserAccount
(
   id                   integer not null auto_increment,
   total                decimal(12,2) not null default 0,
   freeze               decimal(12,2) not null default 0,
   usable               decimal(12,2) not null default 0,
   used                 decimal(12,2) not null default 0,
   totalincome          decimal(12,2) not null default 0,
   expectedincome       decimal(12,2) not null default 0,
   primary key (id)
);