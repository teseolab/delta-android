package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.signature.ProfilePresenter;
import no.ntnu.mikaelr.delta.util.ImageHandler;
import no.ntnu.mikaelr.delta.view.ImageCropperActivity;
import no.ntnu.mikaelr.delta.view.signature.ProfileView;
import org.json.JSONObject;

public class ProfilePresenterImpl implements ProfilePresenter, ProjectInteractorImpl.OnGetUserListener, ProjectInteractorImpl.OnPostImageListener, ProjectInteractorImpl.OnPutAvatarListener {

    private ProfileView view;
    private Fragment context;
    private ProjectInteractor interactor;

    private Uri avatarUri;
    private Bitmap avatar;

    private HighscoreUser user;

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
    public void openCamera(int requestCode) {

        Intent intent = new Intent(context.getActivity(), ImageCropperActivity.class);
        intent.putExtra("requestCode", requestCode);
        context.startActivityForResult(intent, requestCode);

    }

    @Override
    public void openGallery(int requestCode) {

        Intent intent = new Intent(context.getActivity(), ImageCropperActivity.class);
        intent.putExtra("requestCode", requestCode);
        context.startActivityForResult(intent, requestCode);

    }

    @Override
    public void onCropResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri avatarUri = data.getParcelableExtra("avatarUri");
            if (avatarUri != null) {
                avatar = ImageHandler.decodeImageFromFilePath(avatarUri, 350);
                interactor.uploadImage(ImageHandler.byteArrayFromBitmap(avatar), this);
                view.setAvatar(avatarUri);
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
        // TODO: Handle
    }

    @Override
    public void onPostImageSuccess(String uri) {
        interactor.putAvatar(uri, this);
    }

    @Override
    public void onPostImageError(int errorCode) {
        // TODO: Handle
    }

    @Override
    public void onPutAvatarSuccess() {

    }

    @Override
    public void onPutAvatarError(int errorCode) {
        // TODO: Handle
    }
}
