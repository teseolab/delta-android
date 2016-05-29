package no.ntnu.mikaelr.delta.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;
import no.ntnu.mikaelr.delta.fragment.CustomDialog;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractor;
import no.ntnu.mikaelr.delta.interactor.ProjectInteractorImpl;
import no.ntnu.mikaelr.delta.model.Achievement;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.signature.ProfilePresenter;
import no.ntnu.mikaelr.delta.util.*;
import no.ntnu.mikaelr.delta.view.ImageCropperActivity;
import no.ntnu.mikaelr.delta.view.ProfileActivity;
import no.ntnu.mikaelr.delta.view.ProfileFragment;
import no.ntnu.mikaelr.delta.view.signature.ProfileView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.FileNotFoundException;
import java.util.List;

public class ProfilePresenterImpl implements ProfilePresenter, ProjectInteractorImpl.OnGetUserListener,
        ProjectInteractorImpl.OnPostImageListener, ProjectInteractorImpl.OnPutAvatarListener,
        ProjectInteractorImpl.OnGetAchievementsListener {

    private Fragment fragment = null;
    private ProfileView view;
    private Activity activity;
    private ProjectInteractor interactor;
    private HighscoreUser user;

    public ProfilePresenterImpl(ProfileView view, Object context) {
        this.view = view;
        this.interactor = new ProjectInteractorImpl();

        if (context instanceof ProfileActivity) {
            activity = (Activity) context;
        } else if (context instanceof ProfileFragment) {
            activity = ((Fragment) context).getActivity();
            fragment = (Fragment) context;
        }
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void loadProfile(Integer userId) {
        if (userId == null) {
            interactor.getMe(this);
        } else {
            interactor.getUser(userId, this);
        }
    }

    @Override
    public void loadMyAchievements() {
        interactor.getMyAchievements(this);
    }

    @Override
    public void loadUserAchievements(int userId) {
        interactor.getUserAchievements(userId, this);
    }

    @Override
    public void openImageCropper(int requestCode) {
        Intent intent = new Intent(activity, ImageCropperActivity.class);
        intent.putExtra("requestCode", requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCropResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri avatarUri = data.getParcelableExtra("avatarUri");
            if (avatarUri != null) {
                try {
                    Bitmap avatar = ImageHandler.decodeImageFromFilePath(avatarUri, 350);
                    interactor.uploadImage(ImageHandler.byteArrayFromBitmap(avatar), this);
                    view.setAvatar(avatarUri);
                } catch (FileNotFoundException e) {
                    Log.w("ProfilePresenterImpl", "Image file not found.");
                    view.showMessage(ErrorMessage.IMAGE_FILE_NOT_FOUND, Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public boolean userIsLoaded() {
        return user != null;
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
            SessionInvalidator.invalidateSession(activity);
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
            loadProfile(null);
            loadMyAchievements();
            Achievement achievement = JsonFormatter.formatAchievement(result);
            if (fragment != null) {
                fragment.getFragmentManager()
                        .beginTransaction()
                        .add(CustomDialog.newInstance(achievement.getName(), achievement.getDescription(), "OK", null,
                                BadgeIdConverter.getInstance().convertBadgeNameToResourceId(achievement.getBadgeName())), null)
                        .commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onPutAvatarError(int errorCode) {
        view.showMessage(ErrorMessage.COULD_NOT_POST_AVATAR, Toast.LENGTH_LONG);
    }

    @Override
    public void onGetAchievementsSuccess(JSONArray jsonArray) {
        List<Achievement> achievements = JsonFormatter.formatAchievements(jsonArray);
        view.updateAchievements(achievements);
    }

    @Override
    public void onGetAchievementsError(int errorCode) {
        view.showMessage(ErrorMessage.COULD_NOT_LOAD_ACHIEVEMENTS, Toast.LENGTH_LONG);
    }
}
