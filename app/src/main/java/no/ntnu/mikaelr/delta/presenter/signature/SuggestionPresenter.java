package no.ntnu.mikaelr.delta.presenter.signature;

import no.ntnu.mikaelr.delta.model.Suggestion;

public interface SuggestionPresenter {

    void loadComments(int suggestionId);
    Suggestion getSuggestion();
    boolean userAgrees();
    boolean userDisagrees();

    void postAgreement();
    void postDisagreement();

    void onFinished();
}
