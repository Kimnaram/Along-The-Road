package com.example.along_the_road;

public class FirebaseID {
    private static String post ="post";
    private static String documentId = "documentId";
    private static String title ="title";
    private static String contents ="contents";

    public static String getPost() {
        return post;
    }

    public static void setPost(String post) {
        FirebaseID.post = post;
    }

    public static String getDocumentId() {
        return documentId;
    }

    public static void setDocumentId(String documentId) {
        FirebaseID.documentId = documentId;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        FirebaseID.title = title;
    }

    public static String getContents() {
        return contents;
    }

    public static void setContents(String contents) {
        FirebaseID.contents = contents;
    }

}
