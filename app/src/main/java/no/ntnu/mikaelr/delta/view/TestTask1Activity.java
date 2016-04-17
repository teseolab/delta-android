package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.presenter.signature.TaskPresenter;
import no.ntnu.mikaelr.delta.presenter.TaskPresenterImpl;
import no.ntnu.mikaelr.delta.view.signature.TaskView;

public class TestTask1Activity extends AppCompatActivity implements TaskView, SeekBar.OnSeekBarChangeListener {

    private TaskPresenter presenter;

    TextView text1;
    TextView text2;
    TextView text3;

    // Activity methods ------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_task_1);
        presenter = new TaskPresenterImpl(this);
        Integer id = presenter.getTask().getId()+1;
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Oppgave " + id);

        SeekBar seekBar1 = (SeekBar) findViewById(R.id.seekbar_1);
        SeekBar seekBar2 = (SeekBar) findViewById(R.id.seekbar_2);
        SeekBar seekBar3 = (SeekBar) findViewById(R.id.seekbar_3);

        seekBar1.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);
        seekBar3.setOnSeekBarChangeListener(this);

        text1 = (TextView) findViewById(R.id.seekbar_1_text);
        text2 = (TextView) findViewById(R.id.seekbar_2_text);
        text3 = (TextView) findViewById(R.id.seekbar_3_text);

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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        String text;

        if (progress == 0) text = "Helt uenig";
        else if (progress == 1) text = "Uenig";
        else if (progress == 2) text = "Vet ikke";
        else if (progress == 3) text = "Enig";
        else if (progress == 4) text = "Helt enig";
        else text = "";

        if (seekBar.getId() == R.id.seekbar_1) {
            text1.setText(text);
        }
        else if (seekBar.getId() == R.id.seekbar_2) {
            text2.setText(text);
        }
        else if (seekBar.getId() == R.id.seekbar_3) {
            text3.setText(text);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
