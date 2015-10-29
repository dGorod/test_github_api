package example.com.testgithub.api;

import android.content.Context;

import example.com.testgithub.BuildConfig;
import example.com.testgithub.R;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;

/**
 * Created by Dmitriy Gorodnytskiy on 28-Oct-15.
 */
public abstract class ApiService {
    private static ApiInterface instance;

    private ApiService() { }

    public static synchronized ApiInterface init(Context context) {
        if(context == null)
            throw new NullPointerException();

        if(instance == null) {
            RestAdapter restAdapter = new RestAdapter
                    .Builder()
                    .setEndpoint(context.getString(R.string.api_url))
                    .setLog(new AndroidLog(context.getApplicationContext().getPackageName()))
                    .build();

            if(BuildConfig.DEBUG)
                restAdapter.setLogLevel(RestAdapter.LogLevel.BASIC);

            instance = restAdapter.create(ApiInterface.class);
        }

        return instance;
    }
}