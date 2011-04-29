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

import org.w3c.dom.Element;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RenameSessionDialog extends Dialog implements OnClickListener 
{
	String oldSessionName = "";
	String origSessionName = "";
	OpenSession context = null;
	EditText newSessionNameEntry = null;
	Button okButton = null;	
	java.util.Hashtable<String, Element> sessionNames;
	
	public RenameSessionDialog(OpenSession context_) 
	{
		super(context_);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rename_session_dialog);
				
		context = context_;
		
		okButton = (Button) findViewById(R.id.rename_session_ok);		
		okButton.setOnClickListener(okButtonClick);
		
		Button cancelButton = (Button) findViewById(R.id.rename_session_cancel);		
		cancelButton.setOnClickListener(cancelButtonClick);		
		
		newSessionNameEntry = (EditText)findViewById(R.id.rename_session_entry);
		newSessionNameEntry.addTextChangedListener(newSessionNameEntryTextChangedListener);
	}//constructor
	
	private void verifySessionName()
	{
		oldSessionName = newSessionNameEntry.getText().toString();
		
		if (sessionNames.containsKey(oldSessionName) == true) {
			okButton.setEnabled(false);
		} else {
			okButton.setEnabled(true);
		}//if
	}//verifySessionName
	
	private TextWatcher newSessionNameEntryTextChangedListener = new TextWatcher() {
    	@Override
    	public void onTextChanged(CharSequence s, int start, int before, int count) 
    	{
    		verifySessionName();
    	}
    	
		@Override
		public void afterTextChanged(Editable arg0) 
		{
			verifySessionName();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) 
		{
			verifySessionName();
		}
    };
	
	public void setOldSessionName(String oldSessionName_, java.util.Hashtable<String, Element> sessionNames_)
	{
		sessionNames = sessionNames_;
		oldSessionName = oldSessionName_;
		origSessionName = oldSessionName;
		TextView oldNameLabel = (TextView)findViewById(R.id.rename_session_old_name_label);
		oldNameLabel.setText(oldSessionName);
				
		newSessionNameEntry.setText(oldSessionName);
		
		verifySessionName();
	}//setOldSessionName
	
	private View.OnClickListener okButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	EditText renameSessionEntry = (EditText)findViewById(R.id.rename_session_entry);    	
        	String newSessionName = renameSessionEntry.getText().toString();
        	
        	context.doRenameSession(origSessionName, newSessionName);
       		dismiss();
        }
    };
    
    private View.OnClickListener cancelButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	dismiss();
        }
    };

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
		
}//RenameSessionDialog
