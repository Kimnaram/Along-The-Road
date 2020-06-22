package com.example.along_the_road;


public class ParseItem {
    private String imgUrl; // 축제 사진 가져오는 이미지 링크
    private String title; // 축제 이름

    public ParseItem() {
    }

    public ParseItem(String imgUrl, String title) {
        this.imgUrl = imgUrl;
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
