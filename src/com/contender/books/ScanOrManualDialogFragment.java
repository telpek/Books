package com.contender.books;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class ScanOrManualDialogFragment extends DialogFragment {

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 * Each method passes the DialogFragment in case the host needs to query it. */
	public interface ScanOrManualDialogListener {
		public void onDialogScanClick(DialogFragment dialog);
		public void onDialogManualClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	ScanOrManualDialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			mListener = (ScanOrManualDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ScanOrManualDialogListener");
		}
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_scan_title);
		builder.setMessage(R.string.dialog_scan_or_manual);
		builder.setPositiveButton(R.string.dialog_scan, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogScanClick(ScanOrManualDialogFragment.this);
			}
		});
		builder.setNegativeButton(R.string.dialog_manual, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Delete entry
				mListener.onDialogManualClick(ScanOrManualDialogFragment.this);
			}
		});

		// Create the AlertDialog object and return it
		return builder.create();

	}
}