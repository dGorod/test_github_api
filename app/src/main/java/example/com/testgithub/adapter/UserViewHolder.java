package example.com.testgithub.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import example.com.testgithub.R;

/**
 * Created by Dmitriy Gorodnytskiy on 29-Oct-15.
 */
public class UserViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.ivImage)
    ImageView image;
    @Bind(R.id.tvLogin)
    TextView name;

    private Context context;

    public UserViewHolder(Context context, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
    }

    public void bind(String login, String avatar) {
        Picasso.with(context).cancelRequest(image);
        Picasso.with(context).load(avatar).into(image);
        name.setText(login);
    }
}
