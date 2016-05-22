package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.CustomDialog;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Achievement;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.signature.ProfilePresenter;
import no.ntnu.mikaelr.delta.util.*;
import no.ntnu.mikaelr.delta.view.ImageCropperActivity;
import no.ntnu.mikaelr.delta.view.signature.ProfileView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.FileNotFoundException;
import java.util.List;

public class ProfilePresenterImpl implements ProfilePresenter, ProjectInteractorImpl.OnGetUserListener, ProjectInteractorImpl.OnPostImageListener, ProjectInteractorImpl.OnPutAvatarListener, ProjectInteractorImpl.OnGetAchievementsListener {

    private ProfileView view;
    private Fragment context;
    private ProjectInteractor interactor;

    private Uri avatarUri;
    private Bitmap avatar;

    private HighscoreUser user;
    private List<Achievement> achievements;
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
    public void loadAchievements() {
        interactor.getMyAchievements(this);
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
        view.updateProfile(user);
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
    public void onPutAvatarSuccess(String result) {
        if (result != null) {
            loadProfile();
            loadAchievements();
            Achievement achievement = JsonFormatter.formatAchievement(result);
            context.getFragmentManager()
                    .beginTransaction()
                    .add(CustomDialog.newInstance(achievement.getName(), achievement.getDescription(), "OK", null,
                            BadgeIdConverter.getInstance().convertBadgeNameToResourceId(achievement.getBadgeName())), null)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onPutAvatarError(int errorCode) {
        view.showMessage(ErrorMessage.COULD_NOT_POST_AVATAR, Toast.LENGTH_LONG);
    }

    @Override
    public void onGetAchievementsSuccess(JSONArray jsonArray) {
        achievements = JsonFormatter.formatAchievements(jsonArray);
        view.updateAchievements(achievements);
    }

    @Override
    public void onGetAchievementsError(int errorCode) {
        view.showMessage(ErrorMessage.COULD_NOT_LOAD_ACHIEVEMENTS, Toast.LENGTH_LONG);
    }
}
