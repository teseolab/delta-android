package no.ntnu.mikaelr.delta.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TaskResponse {

    private List<String> response;
    private int projectId;
    private int taskId;
    private int questionId;

    public String toJson() {

        JSONObject json = new JSONObject();
        try {
            JSONArray jsonResponse = new JSONArray(response);
            json.put("response", jsonResponse);
            json.put("projectId", projectId);
            json.put("taskId", taskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public List<String> getResponse() {
        return response;
    }

    public void setResponse(List<String> response) {
        this.response = response;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}
