package no.ntnu.mikaelr.delta.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.CommentListAdapter;
import no.ntnu.mikaelr.delta.fragment.ImageViewerDialog;
import no.ntnu.mikaelr.delta.model.Comment;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.presenter.SuggestionPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.SuggestionPresenter;
import no.ntnu.mikaelr.delta.util.BlurBuilder;
import no.ntnu.mikaelr.delta.util.DateFormatter;
import no.ntnu.mikaelr.delta.view.signature.SuggestionView;

import java.util.List;

public class SuggestionActivity extends AppCompatActivity implements SuggestionView, View.OnClickListener {

    private SuggestionPresenter presenter;

    private View contentView;

    private CommentListAdapter listAdapter;
    private ListView listView;
    private View emptyView;

    private ImageView image;
    private TextView agreements;
    private TextView disagreements;

    // LIFECYCLE -------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = getLayoutInflater().inflate(R.layout.activity_suggestion, null);
        setContentView(contentView);
        this.presenter = new SuggestionPresenterImpl(this);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Forslag");

        initializeListAdapter();
        addHeaderView();

//        if (presenter.hasImage()) {
//            presenter.loadImage();
//        }
        presenter.loadComments();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        listView = (ListView) findViewById(R.id.comment_list);
//        TextView emptyView = (TextView) findViewById(R.id.empty_view);
//        emptyView.setText("Det er ingen kommentarer enda.");
//        listView.setEmptyView(emptyView);
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private void addHeaderView() {

        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.suggestion_header, null);

        image = (ImageView) header.findViewById(R.id.image);
        image.setOnClickListener(this);
        TextView username = (TextView) header.findViewById(R.id.username);
        TextView date = (TextView) header.findViewById(R.id.date);
        TextView title = (TextView) header.findViewById(R.id.title);
        TextView details = (TextView) header.findViewById(R.id.details);
        agreements = (TextView) header.findViewById(R.id.agreements);
        disagreements = (TextView) header.findViewById(R.id.disagreements);
        AppCompatButton commentButton = (AppCompatButton) header.findViewById(R.id.add_comment_button);
        ColorStateList csl = ColorStateList.valueOf(0xff80CBC4); // TODO: Replace with color resource
        commentButton.setSupportBackgroundTintList(csl);
        commentButton.setOnClickListener(this);

        Suggestion suggestion = presenter.getSuggestion();
        String imageUri = suggestion.getImageUri();

        if (!imageUri.equals("")) {
            Picasso.with(this).load(imageUri).into(image);
        }
        username.setText(suggestion.getUser().getUsername());
        date.setText(DateFormatter.format(suggestion.getDate(), "dd.MM.yy"));
        title.setText(suggestion.getTitle());
        details.setText(suggestion.getDetails());
        updateAgreements();

        if (presenter.userAgrees()) {
            agreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_thumbs_up_selected, 0, 0);
        } else if (presenter.userDisagrees()) {
            disagreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_thumbs_down_selected, 0, 0);
        }

        agreements.setOnClickListener(this);
        disagreements.setOnClickListener(this);

        listView.addHeaderView(header);
    }

    private void addFooterView(String message) {

        emptyView = getLayoutInflater().inflate(R.layout.list_item_empty, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.message);
        textView.setText(message);
        textView.setPadding(20, 40, 20, 40);
        listView.addFooterView(emptyView);

    }

    private void initializeListAdapter() {
        listAdapter = new CommentListAdapter(this);
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
        String agreeText, disagreeText;

        Integer numberOfAgreements = presenter.getSuggestion().getAgreements();
        Integer numberOfDisagreements = presenter.getSuggestion().getDisagreements();

        if (numberOfAgreements == 0) agreeText = "Ingen er enige";
        else if (numberOfAgreements == 1) agreeText = "1 bruker er enig";
        else agreeText = numberOfAgreements + " brukere er enige";

        if (numberOfDisagreements == 0) disagreeText = "Ingen er uenige";
        else if (numberOfDisagreements == 1) disagreeText = "1 bruker er uenig";
        else disagreeText = numberOfDisagreements + " brukere er uenige";

        this.agreements.setText(agreeText);
        this.disagreements.setText(disagreeText);
    }

    @Override
    public void setSelectedButtonThumbsUp() {
        disagreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_thumbs_down, 0, 0);
        agreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_thumbs_up_selected, 0, 0);
    }

    @Override
    public void setSelectedButtonThumbsDown() {
        disagreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_thumbs_down_selected, 0, 0);
        agreements.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_thumbs_up, 0, 0);
    }

    @Override
    public void setEmptyListMessage(String message) {
        addFooterView(message);
    }

    @Override
    public void hideEmptyListMessage() {
        listView.removeFooterView(emptyView);
    }

    // LISTENERS -------------------------------------------------------------------------------------------------------

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
            case R.id.add_comment_button:
                presenter.goToPostComment();
                break;
            case R.id.image:
                ImageViewerDialog.newInstance(presenter.getSuggestion().getImageUri()).show(getSupportFragmentManager(), "tag");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.onActivityResult(requestCode, data);
        }
    }

}
