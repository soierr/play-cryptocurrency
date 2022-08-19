create table currency (code integer not null auto_increment,
currency_code char(30) not null,
currency_date timestamp not null,
price decimal(16,2) not null,
primary key (code));


create table file_info (code integer not null auto_increment,
filename varchar(128) not null,
loaded datetime not null,
created datetime not null,
modified datetime not null,
primary key (code));