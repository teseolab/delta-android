package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import no.ntnu.mikaelr.delta.R;

public class SuggestionActivity extends AppCompatActivity {

    // Lifecycle methods -----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Forslag");
    }

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
