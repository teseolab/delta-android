package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.presenter.signature.TaskPresenter;
import no.ntnu.mikaelr.delta.presenter.TaskPresenterImpl;
import no.ntnu.mikaelr.delta.view.signature.TaskView;

public class TaskActivity extends AppCompatActivity implements TaskView {

    private TaskPresenter presenter;

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_task_scale);
        this.presenter = new TaskPresenterImpl(this);
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
