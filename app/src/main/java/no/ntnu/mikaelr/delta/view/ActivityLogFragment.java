package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.ActivityLogAdapter;
import no.ntnu.mikaelr.delta.model.LogRecord;
import no.ntnu.mikaelr.delta.presenter.ActivityLogPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.ActivityLogPresenter;
import no.ntnu.mikaelr.delta.view.signature.ActivityLogView;

import java.util.List;

public class ActivityLogFragment extends Fragment implements ActivityLogView {

    private ActivityLogAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_log, container, false);

        ActivityLogPresenter presenter = new ActivityLogPresenterImpl(this);
        listAdapter = new ActivityLogAdapter(getActivity());

        ListView listView = (ListView) view.findViewById(R.id.activity_log_list);
        listView.setAdapter(listAdapter);

        presenter.loadLogRecords();

        return view;
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void updateView(List<LogRecord> logRecords) {
        listAdapter.updateList(logRecords);
    }
}
