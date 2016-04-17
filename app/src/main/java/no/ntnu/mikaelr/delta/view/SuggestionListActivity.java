package no.ntnu.mikaelr.delta.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.SuggestionListAdapter;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.presenter.signature.SuggestionListPresenter;
import no.ntnu.mikaelr.delta.presenter.SuggestionListPresenterImpl;
import no.ntnu.mikaelr.delta.view.signature.SuggestionListView;

import java.util.List;

public class SuggestionListActivity extends AppCompatActivity implements SuggestionListView, AdapterView.OnItemClickListener {

    private SuggestionListPresenter presenter;
    SuggestionListAdapter listAdapter;


    // LIFECYCLE -------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_list);

        this.presenter = new SuggestionListPresenterImpl(this);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Forslag");

        listAdapter = new SuggestionListAdapter(this);
        ListView listView = (ListView) findViewById(R.id.suggestion_list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        presenter.loadSuggestions();
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
