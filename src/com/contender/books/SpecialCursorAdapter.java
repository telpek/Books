package com.contender.books;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;



public class SpecialCursorAdapter extends SimpleCursorAdapter {
	private int[] colors = new int[] { 0x99FFFFFF, 0x99eeeeee };
     
    public SpecialCursorAdapter (Context context, int layout, Cursor c, String[] from, int[] to, int flags)  {
        super(context, layout, c, from, to, flags);
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = super.getView(position, convertView, parent);
      int colorPos = position % colors.length;
      view.setBackgroundColor(colors[colorPos]);
      return view;
    }
}