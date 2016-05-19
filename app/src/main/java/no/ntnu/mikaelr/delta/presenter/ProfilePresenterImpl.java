package no.ntnu.mikaelr.delta.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.signature.ProfilePresenter;
import no.ntnu.mikaelr.delta.util.ErrorMessage;
import no.ntnu.mikaelr.delta.util.ImageHandler;
import no.ntnu.mikaelr.delta.util.SessionInvalidator;
import no.ntnu.mikaelr.delta.view.ImageCropperActivity;
import no.ntnu.mikaelr.delta.view.signature.ProfileView;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.FileNotFoundException;

public class ProfilePresenterImpl implements ProfilePresenter, ProjectInteractorImpl.OnGetUserListener, ProjectInteractorImpl.OnPostImageListener, ProjectInteractorImpl.OnPutAvatarListener {

    private ProfileView view;
    private Fragment context;
    private ProjectInteractor interactor;

    private Uri avatarUri;
    private Bitmap avatar;

    private HighscoreUser user;
    private int REQUEST_OPEN_CAMERA = 0;
    private int REQUEST_OPEN_GALLERY = 1;

    public ProfilePresenterImpl(ProfileView view) {
        this.view = view;
        this.context = (Fragment) view;
        this.interactor = new ProjectInteractorImpl();
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void loadProfile() {
        interactor.getMe(this);
    }

    @Override
    public void openImageCropper(int requestCode) {
        Intent intent = new Intent(context.getActivity(), ImageCropperActivity.class);
        intent.putExtra("requestCode", requestCode);
        context.startActivityForResult(intent, requestCode);
    }


    @Override
    public void onCropResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri avatarUri = data.getParcelableExtra("avatarUri");
            if (avatarUri != null) {
                try {
                    avatar = ImageHandler.decodeImageFromFilePath(avatarUri, 350);
                    interactor.uploadImage(ImageHandler.byteArrayFromBitmap(avatar), this);
                    view.setAvatar(avatarUri);
                } catch (FileNotFoundException e) {
                    Log.w("ProfilePresenterImpl", "Image file not found.");
                    view.showMessage(ErrorMessage.IMAGE_FILE_NOT_FOUND, Toast.LENGTH_LONG);
                }
            }
        }
    }

    // ASYNC TASK LISTENER ---------------------------------------------------------------------------------------------

    @Override
    public void onGetUserSuccess(JSONObject jsonObject) {
        user = HighscoreUser.fromJsonObject(jsonObject);
        view.updateView(user);
    }

    @Override
    public void onGetUserError(int errorCode) {
        if (errorCode == HttpStatus.UNAUTHORIZED.value()) {
            SessionInvalidator.invalidateSession(context.getActivity());
        } else {
            view.showMessage(ErrorMessage.COULD_NOT_LOAD_PROFILE, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onPostImageSuccess(String uri) {
        interactor.putAvatar(uri, this);
    }

    @Override
    public void onPostImageError(int errorCode) {
        view.showMessage(ErrorMessage.COULD_NOT_POST_AVATAR, Toast.LENGTH_LONG);
    }

    @Override
    public void onPutAvatarSuccess() {

    }

    @Override
    public void onPutAvatarError(int errorCode) {
        view.showMessage(ErrorMessage.COULD_NOT_POST_AVATAR, Toast.LENGTH_LONG);
    }
}
