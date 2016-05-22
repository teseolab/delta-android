package no.ntnu.mikaelr.delta.view.signature;

import android.net.Uri;
import no.ntnu.mikaelr.delta.model.Achievement;
import no.ntnu.mikaelr.delta.model.HighscoreUser;

import java.util.List;

public interface ProfileView {
    void updateProfile(HighscoreUser user);
    void updateAchievements(List<Achievement> achievements);
    void setAvatar(Uri avatarUri);
    void showMessage(String message, int length);
}
