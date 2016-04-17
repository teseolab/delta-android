package no.ntnu.mikaelr.delta.model;

import no.ntnu.mikaelr.delta.util.DateFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Suggestion implements Serializable {

    private Integer id;
    private Date date;
    private String imageUri;
    private String title;
    private String details;
    private Integer agreements;
    private Integer disagreements;
    private String agrees;

    private User user;
    private List<Comment> comments;
    private Integer projectId;

    public Suggestion() {}

    public String toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("details", details);
            json.put("date", DateFormatter.millisFromDate(date));
            json.put("imageUri", imageUri);
            json.put("projectId", projectId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

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

    public String getAgrees() {
        return agrees;
    }

    public void setAgrees(String agrees) {
        this.agrees = agrees;
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

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
}
