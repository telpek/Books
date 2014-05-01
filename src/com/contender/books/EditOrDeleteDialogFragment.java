package com.contender.books;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class EditOrDeleteDialogFragment extends DialogFragment {

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 * Each method passes the DialogFragment in case the host needs to query it. */
	public interface EditOrDeleteDialogListener {
		public void onDialogEditClick(DialogFragment dialog);
		public void onDialogDeleteClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	EditOrDeleteDialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			mListener = (EditOrDeleteDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement EditOrDeleteDialogListener");
		}
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.dialog_edit_or_delete);
		builder.setPositiveButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogEditClick(EditOrDeleteDialogFragment.this);
			}
		});
		builder.setNegativeButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Delete entry
				mListener.onDialogDeleteClick(EditOrDeleteDialogFragment.this);
			}
		});

		// Create the AlertDialog object and return it
		return builder.create();

	}
}

