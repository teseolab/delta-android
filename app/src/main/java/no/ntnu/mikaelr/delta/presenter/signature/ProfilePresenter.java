package no.ntnu.mikaelr.delta.presenter.signature;

import android.content.Intent;

public interface ProfilePresenter {
    void loadProfile();

    void openCamera(int requestCode);
    void openGallery(int requestCode);

    void onCropResult(int resultCode, Intent data);
}
