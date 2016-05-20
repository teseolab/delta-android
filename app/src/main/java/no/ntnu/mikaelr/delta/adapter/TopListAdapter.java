package no.ntnu.mikaelr.delta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.util.CircleTransform;

import java.util.Collections;
import java.util.List;

public class TopListAdapter extends BaseAdapter {

    private Context context;

    private List<HighscoreUser> topList = Collections.emptyList();

    public TopListAdapter(Context context) {
        this.context = context;
    }

    static class ViewHolder {
        TextView position;
        ImageView avatar;
        TextView username;
        TextView score;
        TextView missionsCompleted;
        TextView suggestionsPosted;
        TextView commentsPosted;
    }

    public void updateList(List<HighscoreUser> topList) {
        this.topList = topList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return topList.size();
    }

    @Override
    public Object getItem(int position) {
        return topList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_top_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.position = (TextView) convertView.findViewById(R.id.position);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            viewHolder.username = (TextView) convertView.findViewById(R.id.username);
            viewHolder.score = (TextView) convertView.findViewById(R.id.score);
            viewHolder.missionsCompleted = (TextView) convertView.findViewById(R.id.missions_completed);
            viewHolder.suggestionsPosted = (TextView) convertView.findViewById(R.id.suggestions_posted);
            viewHolder.commentsPosted = (TextView) convertView.findViewById(R.id.comments_posted);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HighscoreUser user = topList.get(position);

        int listPosition = position + 1;
        viewHolder.position.setText("#" + listPosition);
        if (user.getAvatarUri() != null) {
            Picasso.with(context).load(user.getAvatarUri()).transform(new CircleTransform()).error(R.drawable.no_avatar).into(viewHolder.avatar);
        } else {
            viewHolder.avatar.setImageResource(R.drawable.no_avatar);
        }
        viewHolder.username.setText(user.getUsername());
        viewHolder.score.setText(user.getScore() + " poeng");
        viewHolder.missionsCompleted.setText(Integer.toString(user.getNumberOfMissions()));
        viewHolder.suggestionsPosted.setText(Integer.toString(user.getNumberOfSuggestions()));
        viewHolder.commentsPosted.setText(Integer.toString(user.getNumberOfComments()));

        return convertView;

    }

}
