package no.ntnu.mikaelr.delta.interactor;

import no.ntnu.mikaelr.delta.model.TaskResponse;
import no.ntnu.mikaelr.delta.model.Suggestion;

import java.util.List;

public interface ProjectInteractor {

    void getProjects(ProjectInteractorImpl.OnGetProjectsListener listener);
    void getTasks(int projectId, ProjectInteractorImpl.OnGetTasksListener listener);
    void getSuggestions(int projectId, ProjectInteractorImpl.OnFinishedLoadingSuggestionsListener listener);
    void getSuggestionAchievement(ProjectInteractorImpl.OnGetSuggestionAchievementListener listener);
    void getComments(int suggestionId, ProjectInteractorImpl.OnFinishedLoadingCommentsListener listener);
    void getCommentAchievement(ProjectInteractorImpl.OnGetCommentAchievementListener listener);
    void getTopList(ProjectInteractorImpl.OnFinishedLoadingTopList listener);
    void getMe(ProjectInteractorImpl.OnGetUserListener listener);
    void getUser(int userId, ProjectInteractorImpl.OnGetUserListener listener);
    void getLogRecords(ProjectInteractorImpl.OnGetLogRecordsListener listener);

    void postResponse(List<TaskResponse> response, ProjectInteractorImpl.OnPostProjectResponseListener listener);
    void postFinishedMission(int projectId, ProjectInteractorImpl.OnPostFinishedMission listener);
    void postAgreement(int suggestionId, ProjectInteractorImpl.OnPostAgreementListener listener);
    void postDisagreement(int suggestionId, ProjectInteractorImpl.OnPostDisagreementListener listener);
    void postSuggestion(Suggestion suggestion, ProjectInteractorImpl.OnPostSuggestionListener listener);

    void getMissionForProjectIsCompletedByUser(int projectId, ProjectInteractorImpl.OnGetMissionForProjectIsCompletedByUser listener);

    void postComment(String comment, int suggestionId, ProjectInteractorImpl.OnPostCommentListener listener);

    void uploadImage(byte[] byteArray, ProjectInteractorImpl.OnPostImageListener listener);
    void getImage(String imageUri, ProjectInteractorImpl.OnGetImageListener listener);

    void putAvatar(String avatarUri, ProjectInteractorImpl.OnPutAvatarListener listener);
    void getMyAchievements(ProjectInteractorImpl.OnGetAchievementsListener listener);

    void postMissionLocations(String locations, ProjectInteractorImpl.OnPostMissionLocationsListener listener);

    void getUserAchievements(int userId, ProjectInteractorImpl.OnGetAchievementsListener listener);

}
