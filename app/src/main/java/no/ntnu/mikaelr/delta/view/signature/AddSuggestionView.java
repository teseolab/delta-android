package no.ntnu.mikaelr.delta.view.signature;

import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;

public interface AddSuggestionView {

    String getSuggestionTitle();
    String getSuggestionDetails();

    void setSuggestionImage(Bitmap bitmap);
    void setButtonText(String text);

    void showSpinner(boolean showSpinner);
    void showMessage(String message, int length);
    void showDialog(DialogFragment dialog, String tag);
}
