package com.vijaysharma.expenses.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.vijaysharma.expenses.database.models.Expense;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ExpenseProvider extends ContentProvider {
    private static String AUTHORITY = "com.vijaysharma.expenses";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static String PATH_EXPENSES = "expenses";
    private static String EXPENSE_CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH_EXPENSES;;
    private static String EXPENSE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "/" + PATH_EXPENSES;
    public static final Uri EXPENSE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXPENSES).build();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int CODE_EXPENSE = 0;
    private static final int CODE_EXPENSE_ID = 1;

    private ExpenseDatabase database;

    @Override
    public boolean onCreate() {
        database = new ExpenseDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        switch (sUriMatcher.match(uri)) {
            case CODE_EXPENSE:
                return EXPENSE_CONTENT_TYPE;
            case CODE_EXPENSE_ID:
                return EXPENSE_CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = null;
        switch (sUriMatcher.match(uri)) {
            case CODE_EXPENSE:
                cursor = cupboard().withDatabase(db).query(Expense.class)
                    .withProjection(projection)
                    .withSelection(selection, selectionArgs)
                    .orderBy(sortOrder)
                    .getCursor();
                break;
            case CODE_EXPENSE_ID:
                cursor = cupboard().withDatabase(db).query(Expense.class)
                    .byId(ContentUris.parseId(uri))
                    .getCursor();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = database.getWritableDatabase();
        Uri objectUri = null;
        switch (sUriMatcher.match(uri)) {
            case CODE_EXPENSE:
                long id = cupboard()
                    .withDatabase(db)
                    .put(Expense.class, values);
                if ( id > 0 ) {
                    objectUri = ContentUris.withAppendedId(EXPENSE_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return objectUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_EXPENSE:
                rowsDeleted = cupboard()
                    .withDatabase(db)
                    .delete(Expense.class, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_EXPENSE:
                rowsUpdated = cupboard().withDatabase(db).update(Expense.class, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = database.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_EXPENSE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = cupboard().withDatabase(db).put(Expense.class, value);
                        if (_id != -1) { returnCount++; }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, PATH_EXPENSES, CODE_EXPENSE);
        matcher.addURI(AUTHORITY, PATH_EXPENSES + "/#", CODE_EXPENSE_ID);

        return matcher;
    }
}
