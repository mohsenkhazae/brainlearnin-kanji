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
import android.widget.TextView;

public class RenameTemplateDialog extends Dialog implements OnClickListener 
{
	String oldTemplateName = "";
	NewSession context = null;
	
	public RenameTemplateDialog(NewSession context_) 
	{
		super(context_);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rename_template_dialog);
				
		context = context_;
		
		Button okButton = (Button) findViewById(R.id.rename_template_ok);		
		okButton.setOnClickListener(okButtonClick);
		
		Button cancelButton = (Button) findViewById(R.id.rename_template_cancel);		
		cancelButton.setOnClickListener(cancelButtonClick);		
	}//constructor
	
	public void setOldTemplateName(String oldTemplateName_)
	{
		oldTemplateName = oldTemplateName_;
		TextView oldNameLabel = (TextView)findViewById(R.id.rename_template_old_name_label);
		oldNameLabel.setText(oldTemplateName);
		
		EditText renameTemplateEntry = (EditText)findViewById(R.id.rename_template_entry);
		renameTemplateEntry.setText(oldTemplateName);
	}//setOldTemplateName
	
	private View.OnClickListener okButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	EditText renameTemplateEntry = (EditText)findViewById(R.id.rename_template_entry);    	
        	String newTemplateName = renameTemplateEntry.getText().toString();
        	
        	boolean res = context.doRenameTemplate(oldTemplateName, newTemplateName);
        	
        	if (res == true) {
        		dismiss();
        	}//if
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
		
}//RenameTemplateDialog
