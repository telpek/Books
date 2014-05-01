package com.contender.books;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;




public class MainView extends ActionBarActivity implements AdapterView.OnItemClickListener,
												 			EditOrDeleteDialogFragment.EditOrDeleteDialogListener,
												 			ScanOrManualDialogFragment.ScanOrManualDialogListener {

	public class BooksOpenHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 6;
		private static final String DATABASE_NAME = "books.db";
		public static final String BOOKS_TABLE_NAME = "books";
		public static final String COLUMN_NAME_BOOK = "books";
		public static final String COLUMN_NAME_CONTACT = "contacts";
		public static final String COLUMN_NAME_ISBN = "ISBN";
		public static final String COLUMN_NAME_LOANDATE = "loandate";
		public static final String COLUMN_NAME_DUEDATE = "duedate";
		public static final String COLUMN_NAME_HASREMINDER = "hasreminder";
		public static final String COLUMN_NAME_REMAININGDATE = "remainingdate";
		private static final String BOOKS_TABLE_CREATE =
				"CREATE TABLE " + BOOKS_TABLE_NAME + " (" +
						BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
						COLUMN_NAME_BOOK + " TEXT NOT NULL," +
						COLUMN_NAME_CONTACT + " TEXT NOT NULL," +
						COLUMN_NAME_ISBN + " TEXT," + 
						COLUMN_NAME_LOANDATE + " LONG NOT NULL," +
						COLUMN_NAME_DUEDATE + " LONG," +
						COLUMN_NAME_HASREMINDER + " BOOLEAN NOT NULL" + ");";

		BooksOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(BOOKS_TABLE_CREATE);
		}


		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Kills the table and existing data
			db.execSQL("DROP TABLE IF EXISTS books");

			// Recreates the database with a new version
			onCreate(db);
		}
	}
	
	public static BooksOpenHelper mDatabaseHelper;
	private static int dialog_id = -1;
	private ListView listView;
	private Cursor mCursor;
	BaseAdapter adapter;
	
	private String convText(TextView v, String text) {
		if(v.getId() == R.id.itemDuedateText) {
			
			Date dueDate = new Date(Long.parseLong(text));
			Date today = new Date(); 
			int days = Days.daysBetween(new DateTime(today), new DateTime(dueDate)).getDays();
			String remTime = new String();
			
			if(days == 0) {
				remTime = "Book is due today!";
				return remTime;
			}
			if(days < 0) {
				remTime = Math.abs(days) + " day" + (Math.abs(days) > 0 ? "s" : "" ) + " overdue";
				return remTime;
			}
			
			int x = days / 365;
			if(x > 0) {
				remTime = "+" + x + " year" + (x > 1 ? "s" : "" );
				return remTime;
			}
			
			x = days / 30;
			if(x > 0) {
				remTime = "+" + x + " month" + (x > 1 ? "s" : "" );
				return remTime;
			}
			x = days / 7;
			if(x > 0) {
				remTime = "+" + x + " week" + (x > 1 ? "s" : "" );
				return remTime;
			}
			if(days > 0) {
				remTime = "+" + x + " day" + (x > 1 ? "s" : "" );
				return remTime;
			}
			
			return remTime;
		}
		return text;
	}
	
	public void refreshOverview() {
				
		adapter = new SimpleCursorAdapter(this, R.layout.overview_item, mCursor, 
				new String[] { BooksOpenHelper.COLUMN_NAME_BOOK, BooksOpenHelper.COLUMN_NAME_CONTACT, BooksOpenHelper.COLUMN_NAME_DUEDATE }, 
				new int[] { R.id.itemBookText, R.id.itemContactText, R.id.itemDuedateText }, 0) {
			@Override
			public void setViewText(TextView v, String text) {
				super.setViewText(v,  convText(v, text));;
			}
		};
		listView.setAdapter(adapter);
//		adapter.notifyDataSetChanged();
	}

	public void showNoticeDialog() {
		DialogFragment dialog = new EditOrDeleteDialogFragment();
		dialog.show(getFragmentManager(), "EditOrDeleteDialogFragment");
	}
	
	@Override
	public void onDialogEditClick(DialogFragment dialog) {
		// Edit button
	}
	

	@Override
	public void onDialogDeleteClick(DialogFragment dialog) {
		// Delete button
		SQLiteDatabase mDB = MainView.mDatabaseHelper.getWritableDatabase();
		int i = dialog_id;
		mDB.delete(BooksOpenHelper.BOOKS_TABLE_NAME, BaseColumns._ID + "=" + i, null );
		
		mCursor = mDB.rawQuery("SELECT _id, books, contacts, duedate FROM books", new String[] {});

		refreshOverview();
	}
	
	@Override
	public void onDialogScanClick(DialogFragment dialog) {
		// Start scanner intent, catch result in this activity and launch addbook activity with ISBN data
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}
	
	@Override
	public void onDialogManualClick(DialogFragment dialog) {
		// launch addbook activity with no data
		Intent intent = new Intent(this, AddBookActivity.class);
		intent.putExtra(Intent.EXTRA_TEXT, "none");
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			// handle scan result
			// FIXME: HANDLE RESULT CODE 
			Intent newIntent = new Intent(this, AddBookActivity.class);
			newIntent.putExtra(Intent.EXTRA_TEXT, scanResult.getContents());
			startActivity(newIntent);			
		}
		// else continue with any other code you need in the method
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_view);

		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(this);

		mDatabaseHelper = new BooksOpenHelper(this);


	}

	@Override
	public void onResume() {
		super.onResume();

		SQLiteDatabase mDB = mDatabaseHelper.getReadableDatabase();
		mCursor = mDB.rawQuery("SELECT _id, books, contacts, duedate FROM books", new String[] {});
		// Manage this cursor ?
		
		refreshOverview();
		


	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_add:
			DialogFragment dialog = new ScanOrManualDialogFragment();
			dialog.show(getFragmentManager(),  "scan_or_manual");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		// map position to _id in DB
		dialog_id = (int) adapter.getItemId(position);
		
		DialogFragment dialog = new EditOrDeleteDialogFragment();
		dialog.show(getFragmentManager(), "edit_or_delete");

		/* Context context = getApplicationContext();
		CharSequence text = "Received Click event on item" + position + " dialog id set to " + dialog_id;
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context,  text,  duration);
		toast.show(); */ 
	}



}

