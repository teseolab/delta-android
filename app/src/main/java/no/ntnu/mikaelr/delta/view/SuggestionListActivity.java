package no.ntnu.mikaelr.delta.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import no.ntnu.mikaelr.delta.R;

public class SuggestionListActivity extends AppCompatActivity implements SuggestionListView, View.OnClickListener {

    // Lifecycle methods -----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_list);

        LinearLayout view = (LinearLayout) findViewById(R.id.activity_suggestion_list);
        view.setOnClickListener(this);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Forslag");

//        RelativeLayout suggestion = (RelativeLayout) findViewById(R.id.list_item_suggestion);
//        suggestion.setOnClickListener(this);
    }

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SuggestionActivity.class);
        startActivity(intent);
    }
}
