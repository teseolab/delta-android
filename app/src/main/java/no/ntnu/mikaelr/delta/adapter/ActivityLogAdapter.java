package no.ntnu.mikaelr.delta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import no.ntnu.mikaelr.delta.R;
import no.ntnu.mikaelr.delta.model.HighscoreUser;
import no.ntnu.mikaelr.delta.model.LogRecord;
import no.ntnu.mikaelr.delta.util.DateFormatter;
import no.ntnu.mikaelr.delta.util.LogRecordType;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ActivityLogAdapter extends BaseAdapter {

    private Context context;

    private List<LogRecord> logRecords = Collections.emptyList();

    private final int ITEM_VIEW_NORMAL = 0;
    private final int ITEM_VIEW_DIVIDER = 1;


    public ActivityLogAdapter(Context context) {
        this.context = context;
    }

    static class ViewHolderNormal {
        ImageView icon;
        TextView description;
        TextView score;
    }

    static class ViewHolderDivider {
        TextView date;
        ImageView icon;
        TextView description;
        TextView score;
    }

    public void updateList(List<LogRecord> logRecords) {
        this.logRecords = logRecords;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        String previousDate = "";
        if (position != 0) {
            previousDate = DateFormatter.format(logRecords.get(position-1).getDate(), "dd.MM.yyyy");
        }
        String logRecordDate = DateFormatter.format(logRecords.get(position).getDate(), "dd.MM.yyyy");
        int itemViewType = ITEM_VIEW_NORMAL;
        if (position == 0 || !logRecordDate.equals(previousDate)) {
            itemViewType = ITEM_VIEW_DIVIDER;
        }
        return itemViewType;
    }

    @Override
    public int getCount() {
        return logRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return logRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LogRecord logRecord = logRecords.get(position);

        int type = getItemViewType(position);

            if (type == ITEM_VIEW_NORMAL) {

                ViewHolderNormal viewHolderNormal;
                if (convertView == null) {

                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item_activity_log, parent, false);
                    viewHolderNormal = new ViewHolderNormal();
                    viewHolderNormal.icon = (ImageView) convertView.findViewById(R.id.icon);
                    viewHolderNormal.description = (TextView) convertView.findViewById(R.id.description);
                    viewHolderNormal.score = (TextView) convertView.findViewById(R.id.score);
                    convertView.setTag(viewHolderNormal);
                } else {
                    viewHolderNormal = (ViewHolderNormal) convertView.getTag();
                }

                viewHolderNormal.icon.setImageResource(resourceIdFromLogRecordType(logRecord.getType()));
                viewHolderNormal.description.setText(logRecord.getDescription());
                viewHolderNormal.score.setText("+" + logRecord.getGeneratedScore());
            }

            else if (type == ITEM_VIEW_DIVIDER) {

                ViewHolderDivider viewHolderDivider;
                if (convertView == null) {

                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item_activity_log_divider, parent, false);
                    viewHolderDivider = new ViewHolderDivider();
                    viewHolderDivider.date = (TextView) convertView.findViewById(R.id.date);
                    viewHolderDivider.icon = (ImageView) convertView.findViewById(R.id.icon);
                    viewHolderDivider.description = (TextView) convertView.findViewById(R.id.description);
                    viewHolderDivider.score = (TextView) convertView.findViewById(R.id.score);
                    convertView.setTag(viewHolderDivider);
                } else {
                    viewHolderDivider = (ViewHolderDivider) convertView.getTag();
                }

                viewHolderDivider.date.setText(DateFormatter.format(logRecord.getDate(), "d. MMMM"));
                viewHolderDivider.icon.setImageResource(resourceIdFromLogRecordType(logRecord.getType()));
                viewHolderDivider.description.setText(logRecord.getDescription());
                viewHolderDivider.score.setText("+" + logRecord.getGeneratedScore());

            }

        return convertView;

    }

    private int resourceIdFromLogRecordType(LogRecordType type) {
        int resourceId = 0;
        switch (type) {
            case MISSION:
                resourceId =  R.drawable.explore;
                break;
            case SUGGESTION:
                resourceId =  R.drawable.lightbulb;
                break;
            case COMMENT:
                resourceId =  R.drawable.forum;
                break;
            case AGREEMENT:
                resourceId =  R.drawable.ic_thumbs_up;
                break;
            case DISAGREEMENT:
                resourceId =  R.drawable.ic_thumbs_down;
                break;
            case SUGGESTION_AGREEMENT:
                resourceId =  R.drawable.ic_thumbs_up;
                break;
        }
        return resourceId;
    }

}
