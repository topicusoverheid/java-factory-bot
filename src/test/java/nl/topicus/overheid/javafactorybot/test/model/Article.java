package nl.topicus.overheid.javafactorybot.test.model;

import lombok.Data;

import java.util.Date;

@Data
public class Article {
    private String title;
    private String content;
    private Date creationDate;
    private User author;
}
