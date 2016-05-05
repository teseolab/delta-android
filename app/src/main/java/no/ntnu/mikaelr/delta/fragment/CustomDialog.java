package no.ntnu.mikaelr.delta.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import no.ntnu.mikaelr.delta.R;

public class CustomDialog extends DialogFragment {

    public interface CustomDialogPositiveButtonListener {
        void onPositiveButtonClick(String dialogTag);
    }

    public interface CustomDialogNegativeButtonListener {
        void onNegativeButtonClick(String dialogTag);
    }

    private CustomDialogPositiveButtonListener positiveButtonListener;
    private CustomDialogNegativeButtonListener negativeButtonListener;

    public static CustomDialog newInstance(@NonNull String title, @NonNull String message, @NonNull String yesButtonText, String noButtonText, int imageResourceId) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("yesButtonText", yesButtonText);
        if (imageResourceId != 0) args.putInt("imageResourceId", imageResourceId);
        if (noButtonText != null) args.putString("noButtonText", noButtonText);
        CustomDialog dialog = new CustomDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            positiveButtonListener = (CustomDialogPositiveButtonListener) activity;
        } catch (ClassCastException e) {
            positiveButtonListener = null;
        }
        try {
            negativeButtonListener = (CustomDialogNegativeButtonListener) activity;
        } catch (ClassCastException e) {
            negativeButtonListener = null;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_custom_dialog, null);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView messageView = (TextView) view.findViewById(R.id.message);
        titleView.setText(getArguments().getString("title"));
        messageView.setText(getArguments().getString("message"));

        ImageView imageView = (ImageView) view.findViewById(R.id.image);

        int imageResourceId = getArguments().getInt("imageResourceId");

        if (imageResourceId != 0) {
            imageView.setImageResource(imageResourceId);
        } else {
            imageView.setVisibility(View.GONE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        builder.setPositiveButton(getArguments().getString("yesButtonText"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                if (positiveButtonListener != null) {
                    positiveButtonListener.onPositiveButtonClick(getTag());
                } else {
                    dismiss();
                }
            }
        });

        final String negativeButton = getArguments().getString("noButtonText");
        if (negativeButton != null) {
            builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (negativeButtonListener != null) {
                        negativeButtonListener.onNegativeButtonClick(getTag());
                    } else {
                        dismiss();
                    }
                }
            });
        }

        return builder.create();
    }

}