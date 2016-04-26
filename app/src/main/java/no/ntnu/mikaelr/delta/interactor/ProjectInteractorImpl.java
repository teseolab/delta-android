package no.ntnu.mikaelr.delta.interactor;

import no.ntnu.mikaelr.delta.async_task.*;
import no.ntnu.mikaelr.delta.model.ProjectResponse;
import no.ntnu.mikaelr.delta.model.Suggestion;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProjectInteractorImpl implements ProjectInteractor {

    public interface OnFinishedLoadingProjectsListener {
        void onFinishedLoadingProjects(JSONArray jsonArray);
        // TODO: onFinishedLoadingProjectsError
    }

    @Override
    public void getProjects(OnFinishedLoadingProjectsListener listener) {
        String apiCall = "http://129.241.102.204:8080/projects";
        new GetProjectsAsyncTask(apiCall, listener).execute();
    }

    public interface OnFinishedLoadingTasksListener {
        void onFinishedLoadingTasks(JSONArray jsonArray);
        // TODO: onFinishedLoadingTasksError
    }

    @Override
    public void getTasks(int projectId, OnFinishedLoadingTasksListener listener) {
        String apiCall = "http://129.241.102.204:8080/projects/" + projectId + "/tasks";
        new GetTasksAsyncTask(apiCall, listener).execute();
    }

    public interface OnPostProjectResponseListener {
        void onPostProjectResponseSuccess(JSONObject jsonObject);
        void onPostProjectResponseError(Integer errorCode);
    }
    @Override
    public void postResponse(ProjectResponse projectResponse, OnPostProjectResponseListener listener) {
        String apiCall = "http://129.241.102.204:8080/projects/" + projectResponse.getProjectId() + "/responses";
        new PostResponseAsyncTask(apiCall, projectResponse.toJson(), listener).execute();
    }

    public interface OnPostFinishedMission {
        void onPostFinishedMissionSuccess();
        void onPostFinishedMissionError(int errorCode);
    }

    @Override
    public void postFinishedMission(int projectId, OnPostFinishedMission listener) {
        String apiCall = "http://129.241.102.204:8080/projects/" + projectId + "/mission/complete";
        new PostFinishedMissionAsyncTask(apiCall, listener).execute();
    }
    public interface OnGetMissionForProjectIsCompletedByUser {
        void onGetMissionForProjectIsCompletedByUserSuccess(Boolean result);
        void onGetMissionForProjectIsCompletedByUserError(Integer errorCode);
    }

    @Override
    public void getMissionForProjectIsCompletedByUser(int projectId, OnGetMissionForProjectIsCompletedByUser listener) {
        String apiCall = "http://129.241.102.204:8080/projects/" + projectId + "/mission/isCompleted";
        new GetMissionIsCompletedAsyncTask(apiCall, listener).execute();
    }
    public interface OnFinishedLoadingSuggestionsListener {
        void onFinishedLoadingSuggestionsSuccess(JSONArray jsonArray);
        void onFinishedLoadingSuggestionsError(Integer errorCode);

    }

    @Override
    public void getSuggestions(int projectId, OnFinishedLoadingSuggestionsListener listener) {
        String apiCall = "http://129.241.102.204:8080/projects/" + projectId + "/suggestions";
        new GetSuggestionsAsyncTask(apiCall, listener).execute();
    }
    public interface OnFinishedLoadingCommentsListener {
        void onFinishedLoadingCommentsSuccess(JSONArray jsonArray);
        void onFinishedLoadingCommentsError(Integer errorCode);

    }

    @Override
    public void getComments(int suggestionId, OnFinishedLoadingCommentsListener listener) {
        String apiCall = "http://129.241.102.204:8080/suggestions/" + suggestionId + "/comments";
        new GetCommentsAsyncTask(apiCall, listener).execute();
    }
    public interface OnFinishedLoadingTopList {
        void onLoadTopListSuccess(JSONArray jsonArray);
        void onLoadTopListError(int errorCode);

    }
    @Override
    public void getTopList(OnFinishedLoadingTopList listener) {
        String apiCall = "http://129.241.102.204:8080/users";
        new GetTopListAsyncTask(apiCall, listener).execute();
    }

    public interface OnGetUserListener {
        void onGetUserSuccess(JSONObject jsonObject);
        void onGetUserError(int errorCode);
    }

    @Override
    public void getUser(int userId, OnGetUserListener listener) {
        String apiCall = "http://129.241.102.204:8080/users/" + userId;
        new GetUserAsyncTask(apiCall, listener).execute();
    }
    public interface OnPostAgreementListener {
        void onPostAgreementSuccess(int agreements, int disagreements);
        void onPostAgreementError(Integer errorCode);
    }
    @Override
    public void postAgreement(int suggestionId, OnPostAgreementListener listener) {
        String apiCall = "http://129.241.102.204:8080/suggestions/" + suggestionId + "/agree";
        new PostAgreementAsyncTask(apiCall, listener).execute();
    }
    public interface OnPostDisagreementListener {
        void onPostDisagreementSuccess(int agreements, int disagreements);
        void onPostDisagreementError(Integer errorCode);
    }
    @Override
    public void postDisagreement(int suggestionId, OnPostDisagreementListener listener) {
        String apiCall = "http://129.241.102.204:8080/suggestions/" + suggestionId + "/disagree";
        new PostDisagreementAsyncTask(apiCall, listener).execute();
    }
    public interface OnPostSuggestionListener {
        void onPostSuggestionSuccess(JSONObject jsonSuggestion);

        void onPostSuggestionError(int errorCode);
    }
    @Override
    public void postSuggestion(Suggestion suggestion, OnPostSuggestionListener listener) {
        String apiCall = "http://129.241.102.204:8080/suggestions";
        new PostSuggestionAsyncTask(apiCall, suggestion.toJson(), listener).execute();
    }
    public interface OnPostCommentListener {
        void onPostCommentSuccess(JSONArray jsonArray);

        void onPostCommentError(int errorCode);

    }

    @Override
    public void postComment(String comment, int suggestionId, OnPostCommentListener listener) {
        String apiCall = "http://129.241.102.204:8080/suggestions/" + suggestionId + "/comments";
        new PostCommentAsyncTask(apiCall, comment, listener).execute();
    }


}
