package no.ntnu.mikaelr.delta.presenter.signature;

import android.content.Intent;
import android.net.Uri;

public interface AddSuggestionPresenter {
    void onDoneClick(String title, String details);

    void openCamera(int requestCode);

//    void onCameraResult(int resultCode, Intent data);

    void openGallery(int requestCode);

//    void onGalleryResult(int resultCode, Intent data);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
