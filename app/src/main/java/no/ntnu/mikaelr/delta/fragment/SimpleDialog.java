package no.ntnu.mikaelr.delta.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class SimpleDialog extends DialogFragment
{
    public SimpleDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        String title = args.getString("title", "");
        String message = args.getString("message", "");

        return new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes, null)
            .create();
    }
}