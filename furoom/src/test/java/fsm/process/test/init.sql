drop table if exists UserAccount;
drop table if exists User;
drop table if exists OperationAction;
drop table if exists Operation;
drop table if exists Organization;
drop table if exists ProductAction;
drop table if exists Product;
drop table if exists StateLog;
drop table if exists AgentProduct;
create table User
(
   id                   integer not null auto_increment,
   name                 varchar(50) not null,
   sex					varchar(20) not null,
   tel                  varchar(50) not null unique,
   email                varchar(50),
   loginId              varchar(255) not null unique,
   password             varchar(255) not null,
   accountid            integer,
   appendixId			integer,
   organizationId		integer,
   province				varchar(50),
   city					varchar(50),
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
create table ProductAction
(
   id                   integer not null auto_increment,
   productId            integer not null,
   title				varchar(200),
   description          varchar(2000),
   estimatedTime        int not null,
   ind			        int not null default 1,
   primary key (id)
);
create table Organization
(
   id                   integer not null auto_increment,
   code					varchar(50),
   name			        varchar(100),
   fullName		        varchar(500),
   license				varchar(100),
   primary key (id)
);
create table Product
(
   id                   integer not null auto_increment,
   title				varchar(100) not null,
   state				int not null default 0,
   organizationId		integer not null,
   loanType				int not null default 0,
   cityCode				varchar(50),
   profits		        varchar(2000),
   requires		        varchar(2000),
   primary key (id)
);
create table OperationAction
(
   id                   integer not null auto_increment,
   OperationId          integer not null,
   title				varchar(200),
   description          varchar(2000),
   createTime        	BIGINT not null,
   lastmodifyTime		BIGINT not null,
   state				int not null default 0,
   primary key (id)
);
create table Operation
(
	id                  integer not null auto_increment,
	state				int not null default 0,
	productId           integer,
	userId           	integer not null,
	agentId				integer,
	loanType			int not null default 0,
	amount				int not null default 0,
	period				int not null default 0,
	province			varchar(50),
	city				varchar(50),
	createtime        	BIGINT not null,
	lastmodifytime      BIGINT not null,
	primary key (id)
);
create table StateLog
(
   ID                   integer not null auto_increment,
   type                 int not null comment '0：user
            1：product
            2：productaction',
   createtime           bigint not null,
   source               int not null,
   target               int not null,
   refid                integer not null,
   primary key (ID)
);
create table AgentProduct
(
	userId				integer not null,
	productId           integer not null,
	primary key (userId, productId)
);
