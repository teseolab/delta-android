package no.ntnu.mikaelr.delta.presenter.signature;

import android.content.Intent;

public interface ProfilePresenter {
    void loadProfile();

    void openImageCropper(int requestCode);

    void onCropResult(int resultCode, Intent data);
}
