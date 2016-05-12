package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.util.ErrorMessage;

public class SimpleMessageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_item_empty, container, false);
        TextView message = (TextView) view.findViewById(R.id.message);
        message.setText(ErrorMessage.COULD_NOT_LOAD_PROJECTS);
        message.setTextSize(16);
        return view;
    }
}
