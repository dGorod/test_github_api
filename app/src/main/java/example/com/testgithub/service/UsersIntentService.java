package example.com.testgithub.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import java.util.List;

import example.com.testgithub.api.ApiService;
import example.com.testgithub.api.UserModel;
import example.com.testgithub.db.MyDbHelper;
import example.com.testgithub.db.UsersTable;
import example.com.testgithub.util.Logger;
import example.com.testgithub.util.ResultReceiverCodes;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Dmitriy Gorodnytskiy on 29-Oct-15.
 */
public class UsersIntentService  extends IntentService {
    private final static int DEFAULT_LIMIT = 100;
    private final static String NAME = "UsersIntentService";
    public final static String RECEIVER_NAME = "UsersServiceResultReceiver";
    public final static String OFFSET = "offset";

    public UsersIntentService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER_NAME);
        final int offset = intent.getIntExtra(OFFSET, 0);

        receiver.send(ResultReceiverCodes.RUNNING, Bundle.EMPTY);
        Logger.d(NAME + ": service started");

        ApiService.init(getApplicationContext()).getUsers(offset, DEFAULT_LIMIT,
            new Callback<List<UserModel>>() {
                @Override
                public void success(List<UserModel> users, Response response) {
                    MyDbHelper.getInstance(getApplicationContext()).insertUsers(UsersTable.convert(users));

                    receiver.send(ResultReceiverCodes.FINISHED, Bundle.EMPTY);
                    Logger.d(NAME + ": service received data");
                }

                @Override
                public void failure(RetrofitError error) {
                    receiver.send(ResultReceiverCodes.FAILED, Bundle.EMPTY);
                    Logger.d(NAME + ": services failed: " + error.getUrl() + "\n" + error.getMessage());
                }
        });
    }
}
