package no.ntnu.mikaelr.delta.view.signature;

import no.ntnu.mikaelr.delta.model.HighscoreUser;

import java.util.List;

public interface TopListView {
    void updateList(List<HighscoreUser> topList);
    void showMessage(String message, int length);

    void setEmptyListMessage(String message);
}
