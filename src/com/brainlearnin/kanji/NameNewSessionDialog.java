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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class NameNewSessionDialog extends Dialog implements OnClickListener 
{
	String newSessionName = "";
	NewSession context = null;
	java.util.HashSet<String> sessionNames;
	EditText newSessionNameEntry = null;
	Button okButton = null;
	
	public NameNewSessionDialog(NewSession context_) 
	{
		super(context_);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.name_new_session_dialog);
				
		context = context_;
		
		okButton = (Button) findViewById(R.id.name_new_session_entry_ok);		
		okButton.setOnClickListener(okButtonClick);
		
		Button cancelButton = (Button) findViewById(R.id.name_new_session_cancel);		
		cancelButton.setOnClickListener(cancelButtonClick);
		
		sessionNames = new java.util.HashSet<String>();
		
		newSessionNameEntry = (EditText)findViewById(R.id.name_new_session_entry);
		newSessionNameEntry.addTextChangedListener(newSessionNameEntryTextChangedListener);
	}//constructor
	
	private void loadSessionList()
	{
		sessionNames = new java.util.HashSet<String>();
		
		FileInputStream userManifestStream = null;
						
		try {			
			userManifestStream = context.openFileInput("user_session_manifest");
			
			/*
			StringBuffer strContent = new StringBuffer("");
			int ch;
			
			try {
				while( (ch = userManifestStream.read()) != -1) {
					strContent.append((char)ch);
				}
			} catch (IOException e) {

			}
			
			String str = strContent.toString();
			
			int a;
			a = 5;
			*/
						
		} catch (FileNotFoundException e1) {
			return;
		}
		
    	Document doc = null;
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	doc = db.parse(userManifestStream);
        } catch (IOException ex) {
        	return;
        } catch (SAXException e) {
			return;
		} catch (ParserConfigurationException e) {
			return;
		}
			
		NodeList topLevelNodeList = doc.getChildNodes();
		Element templatesNode = null;
		for (int index = 0; index < topLevelNodeList.getLength(); ++index) {
			Node childNodeBase = topLevelNodeList.item(index);
			if (childNodeBase instanceof Element) {
				templatesNode = (Element)childNodeBase;
				break;
			}//if
		}//for
		
		//Element templatesNode = (Element)doc.getFirstChild();
		NodeList templateNodeList = templatesNode.getChildNodes();
		for (int index = 0; index < templateNodeList.getLength(); ++index) {
			Node childNodeBase = templateNodeList.item(index);
			if (childNodeBase instanceof Element) {
				Element templateNode = (Element)templateNodeList.item(index);
			
				String name = templateNode.getAttribute("name");
				sessionNames.add(name);
			}//if
		}//for
		
		//Rewrite template
		try {
			userManifestStream.close();
		} catch (IOException e) {
			return;
		}
		
	}//loadSessionList
	
	private void verifySessionName()
	{
		newSessionName = newSessionNameEntry.getText().toString();
		
		if (sessionNames.contains(newSessionName) == true) {
			okButton.setEnabled(false);
		} else {
			okButton.setEnabled(true);
		}//if
	}//verifySessionName
	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event)  {
		verifySessionName();

        return super.onKeyUp(keyCode, event);
    }
	
	public void setNewSessionName(String newSessionName_)
	{
		newSessionName = newSessionName_;
				
		newSessionNameEntry.setText(newSessionName);
		
		loadSessionList();
		verifySessionName();
	}//setNewSessionName
	
	private View.OnClickListener okButtonClick = new View.OnClickListener() {
        public void onClick(View v) {        	
        	context.invokeSessionOpened(newSessionName);
        	dismiss();
        }
    };
    
    private View.OnClickListener cancelButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	dismiss();
        }
    };
    
    
    
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

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
	
}//NameNewSessionDialog
