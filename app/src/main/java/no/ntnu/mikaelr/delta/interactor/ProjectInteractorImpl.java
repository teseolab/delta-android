package no.ntnu.mikaelr.delta.interactor;

import no.ntnu.mikaelr.delta.async_task.*;
import no.ntnu.mikaelr.delta.model.ProjectResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProjectInteractorImpl implements ProjectInteractor {

    public interface OnFinishedLoadingProjectsListener {
        void onFinishedLoadingProjects(JSONArray jsonArray);
    }

    @Override
    public void getProjects(OnFinishedLoadingProjectsListener listener) {
        String apiCall = "http://129.241.102.204:8080/projects";
        new ProjectsAsyncTask(apiCall, listener).execute();
    }

    public interface OnFinishedLoadingTasksListener {
        void onFinishedLoadingTasks(JSONArray jsonArray);
    }

    @Override
    public void getTasks(int projectId, OnFinishedLoadingTasksListener listener) {
        String apiCall = "http://129.241.102.204:8080/projects/" + projectId + "/tasks";
        new TasksAsyncTask(apiCall, listener).execute();
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

    public interface OnGetMissionForProjectIsCompletedByUser {
        void onGetMissionForProjectIsCompletedByUserSuccess(Boolean result);
        void onGetMissionForProjectIsCompletedByUserError(Integer errorCode);
    }
    @Override
    public void getMissionForProjectIsCompletedByUser(int projectId, int userId, OnGetMissionForProjectIsCompletedByUser listener) {
        String apiCall = "http://129.241.102.204:8080/projects/" + projectId + "/mission/user/" + userId + "/isCompleted";
        new MissionIsCompletedAsyncTask(apiCall, listener).execute();
    }

    public interface OnFinishedLoadingSuggestionsListener {
        void onFinishedLoadingSuggestionsSuccess(JSONArray jsonArray);
        void onFinishedLoadingSuggestionsError(Integer errorCode);
    }

    @Override
    public void getSuggestions(int projectId, OnFinishedLoadingSuggestionsListener listener) {
        String apiCall = "http://129.241.102.204:8080/projects/" + projectId + "/suggestions";
        new SuggestionsAsyncTask(apiCall, listener).execute();
    }

    public interface OnFinishedLoadingCommentsListener {
        void onFinishedLoadingCommentsSuccess(JSONArray jsonArray);
        void onFinishedLoadingCommentsError(Integer errorCode);
    }

    @Override
    public void getComments(int suggestionId, OnFinishedLoadingCommentsListener listener) {
        String apiCall = "http://129.241.102.204:8080/suggestions/" + suggestionId + "/comments";
        new CommentsAsyncTask(apiCall, listener).execute();
    }

    public interface OnPostAgreementListener {
        void onPostAgreementSuccess(int agreements, int disagreements);
        void onPostAgreementError(Integer errorCode);
    }

    @Override
    public void postAgreement(int suggestionId, OnPostAgreementListener listener) {
        String apiCall = "http://129.241.102.204:8080/suggestions/" + suggestionId + "/agree";
        new AgreementAsyncTask(apiCall, listener).execute();
    }

    public interface OnPostDisagreementListener {
        void onPostDisagreementSuccess(int agreements, int disagreements);
        void onPostDisagreementError(Integer errorCode);
    }

    @Override
    public void postDisagreement(int suggestionId, OnPostDisagreementListener listener) {
        String apiCall = "http://129.241.102.204:8080/suggestions/" + suggestionId + "/disagree";
        new DisagreementAsyncTask(apiCall, listener).execute();
    }

}
