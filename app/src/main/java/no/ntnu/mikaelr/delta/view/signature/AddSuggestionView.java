package no.ntnu.mikaelr.delta.view.signature;

import android.graphics.Bitmap;

public interface AddSuggestionView {

    String getSuggestionTitle();
    String getSuggestionDetails();

    void setSuggestionImage(Bitmap bitmap);
    void setButtonText(String text);

    void showSpinner(boolean showSpinner);
    void showMessage(String message);
}
