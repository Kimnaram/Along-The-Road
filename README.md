# 길따라 App Project
## 🚉 국내 여행을 위한 숙박, 교통, 지역 축제 정보 등을 제공해주는 어플리케이션

> Database
```
CREATE TABLE reviews (
postId  int           primary key auto_increment,
title   varchar(100)  not null,
content varchar(5000) not null,
name    varchar(30)   not null,
uid     varchar(200)  not null,
image   longblob);
```
