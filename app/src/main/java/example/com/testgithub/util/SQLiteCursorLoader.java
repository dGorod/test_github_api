package example.com.testgithub.util;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * A counterpart to the Android Frameworks CursorLoader. Rather than performing the query against a
 * ContentProvider, this class performs the query against a SQLiteDatabase.
 */
public class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {
    final ForceLoadContentObserver mObserver;

    private SQLiteDatabase mDatabase;
    private String mRawQuery;
    private String[] mSelectionArgs;
    private Cursor mCursor;

    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        Cursor cursor = mDatabase.rawQuery(mRawQuery, mSelectionArgs);
        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            registerContentObserver(cursor, mObserver);
        }
        return cursor;
    }

    /**
     * Registers an observer to get notifications from the content provider when the cursor needs to
     * be refreshed.
     */
    private void registerContentObserver(Cursor cursor, ContentObserver observer) {
        cursor.registerContentObserver(mObserver);
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null)
                cursor.close();

            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted())
            super.deliverResult(cursor);

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed())
            oldCursor.close();
    }

    /**
     * Creates an empty unspecified SQLiteCursorLoader. You must follow this with calls to
     * {@link #setRawQuery(String))} and {@link #setSelectionArgs(String[])} to specify the query to
     * perform.
     */
    public SQLiteCursorLoader(Context context, SQLiteDatabase database) throws IllegalStateException {
        super(context);

        if(database == null || !database.isOpen())
            throw new IllegalStateException("SQLiteCursorLoader constructor: database given is not open.");

        mDatabase = database;
        mObserver = new ForceLoadContentObserver();
    }

    /**
     * Creates a fully-specified SQLiteCursorLoader. See
     * {@link SQLiteDatabase#rawQuery(String, String[]) SQLiteDatabase.rawQuery()} for documentation
     * on the meaning of the parameters. These will be passed as-is to that call.
     */
    public SQLiteCursorLoader(Context context, SQLiteDatabase database, String rawQuery,
                              String... selectionArgs) throws IllegalArgumentException{
        this(context, database);

        if(rawQuery == null || rawQuery.isEmpty())
            throw new IllegalArgumentException("SQLiteCursorLoader constructor: rawQuery is null or empty.");

        mRawQuery = rawQuery;
        mSelectionArgs = selectionArgs;
    }

    /**
     * Starts an asynchronous load of the data. When the result is ready the callbacks will be
     * called on the UI thread. If a previous load has been completed and is still valid the result
     * may be passed to the callbacks immediately.
     * <p/>
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mCursor != null)
            deliverResult(mCursor);
        if (takeContentChanged() || mCursor == null)
            forceLoad();
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed())
            cursor.close();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed())
            mCursor.close();

        mCursor = null;
    }

    public String getRawQuery() {
        return mRawQuery;
    }

    public void setRawQuery(String rawQuery) throws IllegalArgumentException {
        if(rawQuery == null || rawQuery.isEmpty())
            throw new IllegalArgumentException("rawQuery is null or empty.");

        mRawQuery = rawQuery;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public void setSelectionArgs(String... selectionArgs) {
        mSelectionArgs = selectionArgs;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("mDatabase=");
        writer.println(mDatabase.toString());
        writer.print(prefix);
        writer.print("mRawQuery=");
        writer.println(mRawQuery);
        writer.print(prefix);
        writer.print("mSelectionArgs=");
        writer.println(Arrays.toString(mSelectionArgs));
        writer.print(prefix);
        writer.print("mCursor=");
        writer.println(mCursor);
        writer.print(prefix);
    }
}
