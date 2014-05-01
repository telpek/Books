package com.contender.books;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class DatePickerFragment extends DialogFragment
 {

	/*public interface OnDateSetListener {
		public void onDateSet(DialogFragment dialog, int year, int month, int day);
		
	}*/
	
	DatePickerDialog.OnDateSetListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			mListener = (DatePickerDialog.OnDateSetListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnDateSetListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		Calendar c = AddBookActivity.dueDate;
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), mListener, year, month, day);
	}

	/* public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
		// mListener.onDateSet(this, year, month, day);
		
		AddBookActivity.dueDate.set(Calendar.YEAR, year);
		AddBookActivity.dueDate.set(Calendar.MONTH, month);
		AddBookActivity.dueDate.set(Calendar.DAY_OF_MONTH, day);
		
	} */
}