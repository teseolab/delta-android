package no.ntnu.mikaelr.delta.view;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.*;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.AchievementListAdapter;
import no.ntnu.mikaelr.delta.model.Achievement;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.ProfilePresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.ProfilePresenter;
import no.ntnu.mikaelr.delta.util.CircleTransform;
import no.ntnu.mikaelr.delta.view.signature.ProfileView;

import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    private ProfilePresenter presenter;
    private ImageView avatar;
    private TextView username;
    private TextView score;
    private TextView missionsCompleted;
    private TextView suggestionsPosted;
    private TextView commentsPosted;
    private AchievementListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.profile_header, null);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_arrow_back_white_24dp, "Profil");
        presenter = new ProfilePresenterImpl(this, this);

        avatar = (ImageView) header.findViewById(R.id.avatar);
        username = (TextView) header.findViewById(R.id.username);
        score = (TextView) header.findViewById(R.id.score);
        missionsCompleted = (TextView) header.findViewById(R.id.missions_completed);
        suggestionsPosted = (TextView) header.findViewById(R.id.suggestions_posted);
        commentsPosted = (TextView) header.findViewById(R.id.comments_posted);

        ListView achievementList = (ListView) findViewById(R.id.achievement_list);
        adapter = new AchievementListAdapter(this);
        achievementList.addHeaderView(header);
        achievementList.setAdapter(adapter);

        int userId = getIntent().getIntExtra("userId", -1);

        if (userId != -1) {
            presenter.loadProfile(userId);
            presenter.loadUserAchievements(userId);
        } else {
            HighscoreUser user = (HighscoreUser) getIntent().getSerializableExtra("user");
            updateProfile(user);
            presenter.loadUserAchievements(user.getId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void updateProfile(HighscoreUser user) {
        if (user.getAvatarUri() != null) {
            Picasso.with(this).load(user.getAvatarUri()).transform(new CircleTransform()).error(R.drawable.no_avatar).into(avatar);
        } else {
            avatar.setImageResource(R.drawable.no_avatar);
        }
        username.setText(user.getUsername());
        score.setText("Poeng: " + user.getScore());
        missionsCompleted.setText(Integer.toString(user.getNumberOfMissions()));
        suggestionsPosted.setText(Integer.toString(user.getNumberOfSuggestions()));
        commentsPosted.setText(Integer.toString(user.getNumberOfComments()));
    }

    @Override
    public void updateAchievements(List<Achievement> achievements) {
        adapter.updateList(achievements);
    }

    @Override
    public void setAvatar(Uri avatarUri) {
        if (avatarUri != null) {
            Picasso.with(this).load(avatarUri).transform(new CircleTransform()).error(R.drawable.no_avatar).into(avatar);
        } else {
            avatar.setImageResource(R.drawable.no_avatar);
        }
    }

    @Override
    public void showMessage(String message, int length) {
        Toast.makeText(this, message, length).show();
    }
}
