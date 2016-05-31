package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.fragment.ImageViewerDialog;
import no.ntnu.mikaelr.delta.model.Task;
import no.ntnu.mikaelr.delta.model.TaskQuestion;
import no.ntnu.mikaelr.delta.model.TaskResponse;
import no.ntnu.mikaelr.delta.presenter.TaskPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.TaskPresenter;
import no.ntnu.mikaelr.delta.view.signature.TaskView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements TaskView, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private TaskPresenter presenter;

    private ArrayList<View> scaleTaskViews = new ArrayList<View>();
    private List<String> scale;
    private RadioGroup radioGroup;
    private List<RadioGroup> radioGroups = new ArrayList<RadioGroup>();
    private List<List<CheckBox>> checkBoxList = new ArrayList<List<CheckBox>>();

    private boolean responseIsBeingPosted = false;

    // LIFECYCLE -------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.presenter = new TaskPresenterImpl(this);
        initializeView();
    }

    // OPTIONS MENU ----------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem doneButton = menu.findItem(R.id.action_task_done);
        ToolbarUtil.showSpinner(doneButton, responseIsBeingPosted);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_task_done) {
            showSpinner(true);
            presenter.onTaskDoneClick();
        } else {
            presenter.onTaskCancelClick();
        }
        return true;
    }

    // VIEW INITIALIZATION ---------------------------------------------------------------------------------------------

    private void initializeView() {

        int taskIndex = presenter.getTaskIndex();
        Task task = presenter.getTask();

        switch (task.getTaskType()) {

            case FIRST_TASK:
                initializeFirstTaskView(task);
                break;
            case TEXT_TASK:
                initializeTextTaskView(taskIndex, task);
                break;
            case ALTERNATIVE_TASK:
                initializeAlternativeTaskView(taskIndex, task);
                break;
            case ALTERNATIVE_TASK_MULTI:
                initializeAlternativeMultiTaskView(taskIndex, task);
                break;
            case SCALE_TASK:
                initializeScaleTaskView(taskIndex, task);
                break;
        }
    }

    private void initializeFirstTaskView(Task task) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View taskView = inflater.inflate(R.layout.task_first, null);

        TextView firstTaskTextView = (TextView) taskView.findViewById(R.id.description);
        firstTaskTextView.setText(task.getDescription());

        setContentView(taskView);
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Klar, ferdig, gå!");
    }

    private void initializeScaleTaskView(int taskIndex, Task task) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View taskView = inflater.inflate(R.layout.task_scale, null);

        LinearLayout parentLayout = (LinearLayout) taskView.findViewById(R.id.parent_layout_task_scale);
        View imageWrapper = parentLayout.findViewById(R.id.imageWrapper);
        ImageView image = (ImageView) parentLayout.findViewById(R.id.image);
        image.setOnClickListener(this);
        String imageUri = task.getImageUri();
        if (imageUri != null) {
            Picasso.with(this).load(imageUri).into(image);
        } else {
            imageWrapper.setVisibility(View.GONE);
        }

        TextView descriptionView = (TextView) parentLayout.findViewById(R.id.description);
        descriptionView.setText(task.getDescription());

        scale = task.getQuestions().get(0).getAlternatives();

        for (TaskQuestion taskQuestion : task.getQuestions()) {

            View scaleTaskView = inflater.inflate(R.layout.task_scale_item, null);
            TextView scaleTaskTextView = (TextView) scaleTaskView.findViewById(R.id.task_scale_text);
            scaleTaskTextView.setText(taskQuestion.getQuestion());

            parentLayout.addView(scaleTaskView);
            scaleTaskView.setId(taskQuestion.getId());
            scaleTaskViews.add(scaleTaskView);

            SeekBar seekBar = (SeekBar) scaleTaskView.findViewById(R.id.seekbar);
            seekBar.setOnSeekBarChangeListener(this);

            TextView textView = (TextView) scaleTaskView.findViewById(R.id.seekbar_text);
            String scaleValue = scale.get(seekBar.getProgress());
            textView.setText(scaleValue);

        }

        setContentView(taskView);
        int taskNumber = taskIndex + 1;
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Oppgave " + taskNumber);
    }

    private void initializeAlternativeTaskView(int taskIndex, Task task) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.task_alternative, null);
        LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout_task_alternative);

        View imageWrapper = parentLayout.findViewById(R.id.imageWrapper);
        ImageView image = (ImageView) parentLayout.findViewById(R.id.image);
        image.setOnClickListener(this);
        String imageUri = task.getImageUri();
        if (imageUri != null) {
            Picasso.with(this).load(imageUri).into(image);
        } else {
            imageWrapper.setVisibility(View.GONE);
        }

        for (TaskQuestion taskQuestion : task.getQuestions()) {
            LinearLayout taskItemView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.task_alternative_item, null);
            TextView descriptionView = (TextView) taskItemView.findViewById(R.id.question);
            descriptionView.setText(taskQuestion.getQuestion());

            radioGroup = (RadioGroup) taskItemView.findViewById(R.id.radio_group);
            radioGroup.setId(taskQuestion.getId());

            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);

            for (int i = 0; i < taskQuestion.getAlternatives().size(); i++) {
                String alternative = taskQuestion.getAlternatives().get(i);
                RadioButton radioButton = new RadioButton(this);
                radioButton.setId(taskQuestion.getId()*10 + i);
                radioButton.setText(alternative);
                radioGroup.addView(radioButton, layoutParams);
            }
            radioGroups.add(radioGroup);

            parentLayout.addView(taskItemView);
        }

        setContentView(view);
        int taskNumber = taskIndex + 1;
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Oppgave " + taskNumber);
    }

    private void initializeAlternativeMultiTaskView(int taskIndex, Task task) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.task_alternative, null);
        LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout_task_alternative);

        View imageWrapper = parentLayout.findViewById(R.id.imageWrapper);
        ImageView image = (ImageView) parentLayout.findViewById(R.id.image);
        image.setOnClickListener(this);
        String imageUri = task.getImageUri();
        if (imageUri != null) {
            Picasso.with(this).load(imageUri).into(image);
        } else {
            imageWrapper.setVisibility(View.GONE);
        }

        TextView description = (TextView) parentLayout.findViewById(R.id.description);
        description.setText(task.getDescription());

        for (TaskQuestion taskQuestion : task.getQuestions()) {
            LinearLayout taskItemView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.task_alternative_multi_item, null);
            TextView descriptionView = (TextView) taskItemView.findViewById(R.id.question);
            descriptionView.setText(taskQuestion.getQuestion());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginStart((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));

            List<CheckBox> checkBoxes = new ArrayList<CheckBox>();

            for (int i = 0; i < taskQuestion.getAlternatives().size(); i++) {
                String alternative = taskQuestion.getAlternatives().get(i);

                CheckBox checkBox = new CheckBox(this);
                checkBox.setId(taskQuestion.getId());
                checkBox.setText(alternative);
                checkBoxes.add(checkBox);
                taskItemView.addView(checkBox, layoutParams);
                taskItemView.setId(taskQuestion.getId());
            }
            checkBoxList.add(checkBoxes);
            parentLayout.addView(taskItemView);
        }

        setContentView(view);
        int taskNumber = taskIndex + 1;
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Oppgave " + taskNumber);
    }

    private void initializeTextTaskView(int taskIndex, Task task) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View taskView = inflater.inflate(R.layout.task_text, null);

        View imageWrapper = taskView.findViewById(R.id.imageWrapper);
        ImageView image = (ImageView) taskView.findViewById(R.id.image);
        image.setOnClickListener(this);
        String imageUri = task.getImageUri();
        if (imageUri != null) {
            Picasso.with(this).load(imageUri).into(image);
        } else {
            imageWrapper.setVisibility(View.GONE);
        }

        TextView descriptionTextView = (TextView) taskView.findViewById(R.id.description);
        descriptionTextView.setText(task.getDescription());

        TextView questionTextView = (TextView) taskView.findViewById(R.id.description);
        // TODO: 30.05.2016 Only supports one question
        questionTextView.setText(task.getQuestions().get(0).getQuestion());

        setContentView(taskView);
        int taskNumber = taskIndex + 1;
        ToolbarUtil.initializeToolbar(this, R.drawable.ic_close_white_24dp, "Oppgave " + taskNumber);
    }

    // VIEW INTERFACE --------------------------------------------------------------------------------------------------

    @Override
    public void showSpinner(boolean showSpinner) {
        responseIsBeingPosted = showSpinner;
        supportInvalidateOptionsMenu();
    }

    @Override
    public List<TaskResponse> getTextTaskResponse() {
        EditText editText = (EditText) findViewById(R.id.text_task_response);
        String response = editText.getText().toString();
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setQuestionId(presenter.getTask().getQuestions().get(0).getId());
        taskResponse.setResponse(Collections.singletonList(response));
        return Collections.singletonList(taskResponse);
    }

    @Override
    public List<TaskResponse> getScaleTaskResponses() {
        List<TaskResponse> taskResponses = new ArrayList<TaskResponse>();
        for (View scaleTaskView : scaleTaskViews) {
            TextView seekBarTextView = (TextView) scaleTaskView.findViewById(R.id.seekbar_text);
            String response = seekBarTextView.getText().toString();
            TaskResponse taskResponse = new TaskResponse();
            taskResponse.setQuestionId(scaleTaskView.getId());
            taskResponse.setResponse(Collections.singletonList(response));
            taskResponses.add(taskResponse);
        }
        return taskResponses;
    }

    @Override
    public List<TaskResponse> getAlternativeTaskResponses() {
        List<TaskResponse> taskResponses = new ArrayList<TaskResponse>();

        for (RadioGroup radioGroup : radioGroups) {
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton checkedRadioButton = (RadioButton) findViewById(checkedRadioButtonId);
            if (checkedRadioButton != null) {
                String response = checkedRadioButton.getText().toString();
                TaskResponse taskResponse = new TaskResponse();
                taskResponse.setQuestionId(radioGroup.getId());
                taskResponse.setResponse(Collections.singletonList(response));
                taskResponses.add(taskResponse);
            }
        }
        return taskResponses;
    }

    @Override
    public List<TaskResponse> getAlternativeMultiTaskResponses() {
        List<TaskResponse> taskResponses = new ArrayList<TaskResponse>();
        for (List<CheckBox> checkBoxes : checkBoxList) {
            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.isChecked()) {
                    String response = checkBox.getText().toString();
                    TaskResponse taskResponse = new TaskResponse();
                    taskResponse.setQuestionId(checkBox.getId());
                    taskResponse.setResponse(Collections.singletonList(response));
                    taskResponses.add(taskResponse);
                }
            }
        }
        return taskResponses;
    }

    @Override
    public void showMessage(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

    // SEEK BAR LISTENER -----------------------------------------------------------------------------------------------

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        View parent = (View) seekBar.getParent();
        TextView textView = (TextView) parent.findViewById(R.id.seekbar_text);
        textView.setText(scale.get(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    // ON CLICK LISTENER -----------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image) {
            ImageViewerDialog.newInstance(presenter.getTaskImageUri()).show(getSupportFragmentManager(), null);
        }
    }

    // RADIO BUTTON LISTENER -------------------------------------------------------------------------------------------

//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
//        String checkedRadioButtonText = checkedRadioButton.getText().toString();
//        TextView otherTextView = (TextView) findViewById(R.id.other_text);
//        if (checkedRadioButtonText.toLowerCase().equals("annet")) {
//            otherTextView.setVisibility(View.VISIBLE);
//        } else {
//            otherTextView.setVisibility(View.GONE);
//        }
//    }
}
