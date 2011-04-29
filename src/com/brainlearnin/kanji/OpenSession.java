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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class OpenSession extends ListActivity
{
	java.util.Hashtable<String, Element> sessionNames;
	int selectedItemIndex = 0;
	RenameSessionDialog renameSessionDialog = null;	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_session);
        
        reloadSessionList();
      
        Button openButton = (Button)findViewById(R.id.button_open_selected);
        openButton.setOnClickListener(openButtonClick);
        
        ListView listView = (ListView)findViewById(android.R.id.list);
        listView.setOnItemClickListener(listViewClickListener);
        
        renameSessionDialog = new RenameSessionDialog(this);
        
    }
          
    private View.OnClickListener openButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	ListView listView = (ListView)findViewById(android.R.id.list);
        	
        	if (listView.getCount() > 0) {        	
	        	final String selectedFromList = (String)(listView.getItemAtPosition(selectedItemIndex));
	        	String file = sessionNames.get(selectedFromList).getAttribute("file");
	        	
	        	SharedPreferences settings = getSharedPreferences("BrainLearninKanji", MODE_PRIVATE);
	            SharedPreferences.Editor editor = settings.edit();
	            
	            editor.putString("lastSessionName", selectedFromList);
	            editor.putString("lastSessionFile", file);
	            editor.commit();
	        	
	        	Intent openSessionIntent = new Intent(getBaseContext(), SessionOpened.class);
	        	startActivity(openSessionIntent);
        	} else {
        		AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.open_session_no_sessions_err), getString(R.string.OK), OpenSession.this);
        	}//if
        }
    };
    
    private void reloadSessionListHelper(java.io.InputStream inputStream, java.util.Hashtable<String, Element> sessionMap)
    {
    	Document doc = null;
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	doc = db.parse(inputStream);
        } catch (IOException ex) {
        
        } catch (SAXException e) {
			
		} catch (ParserConfigurationException e) {
			
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
				sessionMap.put(name, templateNode);
			}//if
		}//for
    }//reloadSessionListHelper
    
    private void reloadSessionList()
    {
    	ListView listView = (ListView)findViewById(android.R.id.list);
        sessionNames = new java.util.Hashtable<String, Element>();
        
        selectedItemIndex = 0;
                        
    	try {
    		FileInputStream userManifestStream = null;
			userManifestStream = openFileInput("user_session_manifest");
			reloadSessionListHelper(userManifestStream, sessionNames);
		} catch (FileNotFoundException e) {
			//Nothing
		}
		
		ArrayList<String> templateNamesStr = new ArrayList<String>();		
		 
		Enumeration<String> strEnum = Collections.enumeration(sessionNames.keySet());

		while(strEnum.hasMoreElements()) {
			templateNamesStr.add(strEnum.nextElement());
		}//while
		
		java.util.Collections.sort(templateNamesStr, String.CASE_INSENSITIVE_ORDER);

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, templateNamesStr));
        listView.setItemChecked(0, true);
        
        registerForContextMenu(getListView());
    }//reloadSessionList
    
    public void doRenameSession(String oldName, String newName)
    {
    	Element oldElement = sessionNames.get(oldName);
    	sessionNames.remove(oldName);
    	
    	oldElement.setAttribute("name", newName);
    	
    	sessionNames.put(newName, oldElement);
    	
    	rewriteSessionManifest();
    	reloadSessionList();
    }//doRenameSession
    
    private void doRenameSession()
    {    	
    	ListView listView = (ListView)findViewById(android.R.id.list);	        	
    	final String selectedFromList = (String)(listView.getItemAtPosition(selectedItemIndex));
        	
    	renameSessionDialog.setOldSessionName(selectedFromList, sessionNames);
    	renameSessionDialog.show();
    }//doRenameTemplate
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.open_session_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent aboutIntent = new Intent(getBaseContext(), About.class);
    	//aboutIntent.setClassName("com.brainlearnin", "com.brainlearnin.About");
    	
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.menu_rename_session:
	        	doRenameSession();
	            return true;
	            
	        case R.id.menu_delete_session:
	        	doDeleteSession();
	            return true;
	            
	        case R.id.menu_about:
	        	startActivity(aboutIntent);
	            return true;
	            
	        case R.id.menu_help:
            {                    
                aboutIntent.putExtra("titleRes", R.string.open_session_title);
                aboutIntent.putExtra("stringVal", R.string.open_session_help_text);
        
                startActivity(aboutIntent);
                return true;
            }
            
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.open_session_context_menu, menu);
    }//onCreateContextMenu
    
    @Override
    public boolean onContextItemSelected(MenuItem item) 
    {
    	//Intent aboutIntent = new Intent(getBaseContext(), About.class);
    	//aboutIntent.setClassName("com.brainlearnin", "com.brainlearnin.About");
    	
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	selectedItemIndex = menuInfo.position;
    	
    	ListView listView = (ListView)findViewById(android.R.id.list);
    	listView.setItemChecked(selectedItemIndex, true);
    	
    	switch (item.getItemId()) {
	        case R.id.menu_rename_session:	        	
	        	doRenameSession();
	            return true;
	            
	        case R.id.menu_delete_session:
	        	doDeleteSession();
	            return true;
	            
	        default:
	            return super.onContextItemSelected(item);
	    }//switch
    }//onContextItemSelected
    
    private void rewriteSessionManifest()
    {
    	boolean superBadError = false;
		
		java.io.FileOutputStream fos;
		try {
			fos = openFileOutput("user_session_manifest", android.content.Context.MODE_PRIVATE);
				
			fos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
			fos.write("<templates>\n".getBytes());
			
			//java.util.Hashtable<String, Element> sessionNames
			Iterator<Entry<String, Element>> iter = sessionNames.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Element> pairs = (Entry<String, Element>)iter.next();
				String name = pairs.getValue().getAttribute("name");
				String file = pairs.getValue().getAttribute("file");
				
				fos.write(("<template file=\"" + file + "\" name=\"" + name + "\"/>\n").getBytes());
			}//while			
			
			fos.write("</templates>\n".getBytes());
			fos.close();		
		} catch (FileNotFoundException e) {
			//What to do? We're boned...
			superBadError = true;
		} catch (IOException e) {
			//What to do? We're boned...
			superBadError = true;
		}		
		
		if (true == superBadError) {
			AlertHelpers.BasicAlert(getString(R.string.delete_session_err_title), getString(R.string.delete_session_err), getString(R.string.OK), this);
		}//if
    }//rewriteSessionManifest
    
    private ListView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
        	selectedItemIndex = myItemInt;            
          }
    };
    
    private void actuallyDeleteSession(String selectedFromList)
    {
    	Element oldElem = sessionNames.get(selectedFromList);
    	
    	String file = oldElem.getAttribute("file");
    	boolean ret = this.deleteFile(file);
    	
    	if (ret == false) {
    		AlertHelpers.BasicAlert(getString(R.string.delete_session_file_err_title), getString(R.string.delete_session_file_err), getString(R.string.OK), this);
    	}//if
    	
    	sessionNames.remove(selectedFromList);
		
		//Rewrite template
		rewriteSessionManifest();
    }//actuallyDeleteSession
    
    private void doDeleteSession()
    {
    	ListView listView = (ListView)findViewById(android.R.id.list);
    	
    	final String selectedFromList = (String)(listView.getItemAtPosition(selectedItemIndex));
    	
    	DialogInterface.OnClickListener okHandler = new DialogInterface.OnClickListener() {  	       
	 	        @Override
	 	        public void onClick(DialogInterface dialog, int which) {
	 	        	actuallyDeleteSession(selectedFromList);
	 	        	reloadSessionList();
	 	        }
	    	};
	    	
	    AlertHelpers.BasicOkCancel(getString(R.string.delete_session_conf_title), getString(R.string.delete_session_confirm) + " " + selectedFromList, this, 
	    							getString(R.string.OK), getString(R.string.Cancel), okHandler);
    }//doDeleteSession
    
}//OpenSession
