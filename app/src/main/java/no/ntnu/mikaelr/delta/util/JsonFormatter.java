package no.ntnu.mikaelr.delta.util;

import no.ntnu.mikaelr.delta.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CancellationException;

public class JsonFormatter {

    public static List<Project> formatProjects(JSONArray json) {

        List<Project> projects = new ArrayList<Project>();

        try {

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonProject = json.getJSONObject(i);
                Project project = new Project();
                project.setId(jsonProject.getInt("id"));
                project.setName(jsonProject.getString("name"));
                project.setLatitude((float)jsonProject.getDouble("latitude"));
                project.setLongitude((float)jsonProject.getDouble("longitude"));
                project.setDescription(jsonProject.getString("description"));
                projects.add(project);
            }
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return projects;
    }

    public static List<Task> formatTasks(JSONArray json) {

        List<Task> tasks = new ArrayList<Task>();

        try {

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonTask = json.getJSONObject(i);
                Task task = new Task();

                task.setId(jsonTask.getInt("id"));

                String taskTypeString = jsonTask.getString("taskType");
                if (taskTypeString.equals(TaskType.SCALE_TASK.name())) {
                    task.setTaskType(TaskType.SCALE_TASK);
                } else if (taskTypeString.equals(TaskType.TEXT_TASK.name())) {
                    task.setTaskType(TaskType.TEXT_TASK);
                }

                task.setLatitude((float) jsonTask.getDouble("latitude"));
                task.setLongitude((float) jsonTask.getDouble("longitude"));
                task.setHint(jsonTask.getString("hint"));

                JSONArray jsonDescriptions = jsonTask.getJSONArray("descriptions");
                ArrayList<String> descriptions = new ArrayList<String>();
                for (int j = 0; j < jsonDescriptions.length(); j++) {
                    descriptions.add((String) jsonDescriptions.get(j));
                }
                task.setDescriptions(descriptions);
                tasks.add(task);
            }
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public static List<Suggestion> formatSuggestions(JSONArray json) {

        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        try {

            for (int i = 0; i < json.length(); i++) {

                JSONObject jsonSuggestion = json.getJSONObject(i);

                Suggestion suggestion = new Suggestion();
                suggestion.setId(jsonSuggestion.getInt("id"));
                suggestion.setDate(DateFormatter.dateFromMillis(jsonSuggestion.getLong("date")));
                JSONObject jsonUser = jsonSuggestion.getJSONObject("user");
                String avatarUri = jsonUser.getString("avatarUri");
                if (avatarUri.equals("null") || avatarUri.equals("")){
                    avatarUri = null;
                }
                User user = new User(jsonUser.getInt("id"), jsonUser.getString("username"), avatarUri);
                suggestion.setUser(user);
                suggestion.setImageUri(jsonSuggestion.getString("imageUri"));
                suggestion.setTitle(jsonSuggestion.getString("title"));
                suggestion.setDetails(jsonSuggestion.getString("details"));
                suggestion.setAgreements(jsonSuggestion.getInt("agreements"));
                suggestion.setDisagreements(jsonSuggestion.getInt("disagreements"));
                suggestion.setAgrees(jsonSuggestion.getString("agrees"));
                suggestions.add(suggestion);
            }
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return suggestions;
    }

    public static ArrayList<Comment> formatComments(JSONArray json) {

        ArrayList<Comment> comments = new ArrayList<Comment>();

        try {

            for (int i = 0; i < json.length(); i++) {

                JSONObject jsonComment = json.getJSONObject(i);

                Comment comment = new Comment();
                comment.setId(jsonComment.getInt("id"));
                comment.setDate(DateFormatter.dateFromMillis(jsonComment.getLong("date")));
                comment.setComment(jsonComment.getString("comment"));
                JSONObject jsonUser = jsonComment.getJSONObject("user");
                String avatarUri = jsonUser.getString("avatarUri");
                if (avatarUri.equals("null") || avatarUri.equals("")){
                    avatarUri = null;
                }
                User user = new User(jsonUser.getInt("id"), jsonUser.getString("username"), avatarUri);
                comment.setUser(user);
                comments.add(comment);
            }
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return comments;
    }

}
