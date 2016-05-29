package no.ntnu.mikaelr.delta.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.ntnu.mikaelr.delta.util.TaskType;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Task implements Serializable {

    private Integer id;
    private Integer order;
    private boolean finished;
    private TaskType taskType;
    private String imageUri;
    private float latitude;
    private float longitude;
    private String hint;
    private String description;
    @JsonProperty("taskQuestions")
    private List<TaskQuestion> questions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TaskQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TaskQuestion> questions) {
        this.questions = questions;
    }
}
