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
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.util.DateFormatter;

import java.util.Collections;
import java.util.List;

public class SuggestionListAdapter extends BaseAdapter {

    private final Context context;
    private List<Suggestion> suggestions = Collections.emptyList();

    public SuggestionListAdapter(Context context) {
        this.context = context;
    }

    static class ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView details;
        TextView username;
        TextView date;
    }

    public void updateList(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Object getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_suggestion, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.details = (TextView) convertView.findViewById(R.id.details);
            viewHolder.username = (TextView) convertView.findViewById(R.id.username);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Suggestion suggestion = suggestions.get(position);

        if (!suggestion.getImageUri().equals("")) {
            Picasso.with(context).load(suggestion.getImageUri().replace(".jpg", "_thumbnail.jpg")).error(R.drawable.no_image).into(viewHolder.thumbnail);
        }
        viewHolder.title.setText(suggestion.getTitle());
        viewHolder.details.setText(suggestion.getDetails());
        viewHolder.username.setText(suggestion.getUser().getUsername());
        viewHolder.date.setText(DateFormatter.format(suggestion.getDate(), "dd.MM.yy "));

        return convertView;

    }

}
