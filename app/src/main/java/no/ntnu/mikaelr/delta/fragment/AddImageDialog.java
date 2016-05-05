package no.ntnu.mikaelr.delta.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import no.ntnu.mikaelr.delta.R;

public class AddImageDialog extends DialogFragment {
    public AddImageDialog() {}

    public interface AddImageDialogListener {
        void onTakePhotoClicked(Dialog dialog);
        void onSelectPhotoClicked(Dialog dialog);
    }

    private AddImageDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (AddImageDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AddImageDialogListener");
        }
    }

    public static AddImageDialog newInstance() {
        return new AddImageDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_image_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final Dialog dialog = builder.create();

        view.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTakePhotoClicked(dialog);
            }
        });

        view.findViewById(R.id.select_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectPhotoClicked(dialog);
            }
        });

        return dialog;
    }

}