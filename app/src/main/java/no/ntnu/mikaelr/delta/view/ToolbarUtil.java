package no.ntnu.mikaelr.delta.view;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import no.ntnu.mikaelr.delta.R;

public class ToolbarUtil {

    public static void initializeToolbar(AppCompatActivity context, int navigationIcon, String title) {
        Toolbar toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(navigationIcon);
        toolbar.setTitle(title);
        context.setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        context.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void setTitle(AppCompatActivity context, String title) {
        Toolbar toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    public static void showSpinner(MenuItem menuItem, boolean showSpinner) {
        if (showSpinner) {
            menuItem.setActionView(R.layout.menu_progress_spinner);
            menuItem.setEnabled(false);
        } else {
            menuItem.setActionView(null);
            menuItem.setEnabled(true);
        }
    }
}
