package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.TopListAdapter;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.TopListPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.TopListPresenter;
import no.ntnu.mikaelr.delta.view.signature.TopListView;

import java.util.List;

public class TopListFragment extends Fragment implements TopListView, AdapterView.OnItemClickListener {

    private TopListPresenter presenter;
    private TopListAdapter listAdapter;
    private ListView topList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_list, container, false);

        presenter = new TopListPresenterImpl(this);

        topList = (ListView) view.findViewById(R.id.top_list);
        listAdapter = new TopListAdapter(getActivity());
        topList.setAdapter(listAdapter);
        topList.setOnItemClickListener(this);

        presenter.loadTopList();

        return view;
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void updateList(List<HighscoreUser> topList) {
        listAdapter.updateList(topList);
    }

    @Override
    public void showMessage(String message, int length) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showMessage(message, length);
    }

    @Override
    public void setEmptyListMessage(String message) {
        addFooterView(message);
    }

    // PRIVATE METHODS -------------------------------------------------------------------------------------------------

    private void addFooterView(String message) {

        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.list_item_empty, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.message);
        textView.setText(message);
        textView.setTextSize(16);
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        textView.setPadding(0, dp, 0, 0);
        topList.addFooterView(emptyView);

    }

    // CLICK LISTENER --------------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        presenter.onItemClick(position);
    }
}
