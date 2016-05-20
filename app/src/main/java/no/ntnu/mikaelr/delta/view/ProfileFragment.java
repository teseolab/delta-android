package no.ntnu.mikaelr.delta.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.AddImageDialog;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.ProfilePresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.ProfilePresenter;
import no.ntnu.mikaelr.delta.util.CircleTransform;
import no.ntnu.mikaelr.delta.util.Constants;
import no.ntnu.mikaelr.delta.view.signature.ProfileView;

public class ProfileFragment extends Fragment implements ProfileView, View.OnClickListener {

    private ProfilePresenter presenter;

    private ImageView avatar;
    private TextView username;
    private TextView score;
    private TextView missionsCompleted;
    private TextView suggestionsPosted;
    private TextView commentsPosted;

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        presenter = new ProfilePresenterImpl(this);

        avatar = (ImageView) view.findViewById(R.id.avatar);
        avatar.setOnClickListener(this);
        username = (TextView) view.findViewById(R.id.username);
        score = (TextView) view.findViewById(R.id.score);
        missionsCompleted = (TextView) view.findViewById(R.id.missions_completed);
        suggestionsPosted = (TextView) view.findViewById(R.id.suggestions_posted);
        commentsPosted = (TextView) view.findViewById(R.id.comments_posted);

        presenter.loadProfile();

        return view;
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void updateView(HighscoreUser user) {
        if (user.getAvatarUri() != null) {
            Picasso.with(getActivity()).load(user.getAvatarUri()).error(R.drawable.default_avatar).transform(new CircleTransform()).into(avatar);
        } else {
            avatar.setImageResource(R.drawable.default_avatar);
        }
        username.setText(user.getUsername());
        score.setText("Poeng: " + user.getScore());
        missionsCompleted.setText(Integer.toString(user.getNumberOfMissions()));
        suggestionsPosted.setText(Integer.toString(user.getNumberOfSuggestions()));
        commentsPosted.setText(Integer.toString(user.getNumberOfComments()));
    }

    @Override
    public void setAvatar(Uri avatarUri) {
        Picasso.with(getActivity()).load(avatarUri).transform(new CircleTransform()).into(avatar);
    }

    @Override
    public void showMessage(String message, int length) {
        Toast.makeText(getActivity(), message, length).show();
    }

    // ON CLICK LISTENER

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.avatar) {
            AddImageDialog.newInstance().show(getFragmentManager(), "tag");
        }
    }

    // Called from MainActivity
    public void openCamera() {
        presenter.openImageCropper(Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    // Called from MainActivity
    public void openGallery() {
        presenter.openImageCropper(Constants.CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    // INTENT RESULT ---------------------------------------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onCropResult(resultCode, data);
    }
}
