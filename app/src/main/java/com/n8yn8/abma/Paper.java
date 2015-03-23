package com.n8yn8.abma;

import java.util.Map;

/**
 * Created by Nate on 3/22/15.
 */
public class Paper {

    private String author;
    private String title;
    private String synopsis;

    public Paper(String author, String title, String synopsis) {
        this.author = author;
        this.title = title;
        this.synopsis = synopsis;
    }

    public Paper(Map<String, String> paperMap) {
        this.title = paperMap.get("Title");
        this.author = paperMap.get("Author");
        this.synopsis = paperMap.get("Abstract");
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    @Override
    public String toString() {
        return "Paper{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", synopsis='" + synopsis + '\'' +
                '}';
    }
}
