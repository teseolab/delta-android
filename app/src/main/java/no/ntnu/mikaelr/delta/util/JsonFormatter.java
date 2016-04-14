package no.ntnu.mikaelr.delta.util;

import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.model.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
                if (taskTypeString.equals(Task.TaskType.SCALE_TASK.name())) {
                    task.setTaskType(Task.TaskType.SCALE_TASK);
                } else if (taskTypeString.equals(Task.TaskType.TEXT_TASK.name())) {
                    task.setTaskType(Task.TaskType.TEXT_TASK);
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

}
