package example.com.testgithub.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.os.ResultReceiver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import example.com.testgithub.R;
import example.com.testgithub.adapter.UsersAdapter;
import example.com.testgithub.db.MyDbHelper;
import example.com.testgithub.db.UsersTable;
import example.com.testgithub.util.RecyclerItemClickListener;
import example.com.testgithub.util.RequestDataListener;
import example.com.testgithub.util.ResultReceiverCodes;
import example.com.testgithub.util.SQLiteCursorLoader;
import example.com.testgithub.service.UsersIntentService;

/**
 * Created by Dmitriy Gorodnytskiy on 28-Oct-15.
 */
public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>,
                    RequestDataListener {

    @Bind(R.id.srlRefresh)
    SwipeRefreshLayout refresh;
    @Bind(R.id.rvUsers)
    RecyclerView users;

    private final static int USERS_LOADER = 1001;

    private UsersAdapter adapter;
    private boolean isWorking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        refresh.setColorSchemeResources(R.color.blue, R.color.green, R.color.orange, R.color.red);
        refresh.setOnRefreshListener(this);

        users.setLayoutManager(new LinearLayoutManager(this));
        users.setItemAnimator(new DefaultItemAnimator());
        users.addOnItemTouchListener(new RecyclerItemClickListener(this, usersClickListener));

        adapter = new UsersAdapter(this, null);
        adapter.setListener(this);
        users.setAdapter(adapter);

        getSupportLoaderManager().initLoader(USERS_LOADER, null, this);

        // Workaround, see:
        // http://stackoverflow.com/questions/26858692/swiperefreshlayout-setrefreshing-not-showing-indicator-initially
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(adapter.getItemCount() == 0)
                    refresh.setRefreshing(true);

                onRefresh();
            }
        }, 1000);
    }

    @Override
    public void onRefresh() {
        if(refresh == null || isWorking)
            return;

        startUsersService(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = "SELECT * FROM " + UsersTable.TABLE_NAME +
                " ORDER BY " + UsersTable.LOGIN + " ASC";

        return new SQLiteCursorLoader(this, MyDbHelper.getInstance(this).getReadableDatabase(),
                query, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
        isWorking = false;
        refresh.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    public void onRequestData(int position) {
        if(!isWorking) {
            isWorking = true;
            startUsersService(position);
        }
    }

    private void startUsersService(int offset) {
        Intent intent = new Intent(this, UsersIntentService.class);
        intent.putExtra(UsersIntentService.RECEIVER_NAME, receiver);
        intent.putExtra(UsersIntentService.OFFSET, offset);
        startService(intent);
    }

    private ResultReceiver receiver = new ResultReceiver(new Handler()) {
        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            switch(resultCode) {
                case ResultReceiverCodes.FINISHED:
                    getSupportLoaderManager().restartLoader(USERS_LOADER, null, MainActivity.this);
                    break;
                case ResultReceiverCodes.FAILED:
                    isWorking = false;
                    Toast.makeText(MainActivity.this, R.string.error_server, Toast.LENGTH_LONG).show();
                    refresh.setRefreshing(false);
                    break;
            }
        }
    };

    private RecyclerItemClickListener.OnItemClickListener usersClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Cursor c = adapter.getCursor();
            c.moveToPosition(position);
            String url = c.getString(c.getColumnIndex(UsersTable.HTML_URL));

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    };
}
