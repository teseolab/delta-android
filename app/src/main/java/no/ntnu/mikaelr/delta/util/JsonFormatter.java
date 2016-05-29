package no.ntnu.mikaelr.delta.util;

import android.util.Log;
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
                String imageUri = jsonProject.getString("imageUri");
                if (imageUri.equals("null") || imageUri.equals("")){
                    imageUri = null;
                }
                project.setImageUri(imageUri);
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
                task.setTaskType(TaskType.valueOf(jsonTask.getString("taskType")));
                task.setOrder(jsonTask.getInt("order"));
                task.setFinished(jsonTask.getBoolean("finished"));

                String imageUri = jsonTask.getString("imageUri");
                if (imageUri.equals("null") || imageUri.equals("")){
                    imageUri = null;
                }
                task.setImageUri(imageUri);
                task.setLatitude((float) jsonTask.getDouble("latitude"));
                task.setLongitude((float) jsonTask.getDouble("longitude"));
                task.setHint(jsonTask.getString("hint"));
                task.setDescription(jsonTask.getString("description"));

                JSONArray jsonTaskQuestions = jsonTask.getJSONArray("taskQuestions");
                List<TaskQuestion> taskQuestions = new ArrayList<TaskQuestion>();
                for (int j = 0; j < jsonTaskQuestions.length(); j++) {
                    JSONObject jsonTaskQuestion = jsonTaskQuestions.getJSONObject(j);
                    TaskQuestion taskQuestion = new TaskQuestion();
                    taskQuestion.setId(jsonTaskQuestion.getInt("id"));
                    taskQuestion.setQuestion(jsonTaskQuestion.getString("question"));
                    JSONArray jsonTaskAlternatives = jsonTaskQuestion.getJSONArray("alternatives");
                    List<String> alternatives = new ArrayList<String>();
                    for (int k = 0; k < jsonTaskAlternatives.length(); k++) {
                        alternatives.add(jsonTaskAlternatives.getString(k));
                    }
                    taskQuestion.setAlternatives(alternatives);
                    taskQuestions.add(taskQuestion);
                }
                task.setQuestions(taskQuestions);
                tasks.add(task);
            }
        }

        catch (JSONException e) {
            Log.w("JsonFormatter", e.getMessage());
            return null;
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

    public static ArrayList<Achievement> formatAchievements(JSONArray json) {
        ArrayList<Achievement> achievements = new ArrayList<Achievement>();
        try {
            for (int i = 0; i < json.length(); i++) {
//                JSONObject jsonAchievement = json.getJSONObject(i);
//                Achievement achievement = new Achievement();
//                achievement.setName(jsonAchievement.getString("name"));
//                achievement.setDescription(jsonAchievement.getString("description"));
//                achievement.setBadgeName(jsonAchievement.getString("badgeName"));
                achievements.add(formatAchievement(json.getString(i)));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return achievements;
    }

    public static Achievement formatAchievement(String jsonString) {
        Achievement achievement = new Achievement();
        try {
            JSONObject jsonAchievement = new JSONObject(jsonString);
            achievement.setName(jsonAchievement.getString("name"));
            achievement.setDescription(jsonAchievement.getString("description"));
            achievement.setBadgeName(jsonAchievement.getString("badgeName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return achievement;
    }


}
