package no.ntnu.mikaelr.delta.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

public class SimpleDialog extends DialogFragment
{
    public SimpleDialog() {}

    public static void createAndShow(FragmentManager fragmentManager, String title, String message) {
        DialogFragment dialog = new SimpleDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        dialog.setArguments(args);
        dialog.show(fragmentManager, "tag");
    }

    public static SimpleDialog newInstance(String title, String message) {
        SimpleDialog dialog = new SimpleDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        String title = args.getString("title", "");
        String message = args.getString("message", "");

        return new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            })
            .create();
    }

}