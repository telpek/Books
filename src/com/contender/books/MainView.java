package com.contender.books;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
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

	private static int dialog_id = -1;
	private ListView listView;
	public static BooksStorage BooksProvider;
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

		mCursor = BooksProvider.query(BooksStorage.BOOKS_TABLE_NAME, new String[] { BooksStorage.COLUMN_NAME__ID, BooksStorage.COLUMN_NAME_BOOK,
				BooksStorage.COLUMN_NAME_CONTACT, BooksStorage.COLUMN_NAME_DUEDATE }, null, null, null);

		adapter = new SpecialCursorAdapter(this, R.layout.overview_item, mCursor, 
				new String[] { BooksStorage.COLUMN_NAME_BOOK, BooksStorage.COLUMN_NAME_CONTACT, BooksStorage.COLUMN_NAME_DUEDATE }, 
				new int[] { R.id.itemBookText, R.id.itemContactText, R.id.itemDuedateText }, 0) {
			@Override
			public void setViewText(TextView v, String text) {
				super.setViewText(v,  convText(v, text));;
			}
		};
		listView.setAdapter(adapter);


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
		int i = dialog_id;
		BooksProvider.delete(BooksStorage.BOOKS_TABLE_NAME, BooksStorage.COLUMN_NAME__ID + "=" + i, null );
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
		if (scanResult != null && scanResult.getContents() != null) {
			// handle scan result
			Intent newIntent = new Intent(this, AddBookActivity.class);
			newIntent.putExtra(Intent.EXTRA_TEXT, scanResult.getContents());
			startActivity(newIntent);			
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_view);

		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(this);

		BooksProvider = new BooksStorage(this.getApplicationContext());
		
	}

	@Override
	public void onResume() {
		super.onResume();

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

		
	}



}

