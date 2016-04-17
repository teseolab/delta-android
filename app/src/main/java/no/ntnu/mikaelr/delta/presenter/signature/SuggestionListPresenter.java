package no.ntnu.mikaelr.delta.presenter.signature;

import android.content.Intent;

public interface SuggestionListPresenter {

    void loadSuggestions();

    void onItemClick(int position);

    void onActivityResult(int requestCode, Intent data);
}
