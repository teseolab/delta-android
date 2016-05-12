package no.ntnu.mikaelr.delta.view.signature;

import android.net.Uri;
import no.ntnu.mikaelr.delta.model.HighscoreUser;

public interface ProfileView {
    void updateView(HighscoreUser user);

    void setAvatar(Uri avatarUri);

    void showMessage(String message, int length);
}
