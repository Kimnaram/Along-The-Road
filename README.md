# 길따라 App Project

### 🚉 국내 여행을 위한 숙박, 교통, 지역 축제 정보 등을 제공해주는 어플리케이션  

> ### Database
```
CREATE TABLE users (
uid       varchar(200)  PRIMARY KEY,
email     varchar(100)  NOT NULL,
password  varchar(5000) NOT NULL,
name      varchar(30)   NOT NULL);


CREATE TABLE reviews (
postId  int           PRIMARY KEY AUTO_INCREMENT,
title   varchar(100)  NOT NULL,
content varchar(5000) NOT NULL,
uid     varchar(200)  NOT NULL,
image   longblob,
FOREIGN KEY(uid) REFERENCES users(uid) ON UPDATE CASCADE ON DELETE CASCADE);


CREATE TABLE recommend_review (
postId  int         NOT NULL,
uid     varchar(30) NOT NULL,
PRIMARY KEY(postId, uid),
FOREIGN KEY(postId) REFERENCES reviews(postId) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY(uid) REFERENCES users(uid) ON UPDATE CASCADE ON DELETE CASCADE);


CREATE TABLE plan (
uid         varchar(200)  PRIMARY KEY,
city        varchar(30)   NOT NULL,
start_date  date,
end_date    date,
stay        varchar(30),
hotel_name  varchar(30),
image       longblob,
url         varchar(100),
course      varchar(100),
FOREIGN KEY(uid) REFERENCES users(uid) ON UPDATE CASCADE ON DELETE CASCADE);

```
