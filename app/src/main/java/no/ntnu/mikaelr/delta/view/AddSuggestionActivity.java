package no.ntnu.mikaelr.delta.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.MenuItem;
import no.ntnu.mikaelr.delta.R;

public class AddSuggestionActivity extends AppCompatActivity {

    // Lifecycle methods -----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_suggestion);

        AppCompatButton v = (AppCompatButton) findViewById(R.id.add_image_button);
        ColorStateList csl = ColorStateList.valueOf(0xff26A69A);
        v.setSupportBackgroundTintList(csl);

        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Nytt forslag");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_suggestion, menu);
        return true;
    }

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
