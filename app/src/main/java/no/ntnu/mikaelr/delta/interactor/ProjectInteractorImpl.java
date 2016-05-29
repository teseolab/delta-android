package no.ntnu.mikaelr.delta.interactor;

import android.graphics.Bitmap;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.mikaelr.delta.async_task.*;
import no.ntnu.mikaelr.delta.model.TaskResponse;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.util.Constants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class ProjectInteractorImpl implements ProjectInteractor {

    public interface OnGetProjectsListener {
        void onGetProjectsSuccess(JSONArray jsonArray);
        void onGetProjectsError(int errorCode);
    }

    @Override
    public void getProjects(OnGetProjectsListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/projects";
        new GetProjects2AsyncTask(apiCall, listener).execute();
    }

    public interface OnGetTasksListener {
        void onGetTasksSuccess(String response);
        void onGetTasksError(int errorCode);
    }

    @Override
    public void getTasks(int projectId, OnGetTasksListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/projects/" + projectId + "/tasks";
        new GetTasks2AsyncTask(apiCall, listener).execute();
    }

    public interface OnPostProjectResponseListener {
        void onPostProjectResponseSuccess();
        void onPostProjectResponseError(Integer errorCode);
    }
    @Override
    public void postResponse(List<TaskResponse> taskResponse, OnPostProjectResponseListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/projects/taskResponses";
        ObjectMapper mapper = new ObjectMapper();
        try {
            new PostResponseAsyncTask(apiCall, mapper.writeValueAsString(taskResponse), listener).execute();
        } catch (IOException e) {
            Log.w("ProjectInteractorImpl", e.getMessage());
        }
    }

    public interface OnPostFinishedMission {
        void onPostFinishedMissionSuccess();
        void onPostFinishedMissionError(int errorCode);
    }

    @Override
    public void postFinishedMission(int projectId, OnPostFinishedMission listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/projects/" + projectId + "/mission/complete";
        new PostFinishedMissionAsyncTask(apiCall, listener).execute();
    }
    public interface OnGetMissionForProjectIsCompletedByUser {
        void onGetMissionForProjectIsCompletedByUserSuccess(Boolean result);
        void onGetMissionForProjectIsCompletedByUserError(Integer errorCode);
    }

    @Override
    public void getMissionForProjectIsCompletedByUser(int projectId, OnGetMissionForProjectIsCompletedByUser listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/projects/" + projectId + "/mission/isCompleted";
        new GetMissionIsCompletedAsyncTask(apiCall, listener).execute();
    }
    public interface OnFinishedLoadingSuggestionsListener {
        void onFinishedLoadingSuggestionsSuccess(JSONArray jsonArray);
        void onFinishedLoadingSuggestionsError(Integer errorCode);

    }

    @Override
    public void getSuggestions(int projectId, OnFinishedLoadingSuggestionsListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/projects/" + projectId + "/suggestions";
        new GetSuggestionsAsyncTask(apiCall, listener).execute();
    }

    public interface OnFinishedLoadingCommentsListener {

        void onFinishedLoadingCommentsSuccess(JSONArray jsonArray);
        void onFinishedLoadingCommentsError(Integer errorCode);
    }
    @Override
    public void getComments(int suggestionId, OnFinishedLoadingCommentsListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/suggestions/" + suggestionId + "/comments";
        new GetCommentsAsyncTask(apiCall, listener).execute();
    }

    public interface OnGetSuggestionAchievementListener {
        void onGetSuggestionAchievementSuccess(String result);
        void onGetSuggestionAchievementError(int errorCode);
    }

    @Override
    public void getSuggestionAchievement(OnGetSuggestionAchievementListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/users/me/suggestions/achievement";
        new GetSuggestionAchievementAsyncTask(apiCall, listener).execute();
    }

    public interface OnGetCommentAchievementListener {
        void onGetCommentAchievementSuccess(String result);
        void onGetCommentAchievementError(int errorCode);
    }

    @Override
    public void getCommentAchievement(OnGetCommentAchievementListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "users/me/comments/achievement";
        new GetCommentAchievementAsyncTask(apiCall, listener).execute();
    }

    public interface OnFinishedLoadingTopList {
        void onLoadTopListSuccess(JSONArray jsonArray);
        void onLoadTopListError(int errorCode);

    }
    @Override
    public void getTopList(OnFinishedLoadingTopList listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/users";
        new GetTopListAsyncTask(apiCall, listener).execute();
    }

    public interface OnGetUserListener {
        void onGetUserSuccess(JSONObject jsonObject);
        void onGetUserError(int errorCode);
    }

    @Override
    public void getMe(OnGetUserListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/users/me";
        new GetUserAsyncTask(apiCall, listener).execute();
    }

    @Override
    public void getUser(int userId, OnGetUserListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/users/" + userId;
        new GetUserAsyncTask(apiCall, listener).execute();
    }

    public interface OnGetLogRecordsListener {
        void onGetLogRecordsSuccess(JSONArray jsonArray);
        void onGetLogRecordsError(int errorCode);
    }

    @Override
    public void getLogRecords(ProjectInteractorImpl.OnGetLogRecordsListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/users/me/logRecords";
        new GetLogRecordsAsyncTask(apiCall, listener).execute();
    }

    public interface OnPostAgreementListener {
        void onPostAgreementSuccess(int agreements, int disagreements);
        void onPostAgreementError(Integer errorCode);
    }
    @Override
    public void postAgreement(int suggestionId, OnPostAgreementListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/suggestions/" + suggestionId + "/agree";
        new PostAgreementAsyncTask(apiCall, listener).execute();
    }
    public interface OnPostDisagreementListener {
        void onPostDisagreementSuccess(int agreements, int disagreements);
        void onPostDisagreementError(Integer errorCode);
    }
    @Override
    public void postDisagreement(int suggestionId, OnPostDisagreementListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/suggestions/" + suggestionId + "/disagree";
        new PostDisagreementAsyncTask(apiCall, listener).execute();
    }
    public interface OnPostSuggestionListener {
        void onPostSuggestionSuccess(JSONObject jsonSuggestion);
        void onPostSuggestionError(int errorCode);
    }
    @Override
    public void postSuggestion(Suggestion suggestion, OnPostSuggestionListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/suggestions";
        new PostSuggestionAsyncTask(apiCall, suggestion.toJson(), listener).execute();
    }
    public interface OnPostCommentListener {
        void onPostCommentSuccess(JSONArray jsonArray);
        void onPostCommentError(int errorCode);
    }

    @Override
    public void postComment(String comment, int suggestionId, OnPostCommentListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/suggestions/" + suggestionId + "/comments";
        new PostCommentAsyncTask(apiCall, comment, listener).execute();
    }

    public interface OnPostImageListener {
        void onPostImageSuccess(String uri);
        void onPostImageError(int errorCode);
    }

    @Override
    public void uploadImage(byte[] byteArray, OnPostImageListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/images/upload";
        new PostImageAsyncTask(apiCall, byteArray, listener).execute();
    }

    public interface OnGetImageListener {
        void onGetImageSuccess(Bitmap image);
        void onGetImageError(int errorCode);
    }

    @Override
    public void getImage(String imageUri, OnGetImageListener listener) {
        new GetImageAsyncTask(imageUri, listener).execute();
    }

    public interface OnPutAvatarListener {
        void onPutAvatarSuccess(String result);
        void onPutAvatarError(int errorCode);
    }

    @Override
    public void putAvatar(String avatarUri, OnPutAvatarListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/users/me/avatar";
        new PutAvatarAsyncTask(apiCall, avatarUri, listener).execute();
    }

    public interface OnGetAchievementsListener {
        void onGetAchievementsSuccess(JSONArray jsonArray);
        void onGetAchievementsError(int errorCode);
    }

    @Override
    public void getMyAchievements(OnGetAchievementsListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/users/me/achievements";
        new GetAchievementsAsyncTask(apiCall, listener).execute();
    }

    @Override
    public void getUserAchievements(int userId, OnGetAchievementsListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/users/" + userId + "/achievements";
        new GetAchievementsAsyncTask(apiCall, listener).execute();
    }

    public interface OnPostMissionLocationsListener {
        void onPostMissionLocationsSuccess();
        void onPostMissionLocationsError(int errorCode);
    }

    @Override
    public void postMissionLocations(String locations, OnPostMissionLocationsListener listener) {
        String apiCall = "http://" + Constants.SERVER_URL + "/admin/postMissionLocations";
        new PostMissionLocationsAsyncTask(apiCall, locations, listener).execute();
    }


}
