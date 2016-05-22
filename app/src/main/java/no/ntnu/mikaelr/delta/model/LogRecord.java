package no.ntnu.mikaelr.delta.model;

import no.ntnu.mikaelr.delta.util.DateFormatter;
import no.ntnu.mikaelr.delta.util.LogRecordType;
import no.ntnu.mikaelr.delta.util.TaskType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogRecord {

    private Integer id;
    private Date date;
    private String description;
    private LogRecordType type;
    private Integer generatedScore;

    private Integer suggestionId;
    private Integer projectId;
    private String achievementBadgeName;

    public static List<LogRecord> fromJsonArray(JSONArray jsonArray) {
        List<LogRecord> logRecords = new ArrayList<LogRecord>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonLogRecord = jsonArray.getJSONObject(i);
                logRecords.add(fromJsonObject(jsonLogRecord));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return logRecords;
    }

    private static LogRecord fromJsonObject(JSONObject jsonObject) throws JSONException {
        LogRecord logRecord = new LogRecord();
        logRecord.setId(jsonObject.getInt("id"));
        logRecord.setDate(DateFormatter.dateFromMillis(jsonObject.getLong("date")));
        logRecord.setDescription(jsonObject.getString("description"));
        logRecord.setType(LogRecordType.valueOf(jsonObject.getString("type")));
        logRecord.setGeneratedScore(jsonObject.getInt("generatedScore"));
        if (logRecord.getType() == LogRecordType.ACHIEVEMENT) {
            logRecord.setAchievementBadgeName(jsonObject.getString("achievementBadgeName"));
        }
        return logRecord;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LogRecordType getType() {
        return type;
    }

    public void setType(LogRecordType type) {
        this.type = type;
    }

    public Integer getGeneratedScore() {
        return generatedScore;
    }

    public void setGeneratedScore(Integer generatedScore) {
        this.generatedScore = generatedScore;
    }

    public Integer getSuggestionId() {
        return suggestionId;
    }

    public void setSuggestionId(Integer suggestionId) {
        this.suggestionId = suggestionId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getAchievementBadgeName() {
        return achievementBadgeName;
    }

    public void setAchievementBadgeName(String achievementBadgeName) {
        this.achievementBadgeName = achievementBadgeName;
    }
}
