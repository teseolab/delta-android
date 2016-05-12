package no.ntnu.mikaelr.delta.view.signature;

import no.ntnu.mikaelr.delta.model.Comment;

import java.util.List;

public interface SuggestionView {

    void updateComments(List<Comment> comments);

    void enableAgreementButtonsClickListener(boolean enable);

    void updateAgreements();
    void setSelectedButtonThumbsUp();
    void setSelectedButtonThumbsDown();

    void setEmptyListMessage(String message);
    void hideEmptyListMessage();

    void showMessage(String message, int length);
}
