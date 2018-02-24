package nl.topicus.overheid.javafactorybot.test.model;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private Article article;
    private User author;
    private String content;
    private Date creationDate;
}
