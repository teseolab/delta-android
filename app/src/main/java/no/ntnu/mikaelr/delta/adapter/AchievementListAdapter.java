package no.ntnu.mikaelr.delta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.Achievement;
import no.ntnu.mikaelr.delta.util.BadgeIdConverter;

import java.util.Collections;
import java.util.List;

public class AchievementListAdapter extends BaseAdapter {

    private final Context context;
    private List<Achievement> achievements = Collections.emptyList();

    public AchievementListAdapter(Context context) {
        this.context = context;
    }

    static class ViewHolder {
        ImageView badge;
        TextView name;
        TextView description;
    }

    public void updateList(List<Achievement> achievements) {
        this.achievements = achievements;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return achievements.size();
    }

    @Override
    public Object getItem(int position) {
        return achievements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_achievement, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.badge = (ImageView) convertView.findViewById(R.id.badge);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Achievement achievement = achievements.get(position);

        viewHolder.badge.setImageResource(BadgeIdConverter.getInstance().convertBadgeNameToResourceId(achievement.getBadgeName()));
        viewHolder.name.setText(achievement.getName());
        viewHolder.description.setText(achievement.getDescription());

        return convertView;

    }

}
