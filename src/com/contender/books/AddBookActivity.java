package com.contender.books;



import java.io.IOException;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;



public class AddBookActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {

	public static Calendar dueDate;
	private String lIsbn, lAuthor, lContact, lTitle;


	private class BookResolver extends AsyncTask<String, Void, Integer> {

		private static final String APPLICATION_NAME = "Contender-Books/0.1";
		private static final String API_KEY = "AIzaSyBo4wt19r4dcaMd6zwnNtSzWZ8FYUHJ2wI";

		private JsonFactory json = JacksonFactory.getDefaultInstance();
		private Books book;

		@Override
		protected void onPreExecute() {

			
		}

		@Override
		protected Integer doInBackground(String... args) {

			if(args.length < 1) {
				cancel(true);
				return null;
			}

			lIsbn = new String(args[0]);
			book = new Books.Builder(AndroidHttp.newCompatibleTransport(), json, null)
			.setApplicationName(APPLICATION_NAME)
			.setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
			.build();

			List volumesList;
			try {
				volumesList = book.volumes().list("isbn:" + lIsbn);
			} catch (IOException e) {

				cancel(true);
				return null;
			}

			Volumes searchResult;
			try {
				searchResult = volumesList.execute();
			} catch (IOException e) {

				cancel(true);
				return null;
			}

			if (searchResult.getTotalItems() == 0 || searchResult.getItems() == null) {
				System.out.println("No matches found.");
				cancel(true);
				return null;
			}

			// Not sure if ISBN query can return > 1 results, will just use the first.

			for(Volume volume : searchResult.getItems()) {
				Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
				lTitle = new String(volumeInfo.getTitle());
				java.util.List<String> authors = volumeInfo.getAuthors();
				if (authors != null && !authors.isEmpty()) {
					lAuthor = new String(authors.get(0));

				} else 
					lAuthor = new String("Unknown");
				break;
			}

			return 0;
		}

		@Override
		protected void onCancelled(Integer result)	{
			Context context = getApplicationContext();
			CharSequence text = "Failed to retrieve book information online, please enter manually";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		@Override
		protected void onPostExecute(Integer result) {

			if(result == 0)
			{
				// doInBackground() completed succesfully
				TextView editTitle = (TextView) findViewById(R.id.editBookTitle);
				TextView editAuthor = (TextView) findViewById(R.id.editAuthor);
				editTitle.setText(lTitle);
				editAuthor.setText(lAuthor);
			}
		}
	}




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_book);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		// Set the date field to reflect the default loan duration (1 month)
		// TODO: Allow this duration to be defined in settings
		dueDate = Calendar.getInstance();
		dueDate.add(Calendar.MONTH, 1); // Should be a preference value
		TextView editDueDate = (TextView) findViewById(R.id.editDuedate);
		CharSequence format = DateFormat.format("MMM d, yyyy", dueDate);
		editDueDate.setText(format);

		// Check if Intent came with an ISBN number or none, proceed to
		// resolve book information if ISBN number was provided.
		Intent intent = getIntent();
		String message = intent.getStringExtra(Intent.EXTRA_TEXT);
		if(message.contains("none")) {
			// No ISBN number from Intent, user opted to enter manually
		} else if(!message.isEmpty()) {
			// Lookup ISBN code
			new BookResolver().execute(message);
		} else {
			// ????
		}

		// Set up autocompletion for the Contact field
		initContactAutocomplete();
	}

	@Override
	public void onPause() {
		super.onPause();


	}

	private void initContactAutocomplete() {

		String[] mProjection = 
			{
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
			};

		

		Cursor mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, mProjection, null, null, null);
		AutoCompleteTextView editContact = (AutoCompleteTextView) findViewById(R.id.editContact);
	
		int columnIndex = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
		String [] contacts = new String [mCursor.getCount()]; 
		
		while(mCursor.moveToNext()) {
			contacts[mCursor.getPosition()] = mCursor.getString(columnIndex);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, contacts);

		editContact.setAdapter(adapter);
		editContact.setThreshold(2);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_book, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void changeDate(View view) {
		// Open a dialog with a datepicker to change the value of dueDate
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");

	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		AddBookActivity.dueDate.set(Calendar.YEAR, year);
		AddBookActivity.dueDate.set(Calendar.MONTH, month);
		AddBookActivity.dueDate.set(Calendar.DAY_OF_MONTH, day);

		TextView editDueDate = (TextView) findViewById(R.id.editDuedate);
		CharSequence format = DateFormat.format("MMM d, yyyy", dueDate);
		editDueDate.setText(format);

	}


	public void submitBook(View view) {
		// Verify input and either submit to DB or implore user to re-enter

		// TODO: Verify input


		// Submit
		Calendar rightNow = Calendar.getInstance();

		ContentValues value = new ContentValues();
		EditText text = (EditText) findViewById(R.id.editBookTitle);
		value.put(BooksStorage.COLUMN_NAME_BOOK, text.getText().toString());
		text = (EditText) findViewById(R.id.editContact);
		value.put(BooksStorage.COLUMN_NAME_CONTACT, text.getText().toString());
		text = (EditText) findViewById(R.id.editAuthor);
		value.put(BooksStorage.COLUMN_NAME_AUTHOR, text.getText().toString());
		value.put(BooksStorage.COLUMN_NAME_LOANDATE, rightNow.getTimeInMillis());
		value.put(BooksStorage.COLUMN_NAME_DUEDATE, dueDate.getTimeInMillis());

		CheckBox reminder = (CheckBox) findViewById(R.id.checkBoxCalendar);
		value.put(BooksStorage.COLUMN_NAME_HASREMINDER, reminder.isChecked());

		MainView.BooksProvider.insert(BooksStorage.BOOKS_TABLE_NAME, value);
				
		finish();
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_add_book,
					container, false);
			return rootView;
		}
	}

}
