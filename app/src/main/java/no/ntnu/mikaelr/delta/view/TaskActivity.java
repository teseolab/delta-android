package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.presenter.TaskPresenter;
import no.ntnu.mikaelr.delta.presenter.TaskPresenterImpl;

public class TaskActivity extends AppCompatActivity implements TaskView {

    private TaskPresenter presenter;

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        this.presenter = new TaskPresenterImpl(this);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Oppgave " + presenter.getTask().getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_task_done) {
            presenter.onTaskDoneClick();
        } else {
            presenter.onTaskCancelClick();
        }
        return true;
    }

}
