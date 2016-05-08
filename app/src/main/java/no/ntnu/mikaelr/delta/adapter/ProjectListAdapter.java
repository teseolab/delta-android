package no.ntnu.mikaelr.delta.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.Project;

import java.util.List;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder> {

    private List<Project> projects;

    public interface OnItemClickListener {
        void onItemClick(Project project);
    }

    private final OnItemClickListener listener;

    public ProjectListAdapter(OnItemClickListener listener, List<Project> projects) {
        this.listener = listener;
        this.projects = projects;
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_project, viewGroup, false);
        return new ProjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder viewHolder, int i) {
        viewHolder.bind(projects.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView title;
        protected TextView description;
        protected Button button;

        public ProjectViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            title = (TextView) v.findViewById(R.id.title);
            description = (TextView) v.findViewById(R.id.description);
            button = (Button) v.findViewById(R.id.button);
        }

        public void bind(final Project project, final OnItemClickListener listener) {
            String imageUri = project.getImageUri();
            if (imageUri != null) {
                Picasso.with(itemView.getContext()).load(imageUri).into(image);
            }
            title.setText(project.getName());
            description.setText(project.getDescription());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(project);
                }
            });
        }
    }

}
