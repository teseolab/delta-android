package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.CommentListAdapter;
import no.ntnu.mikaelr.delta.model.Comment;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.presenter.SuggestionPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.SuggestionPresenter;
import no.ntnu.mikaelr.delta.util.DateFormatter;
import no.ntnu.mikaelr.delta.view.signature.SuggestionView;

import java.util.List;

public class SuggestionActivity extends AppCompatActivity implements SuggestionView, View.OnClickListener {

    private SuggestionPresenter presenter;

    private CommentListAdapter listAdapter;
    private ListView listView;

    TextView agreements;
    TextView disagreements;

    // LIFECYCLE -------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        this.presenter = new SuggestionPresenterImpl(this);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Forslag");

        initializeList();
        initializeHeader();

        presenter.loadComments(presenter.getSuggestion().getId());
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private void initializeHeader() {

        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.suggestion_header, null);

        ImageView image = (ImageView) header.findViewById(R.id.image);
        TextView username = (TextView) header.findViewById(R.id.username);
        TextView date = (TextView) header.findViewById(R.id.date);
        TextView title = (TextView) header.findViewById(R.id.title);
        TextView details = (TextView) header.findViewById(R.id.details);
        agreements = (TextView) header.findViewById(R.id.agreements);
        disagreements = (TextView) header.findViewById(R.id.disagreements);

        Suggestion suggestion = presenter.getSuggestion();

        image.setImageResource(R.drawable.nyhavna2_hires);
        username.setText(suggestion.getUser().getUsername());
        date.setText(DateFormatter.format(suggestion.getDate(), "dd.MM.yy"));
        title.setText(suggestion.getTitle());
        details.setText(suggestion.getDetails());
        agreements.setText(suggestion.getAgreements() + " bukere er enige");
        disagreements.setText(suggestion.getDisagreements() + " brukere er uenige");

        if (presenter.userAgrees()) {
            agreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_achievement, 0, 0);
        } else if (presenter.userDisagrees()) {
            disagreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_achievement, 0, 0);
        }

        agreements.setOnClickListener(this);
        disagreements.setOnClickListener(this);

        listView.addHeaderView(header);
    }

    private void initializeList() {
        listAdapter = new CommentListAdapter(this);
        listView = (ListView) findViewById(R.id.comment_list);
        listView.setAdapter(listAdapter);
    }

    // ACTIVITY --------------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        presenter.onFinished();
        return true;
    }

    // VIEW INTERFACE --------------------------------------------------------------------------------------------------

    @Override
    public void updateComments(List<Comment> comments) {
        listAdapter.updateList(comments);
    }

    @Override
    public void enableAgreementButtonsClickListener(boolean enable) {
        if (enable) {
            agreements.setOnClickListener(this);
            disagreements.setOnClickListener(this);
        } else {
            agreements.setOnClickListener(null);
            disagreements.setOnClickListener(null);
        }
    }

    @Override
    public void updateAgreements() {
        agreements.setText(presenter.getSuggestion().getAgreements() + " brukere er enige");
        disagreements.setText(presenter.getSuggestion().getDisagreements() + " brukere er uenige");
    }

    @Override
    public void setSelectedButtonThumbsUp() {
        disagreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_thumbs_down, 0, 0);
        agreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_achievement, 0, 0);
    }

    @Override
    public void setSelectedButtonThumbsDown() {
        disagreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_achievement, 0, 0);
        agreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_thumbs_up, 0, 0);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.agreements:
                if (!presenter.userAgrees()) {
                    enableAgreementButtonsClickListener(false);
                    presenter.postAgreement();
                }
                break;
            case R.id.disagreements:
                if (!presenter.userDisagrees()) {
                    enableAgreementButtonsClickListener(false);
                    presenter.postDisagreement();
                }
                break;

        }

    }
}
