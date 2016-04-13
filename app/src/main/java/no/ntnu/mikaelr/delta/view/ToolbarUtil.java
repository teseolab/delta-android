package no.ntnu.mikaelr.delta.view;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
}
