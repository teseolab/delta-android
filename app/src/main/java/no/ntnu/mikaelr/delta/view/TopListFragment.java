package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.TopListAdapter;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.presenter.TopListPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.TopListPresenter;
import no.ntnu.mikaelr.delta.view.signature.TopListView;

import java.util.List;

public class TopListFragment extends Fragment implements TopListView {

    private TopListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_list, container, false);

        TopListPresenter presenter = new TopListPresenterImpl(this);
        presenter.loadTopList();

        listAdapter = new TopListAdapter(getActivity());

        ListView topList = (ListView) view.findViewById(R.id.top_list);
        topList.setAdapter(listAdapter);

        return view;
    }

    // INTERFACE METHODS -----------------------------------------------------------------------------------------------

    @Override
    public void updateList(List<HighscoreUser> topList) {
        listAdapter.updateList(topList);
    }
}
