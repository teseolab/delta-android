package no.ntnu.mikaelr.delta.interactor;

import no.ntnu.mikaelr.delta.model.ProjectResponse;
import no.ntnu.mikaelr.delta.model.Suggestion;

public interface ProjectInteractor {

    void getProjects(ProjectInteractorImpl.OnFinishedLoadingProjectsListener listener);
    void getTasks(int projectId, ProjectInteractorImpl.OnFinishedLoadingTasksListener listener);
    void getSuggestions(int projectId, ProjectInteractorImpl.OnFinishedLoadingSuggestionsListener listener);
    void getComments(int suggestionId, ProjectInteractorImpl.OnFinishedLoadingCommentsListener listener);

    void postResponse(ProjectResponse response, ProjectInteractorImpl.OnPostProjectResponseListener listener);
    void postAgreement(int suggestionId, ProjectInteractorImpl.OnPostAgreementListener listener);
    void postDisagreement(int suggestionId, ProjectInteractorImpl.OnPostDisagreementListener listener);
    void postSuggestion(Suggestion suggestion, ProjectInteractorImpl.OnPostSuggestionListener listener);

    void getMissionForProjectIsCompletedByUser(int projectId, int userId, ProjectInteractorImpl.OnGetMissionForProjectIsCompletedByUser listener);

    void postComment(String comment, int suggestionId, ProjectInteractorImpl.OnPostCommentListener listener);
}
