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
import no.ntnu.mikaelr.delta.model.Comment;
import no.ntnu.mikaelr.delta.model.Suggestion;
import no.ntnu.mikaelr.delta.util.CircleTransform;
import no.ntnu.mikaelr.delta.util.DateFormatter;

import java.util.Collections;
import java.util.List;

public class CommentListAdapter extends BaseAdapter {

    private final Context context;
    private List<Comment> comments = Collections.emptyList();

    public CommentListAdapter(Context context) {
        this.context = context;
    }

    static class ViewHolder {
        ImageView thumbnail;
        TextView usernameAndDate;
        TextView comment;
    }

    public void updateList(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            viewHolder.usernameAndDate = (TextView) convertView.findViewById(R.id.username_and_date);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Comment comment = comments.get(position);

        String avatarUri = comment.getUser().getAvatarUri();
        if (avatarUri != null) {
            Picasso.with(context).load(avatarUri.replace(".jpg", "_thumbnail.jpg")).transform(new CircleTransform()).error(R.drawable.no_avatar).into(viewHolder.thumbnail);
        } else {
            viewHolder.thumbnail.setImageResource(R.drawable.no_avatar);
        }

        viewHolder.usernameAndDate.setText(comment.getUser().getUsername() + " @ " + DateFormatter.format(comment.getDate(), "dd.MM.yy"));
        viewHolder.comment.setText(comment.getComment());

        return convertView;

    }

}
