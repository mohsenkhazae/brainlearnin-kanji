/*
Brain Learnin' Kanji: It ain't learnin' if it ain't Brain Learnin'!
Written by Chris Mennie (chris at chrismennie.ca or cmennie at rogers.com)

License:

Brain Learnin' Kanji -- A kanji learning tool for the Android platform
Copyright (C) 2011 Chris A. Mennie								

Brain Learnin' Kanji is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Brain Learnin' Kanji is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Brain Learnin' Kanji.  If not, see http://www.gnu.org/licenses/.
*/

package com.brainlearnin.kanji;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertHelpers 
{

	static void BasicAlert(String title, String message, String okButtonStr, Context context)
	{
		new AlertDialog.Builder(context)
	    .setIcon(R.drawable.brain_icon)
	    .setTitle(title)
	    .setMessage(message)
	    .setPositiveButton(okButtonStr, 
	      new DialogInterface.OnClickListener() {
	       
	       @Override
	       public void onClick(DialogInterface dialog, int which) {
	        // TODO Auto-generated method stub
	       }
	      }).show();
	}
	
	static void BasicOkCancel(String title, String message, Context context, String okStr, String cancelStr, DialogInterface.OnClickListener okHandler)
	{
		new AlertDialog.Builder(context)
	    .setIcon(R.drawable.brain_icon)
	    .setTitle(title)
	    .setMessage(message)
		.setNegativeButton(cancelStr, null)
		.setPositiveButton(okStr, okHandler)
		.show();
	}
	
}//AlertHelpers
