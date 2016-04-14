package no.ntnu.mikaelr.delta.model;

import java.util.Date;
import java.util.List;

public class Suggestion {

    private Integer id;
    private Date date;
    private String imageUri;
    private String title;
    private String details;
    private Integer agreements;
    private Integer disagreements;

    private User user;
    private List<Comment> comments;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getAgreements() {
        return agreements;
    }

    public void setAgreements(Integer agreements) {
        this.agreements = agreements;
    }

    public Integer getDisagreements() {
        return disagreements;
    }

    public void setDisagreements(Integer disagreements) {
        this.disagreements = disagreements;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
