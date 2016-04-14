package no.ntnu.mikaelr.delta.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectResponse {

    private ArrayList<String> response;
    private int userId;
    private int projectId;
    private int taskId;

    public String toJson() {

        JSONObject json = new JSONObject();
        try {
            JSONArray jsonResponse = new JSONArray(response);
            json.put("response", jsonResponse);
            json.put("userId", userId);
            json.put("projectId", projectId);
            json.put("taskId", taskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public ArrayList<String> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<String> response) {
        this.response = response;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
}
