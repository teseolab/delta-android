package no.ntnu.mikaelr.delta.presenter.signature;

import android.content.Intent;
import android.support.annotation.Nullable;

public interface ProfilePresenter {
    void loadProfile(@Nullable Integer userId);
    void loadMyAchievements();
    void loadUserAchievements(int userId);
    void openImageCropper(int requestCode);
    void onCropResult(int resultCode, Intent data);
    boolean userIsLoaded();
}
