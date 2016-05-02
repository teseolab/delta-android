package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.ActivityLogAdapter;
import no.ntnu.mikaelr.delta.model.LogRecord;
import no.ntnu.mikaelr.delta.presenter.ActivityLogPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.ActivityLogPresenter;
import no.ntnu.mikaelr.delta.view.signature.ActivityLogView;

import java.util.List;

public class ActivityLogFragment extends Fragment implements ActivityLogView {

    private ActivityLogAdapter listAdapter;
    private View view;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activity_log, container, false);

        ActivityLogPresenter presenter = new ActivityLogPresenterImpl(this);
        listAdapter = new ActivityLogAdapter(getActivity());

        listView = (ListView) view.findViewById(R.id.activity_log_list);
        listView.setAdapter(listAdapter);

        presenter.loadLogRecords();

        return view;
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void setEmptyListMessage(String message) {
        addFooterView(message);
    }

    @Override
    public void updateView(List<LogRecord> logRecords) {
        listAdapter.updateList(logRecords);
    }

    @Override
    public void hideProgressSpinner() {}

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private void addFooterView(String message) {

        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.list_item_empty, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.message);
        textView.setText(message);
        textView.setTextSize(16);
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        textView.setPadding(0, dp, 0, 0);
        listView.addFooterView(emptyView);

    }

}
