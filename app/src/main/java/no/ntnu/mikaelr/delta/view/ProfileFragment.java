package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.ProfilePresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.ProfilePresenter;
import no.ntnu.mikaelr.delta.view.signature.ProfileView;

public class ProfileFragment extends Fragment implements ProfileView {

    private ImageView avatar;
    private TextView username;
    private TextView score;
    private TextView missionsCompleted;
    private TextView suggestionsPosted;
    private TextView commentsPosted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ProfilePresenter presenter = new ProfilePresenterImpl(this);

        avatar = (ImageView) view.findViewById(R.id.avatar);
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
        avatar.setImageResource(R.drawable.mikael_delta_profile_picture); // TODO: Get from user
        username.setText(user.getUsername());
        score.setText("Poeng: " + user.getScore());
        missionsCompleted.setText(Integer.toString(user.getNumberOfMissions()));
        suggestionsPosted.setText(Integer.toString(user.getNumberOfSuggestions()));
        commentsPosted.setText(Integer.toString(user.getNumberOfComments()));
    }
}
