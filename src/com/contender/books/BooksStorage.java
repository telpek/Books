package com.contender.books;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

public class BooksStorage {

	private static final int DATABASE_VERSION = 7;

	private static final String DATABASE_NAME = "books.db";

	// The table where we store books currently on loan
	public static final String BOOKS_TABLE_NAME = "books";
	public static final String HISTORY_TABLE_NAME = "history";

	// The data columns
	public static final String COLUMN_NAME__ID = BaseColumns._ID;
	public static final String COLUMN_NAME_BOOK = "books";
	public static final String COLUMN_NAME_CONTACT = "contacts";
	public static final String COLUMN_NAME_ISBN = "ISBN";
	public static final String COLUMN_NAME_AUTHOR = "author";
	public static final String COLUMN_NAME_LOANDATE = "loandate";
	public static final String COLUMN_NAME_DUEDATE = "duedate";
	public static final String COLUMN_NAME_HASREMINDER = "hasreminder";


	private BooksHelper mBooksHelper;

	static class BooksHelper extends SQLiteOpenHelper {

		BooksHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + BOOKS_TABLE_NAME + " (" +
					COLUMN_NAME__ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					COLUMN_NAME_BOOK + " TEXT NOT NULL," +
					COLUMN_NAME_CONTACT + " TEXT NOT NULL," +
					COLUMN_NAME_ISBN + " TEXT," + 
					COLUMN_NAME_AUTHOR + " TEXT," +
					COLUMN_NAME_LOANDATE + " LONG NOT NULL," +
					COLUMN_NAME_DUEDATE + " LONG NOT NULL," +
					COLUMN_NAME_HASREMINDER + " BOOLEAN NOT NULL" + ");");
		}


		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// TODO: Notify user of DB extermination

			// TODO: Upgrade nicely once code is settled

			// Kills the table and existing data
			db.execSQL("DROP TABLE IF EXISTS books");

			// Recreates the database with a new version
			onCreate(db);
		}
	}


	public BooksStorage(Context context) {

		mBooksHelper = new BooksHelper(context);

	}

	// Returns a cursor to the DB pointing to the requested data
	// Will return null if the query is not valid
	// TODO: handle sortOrder
	public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {

		SQLiteQueryBuilder qr = new SQLiteQueryBuilder();
		if(table != null)
			qr.setTables(table);
		else
			return null;

		SQLiteDatabase db = mBooksHelper.getReadableDatabase();

		Cursor c = qr.query(db, projection, selection, selectionArgs, null, null, null);
		return c;
	}

	// Enter a new row in the DB, returns the row ID of the inserted row.
	public long insert(String table, ContentValues initialValues) {

		if(table != HISTORY_TABLE_NAME && table != BOOKS_TABLE_NAME)
			return -1;

		ContentValues values;
		if(initialValues != null) {
			values = new ContentValues(initialValues);

		} else {
			return -1;
		}

		// Check if any of the not null columns are missing data
		if(values.containsKey(COLUMN_NAME_BOOK) == false || values.containsKey(COLUMN_NAME_CONTACT) == false ||
				values.containsKey(COLUMN_NAME_LOANDATE) == false || values.containsKey(COLUMN_NAME_DUEDATE) == false || 
				values.containsKey(COLUMN_NAME_LOANDATE) == false)
			return -1;
		
		SQLiteDatabase db = mBooksHelper.getWritableDatabase();
		
		return db.insert(table, COLUMN_NAME_ISBN, values);
	}
	
	public int delete(String table, String where, String[] whereArgs) {
		
		int count;
		SQLiteDatabase db = mBooksHelper.getWritableDatabase();
		
		if(table != HISTORY_TABLE_NAME && table != BOOKS_TABLE_NAME)
			return -1;
		
		count = db.delete(table, where, whereArgs);
		
		return count;
	}


	public int update(String table, ContentValues values, String where, String[] whereArgs) {
		
		int count;
		SQLiteDatabase db = mBooksHelper.getWritableDatabase();
		
		if(table != HISTORY_TABLE_NAME && table != BOOKS_TABLE_NAME)
			return -1;
		
		if(where == null) {
			Log.w("Books", "Please dont murder the DB.");
			return -1;
		}
			
		
		count = db.update(table, values, where, whereArgs);
		
		return count;
	}




}