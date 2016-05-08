package no.ntnu.mikaelr.delta.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.adapter.ProjectListAdapter;
import no.ntnu.mikaelr.delta.model.Project;
import no.ntnu.mikaelr.delta.presenter.ProjectListPresenterImpl;
import no.ntnu.mikaelr.delta.presenter.signature.ProjectListPresenter;
import no.ntnu.mikaelr.delta.view.signature.ProjectListView;

import java.util.ArrayList;
import java.util.List;

public class ProjectListFragment extends Fragment implements ProjectListView, ProjectListAdapter.OnItemClickListener {

    private ProjectListPresenter presenter;

    public static ProjectListFragment newInstance(List<Project> projects) {
        Bundle args = new Bundle();
        args.putSerializable("projects", (ArrayList) projects);
        ProjectListFragment fragment = new ProjectListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_list, container, false);

        this.presenter = new ProjectListPresenterImpl(this);

        RecyclerView cardList = (RecyclerView) view.findViewById(R.id.card_list);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);

        @SuppressWarnings("unchecked")
        ArrayList<Project> projects = (ArrayList<Project>) getArguments().getSerializable("projects");
        ProjectListAdapter adapter = new ProjectListAdapter(this, projects);
        cardList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(Project project) {
        presenter.goToProjectPage(project);
    }
}
