package example.com.testgithub.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import example.com.testgithub.R;
import example.com.testgithub.db.UsersTable;
import example.com.testgithub.util.RequestDataListener;

/**
 * Created by Dmitriy Gorodnytskiy on 28-Oct-15.
 */
public class UsersAdapter extends CursorRecyclerViewAdapter<UserViewHolder> {
    private Context context;
    private RequestDataListener listener;

    public UsersAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    public void setListener(RequestDataListener listener) {
        this.listener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(context, v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, Cursor cursor) {
        String login = cursor.getString(cursor.getColumnIndex(UsersTable.LOGIN));
        String avatar = cursor.getString(cursor.getColumnIndex(UsersTable.AVATAR_URL));

        holder.bind(login, avatar);

        if(listener != null && closeToEnd(cursor.getPosition()))
            listener.onRequestData(cursor.getPosition());
    }

    private boolean closeToEnd(int position) {
        return (getItemCount() / (position + 1.0f)) < 1.3f;
    }
}
