package no.ntnu.mikaelr.delta.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.SuggestionListAdapter;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.presenter.signature.SuggestionListPresenter;
import no.ntnu.mikaelr.delta.presenter.SuggestionListPresenterImpl;
import no.ntnu.mikaelr.delta.view.signature.SuggestionListView;

import java.util.List;

public class SuggestionListActivity extends AppCompatActivity implements SuggestionListView, AdapterView.OnItemClickListener {

    private SuggestionListPresenter presenter;
    ListView listView;
    SuggestionListAdapter listAdapter;


    // LIFECYCLE -------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_list);

        this.presenter = new SuggestionListPresenterImpl(this);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Forslag");

        LinearLayout progressSpinnerView = (LinearLayout) findViewById(R.id.progress_spinner_view);
        CircularProgressView progressSpinner = (CircularProgressView) progressSpinnerView.findViewById(R.id.progress_spinner);
        progressSpinner.startAnimation();

        listAdapter = new SuggestionListAdapter(this);
        listView = (ListView) findViewById(R.id.suggestion_list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        presenter.loadSuggestions();
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private void addFooterView(String message) {

        View emptyView = getLayoutInflater().inflate(R.layout.list_item_empty, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.message);
        textView.setText(message);
        textView.setTextSize(16);
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        textView.setPadding(0, dp, 0, 0);
        listView.addFooterView(emptyView);

    }

    // ACTIVITY --------------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    // INTERFACE -------------------------------------------------------------------------------------------------------

    @Override
    public void updateList(List<Suggestion> suggestions) {
        listAdapter.updateList(suggestions);
    }

    @Override
    public void setEmptyListMessage(String message) {
        addFooterView(message);
    }

    @Override
    public void hideProgressSpinner() {
        LinearLayout progressSpinnerView = (LinearLayout) findViewById(R.id.progress_spinner_view);
        CircularProgressView progressSpinner = (CircularProgressView) progressSpinnerView.findViewById(R.id.progress_spinner);
        progressSpinnerView.setVisibility(View.GONE);
        progressSpinner.setVisibility(View.GONE); // This will also stop the animation
    }

    // LISTENERS -------------------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        presenter.onItemClick(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.onActivityResult(requestCode, data);
        }
    }
}
