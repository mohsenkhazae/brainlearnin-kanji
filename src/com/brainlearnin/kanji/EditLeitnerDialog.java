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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class EditLeitnerDialog extends Dialog implements OnClickListener
{
	EditLeitner context;
	Button okButton;
	int origLevel;

	public EditLeitnerDialog(EditLeitner context_) 
	{
		super(context_);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_leitner_dialog);
				
		context = context_;
		
		okButton = (Button) findViewById(R.id.edit_leitner_ok);		
		okButton.setOnClickListener(okButtonClick);
		
		Button cancelButton = (Button) findViewById(R.id.edit_leitner_cancel);		
		cancelButton.setOnClickListener(cancelButtonClick);		
	}//constructor
	
	private View.OnClickListener okButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	EditText entry = (EditText)findViewById(R.id.edit_leitner_entry);
        	        	        
        	int newLevel = 0;
        	
        	try {
        		newLevel = Integer.parseInt(entry.getText().toString());
        		context.doChangeLevel(origLevel, newLevel);
        	} catch (NumberFormatException ex) {
        		//Nothing
        	}//try/catch
        	        	
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
	
	void setOldLevel(int origLevel_, int curValue)
	{
		origLevel = origLevel_;
		
		EditText entry = (EditText)findViewById(R.id.edit_leitner_entry);
		entry.setText(Integer.toString(curValue));
	}//setOldLevel

	
}//EditLeitnerDialog
