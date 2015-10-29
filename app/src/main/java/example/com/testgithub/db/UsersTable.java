package example.com.testgithub.db;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

import example.com.testgithub.api.UserModel;

/**
 * Created by Dmitriy Gorodnytskiy on 10/29/15.
 */
public abstract class UsersTable implements DatabaseTable {
    public final static String TABLE_NAME = "users_table";

    public final static String LOGIN = "login";
    public final static String AVATAR_URL = "avatar_url";
    public final static String HTML_URL = "html_url";

    protected static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (" + ID + " TEXT PRIMARY KEY NOT NULL, " +
            LOGIN + " TEXT  NOT NULL, " +
            AVATAR_URL + " TEXT, " +
            HTML_URL + " TEXT);";

    protected static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static ContentValues makeContentValue(UserModel m) {
        ContentValues v = new ContentValues();

        v.put(ID, m.getId());
        v.put(LOGIN, m.getLogin());
        v.put(AVATAR_URL, m.getAvatarUrl());
        v.put(HTML_URL, m.getHtmlUrl());

        return v;
    }

    public static List<ContentValues> convert(List<? extends UserModel> models) {
        List<ContentValues> values = new ArrayList<>();

        for(UserModel m : models)
            values.add(makeContentValue(m));

        return values;
    }
}
