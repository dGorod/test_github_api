package example.com.testgithub.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import example.com.testgithub.util.Logger;

/**
 * Helper class to work with app database.
 * Methods are added as needed.
 *
 * Created by Dmitriy Gorodnytskiy on 10/29/15.
 */
public class MyDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "test_github_database.db";
    private static final int DATABASE_VERSION = 1;

    private static MyDbHelper instance;

    private MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized MyDbHelper getInstance(Context context) {
        if (instance == null)
            instance = new MyDbHelper(context.getApplicationContext());

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL(UsersTable.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(UsersTable.SQL_CREATE_TABLE);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: currently we just recreate all with losing data, that not right
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL(UsersTable.SQL_DELETE_TABLE);
        sqLiteDatabase.execSQL(UsersTable.SQL_CREATE_TABLE);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Logger.v("database opened");
    }

    public Cursor getUsers() {
        SQLiteDatabase sdb = instance.getReadableDatabase();
        Cursor cursor;

        cursor = sdb.query(UsersTable.TABLE_NAME,
                new String[]{
                        UsersTable.ID,
                        UsersTable.LOGIN,
                        UsersTable.AVATAR_URL,
                        UsersTable.HTML_URL
                },
                null, null, null, null,
                UsersTable.LOGIN + " ASC");


        return cursor;
    }

    public int insertUsers(List<ContentValues> values) {

        if(values == null || values.isEmpty())
            return -1;

        SQLiteDatabase sdb = instance.getWritableDatabase();
        int count = 0;

        sdb.beginTransaction();

        for(ContentValues v : values) {
            if(v.size() > 0) {
                long id;

                try {
                    id = sdb.insertWithOnConflict(UsersTable.TABLE_NAME, null, v, SQLiteDatabase.CONFLICT_FAIL);
                }
                catch (SQLiteConstraintException ex) {
                    String whereClause = UsersTable.TABLE_NAME + "." + DatabaseTable.ID +"=?";
                    String[] whereArgs = new String[] { v.getAsString(DatabaseTable.ID) };

                    id = sdb.update(UsersTable.TABLE_NAME, v, whereClause, whereArgs);
                }

                if(id != -1L) count++;
            }
        }

        sdb.setTransactionSuccessful();
        sdb.endTransaction();

        return count;
    }
}
