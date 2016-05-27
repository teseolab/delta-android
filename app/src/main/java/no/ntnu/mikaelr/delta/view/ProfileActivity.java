package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.util.CircleTransform;
import no.ntnu.mikaelr.delta.util.Constants;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_arrow_back_white_24dp, "Profil");

        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        TextView username = (TextView) findViewById(R.id.username);
        TextView score = (TextView) findViewById(R.id.score);
        TextView missionsCompleted = (TextView) findViewById(R.id.missions_completed);
        TextView suggestionsPosted = (TextView) findViewById(R.id.suggestions_posted);
        TextView commentsPosted = (TextView) findViewById(R.id.comments_posted);

        HighscoreUser user = (HighscoreUser) getIntent().getSerializableExtra("user");

        if (user.getAvatarUri() != null) {
            Picasso.with(this).load(Constants.IMAGES_PATH + user.getAvatarUri()).transform(new CircleTransform()).error(R.drawable.no_avatar).into(avatar);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
