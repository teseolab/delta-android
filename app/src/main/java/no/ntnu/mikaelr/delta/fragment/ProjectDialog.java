package no.ntnu.mikaelr.delta.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import no.ntnu.mikaelr.delta.listener.ProjectDialogClickListener;
import no.ntnu.mikaelr.delta.model.Project;

public class ProjectDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private int projectId;

    public ProjectDialog() {}

    public static ProjectDialog newInstance(Project project) {
        Bundle args = new Bundle();
        args.putInt("id", project.getId());
        args.putString("name", project.getName());
        args.putString("description", project.getDescription());
        ProjectDialog fragment = new ProjectDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        projectId = getArguments().getInt("id");

        // Get the layout inflater
        //LayoutInflater inflater = getActivity().getLayoutInflater();
        //View view = inflater.inflate(R.layout.fragment_project_dialog, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        String title = getArguments().getString("name", "Unknown project");
        String description = getArguments().getString("description");

        if (description != null) {
            if (description.length() > 150) {
                description = description.substring(0, 150) + "...";
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(description);
        builder.setPositiveButton("Les mer", this);
        builder.setNegativeButton("Tilbake", this);

        return builder.create();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_project_dialog, container);
//        String title = getArguments().getString("name", "Unknown project");
//        getDialog().setTitle(title);
//        //ImageView projectImage = (ImageView) view.findViewById(R.id.project_dialog_image);
//        //projectImage.setImageResource(getArguments().getInt("icon"));
//        TextView projectDescription = (TextView) view.findViewById(R.id.project_dialog_description);
//        projectDescription.setText(getArguments().getString("description"));
////        Button dismissButton = (Button) view.findViewById(R.id.project_dialog_dismiss_button);
////        dismissButton.setText("Avbryt");
////        dismissButton.setOnClickListener(this);
////        Button moreButton = (Button) view.findViewById(R.id.project_dialog_more_button);
////        moreButton.setText("Les mer");
////        moreButton.setOnClickListener(this);
//        return view;
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            listener = (ProjectDialogClickListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement ProjectDialogClickListener");
//        }
//    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Intent intent = getActivity().getIntent();
            intent.putExtra("projectId", projectId);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }

        else if (which == DialogInterface.BUTTON_NEGATIVE) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
        }
    }
}
