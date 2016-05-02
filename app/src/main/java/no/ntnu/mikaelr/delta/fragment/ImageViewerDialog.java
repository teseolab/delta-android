package no.ntnu.mikaelr.delta.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;

public class ImageViewerDialog extends DialogFragment {

    public ImageViewerDialog() {}

    public static ImageViewerDialog newInstance(String imageUri) {
        Bundle args = new Bundle();
        args.putString("imageUri", imageUri);
        ImageViewerDialog dialog = new ImageViewerDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.image);

        String imageUri = getArguments().getString("imageUri", "");
        Picasso.with(getActivity())
                .load(imageUri)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, this).addToBackStack("").commit();
    }

    @Override
    public void dismiss() {
        FragmentManager manager = getFragmentManager();
        manager.popBackStack();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.remove(this).commit();
    }

}