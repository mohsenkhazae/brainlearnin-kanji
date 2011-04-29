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
import java.lang.reflect.Field;
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
import android.os.Bundle;
import android.widget.Button;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class NewSession extends ListActivity {	
	java.util.Hashtable<String, Element> templateNames;
	java.util.Hashtable<String, Element> templateNamesUser;
	int selectedItemIndex = 0;
	RenameTemplateDialog renameTemplateDialog = null;
	NameNewSessionDialog nameNewSessionDialog = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_session);
        
        reloadTemplateList();
    
        Button importButton = (Button)findViewById(R.id.button_import);
        importButton.setOnClickListener(importButtonClick);
        
        Button openButton = (Button)findViewById(R.id.button_open_selected);
        openButton.setOnClickListener(openButtonClick);
        
        ListView listView = (ListView)findViewById(android.R.id.list);
        listView.setOnItemClickListener(listViewClickListener);
        
        renameTemplateDialog = new RenameTemplateDialog(this);
        nameNewSessionDialog = new NameNewSessionDialog(this); 
    }
    
    private void actuallyDeleteTemplate(String selectedFromList)
    {
    	FileInputStream userManifestStream = null;
		try {
			userManifestStream = openFileInput("user_import_manifest");
		} catch (FileNotFoundException e1) {
			return;
		}
		
    	Document doc = null;
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	doc = db.parse(userManifestStream);
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
				if (name.equals(selectedFromList) == true) {
					String file = templateNode.getAttribute("file");
					
					templatesNode.removeChild(templateNode);
					deleteFile(file);
					
					break;
				}//if
			}//if
		}//for
		
		//Rewrite template
		try {
			userManifestStream.close();
		} catch (IOException e) {
			return;
		}
		
		boolean superBadError = false;
		
		java.io.FileOutputStream fos;
		try {
			fos = openFileOutput("user_import_manifest", android.content.Context.MODE_PRIVATE);
				
			fos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
			fos.write("<templates>\n".getBytes());
			
			templateNodeList = templatesNode.getChildNodes();
			for (int index = 0; index < templateNodeList.getLength(); ++index) {
				Node childNodeBase = templateNodeList.item(index);
				if (childNodeBase instanceof Element) {
					Element templateNode = (Element)templateNodeList.item(index);
				
					String name = templateNode.getAttribute("name");
					String file = templateNode.getAttribute("file");
					
					fos.write(("<template file=\"" + file + "\" name=\"" + name + "\"/>\n").getBytes());
				}//if
			}//for
			
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
			AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.import_sample_template_manifest_write_err), getString(R.string.OK), this);
		}//if
    }//actuallyDeleteTemplate
    
    private void doRenameTemplate()
    {    	
    	ListView listView = (ListView)findViewById(android.R.id.list);	        	
    	final String selectedFromList = (String)(listView.getItemAtPosition(selectedItemIndex));
    	
    	if (templateNames.containsKey(selectedFromList) == true) {
    		AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.rename_template_cant_rename), getString(R.string.OK), this);
			return;
    	}//if
        	
    	renameTemplateDialog.setOldTemplateName(selectedFromList);
    	renameTemplateDialog.show();
    }//doRenameTemplate
    
    public boolean doRenameTemplate(String oldTemplateName, String newTemplateName)
    {
    	FileInputStream userManifestStream = null;
		try {
			userManifestStream = openFileInput("user_import_manifest");
		} catch (FileNotFoundException e1) {
			return false;
		}
		
    	Document doc = null;
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	doc = db.parse(userManifestStream);
        } catch (IOException ex) {
        
        } catch (SAXException e) {
			
		} catch (ParserConfigurationException e) {
			
		}
		
		Element templateNodeToChange = null;
		boolean hasDuplicateTemplate = false;
		
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
				if (name.equals(oldTemplateName) == true) {
					templateNodeToChange = templateNode;
				}//if
				
				if (name.equals(newTemplateName) == true) {
					hasDuplicateTemplate = true;
				}//if
			}//if
		}//for

		//Rewrite template
		try {
			userManifestStream.close();
		} catch (IOException e) {
			return false;
		}
		
		if (hasDuplicateTemplate == true) {
			AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.rename_template_duplicate_err), getString(R.string.OK), this);						
			return false;
		}//if
		
		templateNodeToChange.removeAttribute("name");
		templateNodeToChange.setAttribute("name", newTemplateName);
		
		boolean superBadError = false;
		
		java.io.FileOutputStream fos;
		try {
			fos = openFileOutput("user_import_manifest", android.content.Context.MODE_PRIVATE);
				
			fos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
			fos.write("<templates>\n".getBytes());
			
			templateNodeList = templatesNode.getChildNodes();
			for (int index = 0; index < templateNodeList.getLength(); ++index) {
				Node childNodeBase = templateNodeList.item(index);
				if (childNodeBase instanceof Element) {
					Element templateNode = (Element)templateNodeList.item(index);
				
					String name = templateNode.getAttribute("name");
					String file = templateNode.getAttribute("file");
					
					fos.write(("<template file=\"" + file + "\" name=\"" + name + "\"/>\n").getBytes());
				}//if
			}//for
			
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
			AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.import_sample_template_manifest_write_err), getString(R.string.OK), this);
			return false;
		}//if
		
		reloadTemplateList();
		return true;
    }//doRenameTemplate
    
    private void doDeleteTemplate()
    {
    	ListView listView = (ListView)findViewById(android.R.id.list);
    	
    	final String selectedFromList = (String)(listView.getItemAtPosition(selectedItemIndex));
    	
    	if (templateNamesUser.containsKey(selectedFromList) == false) {
    		AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.delete_template_missing), getString(R.string.OK), this);
    		return;
    	}//if
    	
    	DialogInterface.OnClickListener okHandler = new DialogInterface.OnClickListener() {  	       
 	        @Override
 	        public void onClick(DialogInterface dialog, int which) {
 	        	actuallyDeleteTemplate(selectedFromList);
 	        	reloadTemplateList();
 	        }
    	};
    	
    	AlertHelpers.BasicOkCancel(getString(R.string.delete_template_conf_title), getString(R.string.delete_template_confirm) + " " + selectedFromList, this, 
    								getString(R.string.OK), getString(R.string.Cancel), okHandler);
    }//doDeleteTemplate
    
    private ListView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
        	selectedItemIndex = myItemInt;            
          }
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.import_template_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent aboutIntent = new Intent(getBaseContext(), About.class);
    	//aboutIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.About");
    	
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.menu_rename_template:
	        	doRenameTemplate();
	            return true;
	            
	        case R.id.menu_delete_template:
	        	doDeleteTemplate();
	            return true;
	            
	        case R.id.menu_about:
	        	startActivity(aboutIntent);
	            return true;
	            
	        case R.id.menu_help:
	        	aboutIntent.putExtra("titleRes", R.string.new_session_title);
	        	aboutIntent.putExtra("stringVal", R.string.new_session_help_text);
	        	
	        	startActivity(aboutIntent);
	        	return true;	            
	        	
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.import_template_context_menu, menu);
    }//onCreateContextMenu
    
    @Override
    public boolean onContextItemSelected(MenuItem item) 
    {
    	//Intent aboutIntent = new Intent(getBaseContext(), About.class);
    	//aboutIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.About");
    	
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	selectedItemIndex = menuInfo.position;
    	
    	ListView listView = (ListView)findViewById(android.R.id.list);
    	listView.setItemChecked(selectedItemIndex, true);
    	
    	switch (item.getItemId()) {
	        case R.id.menu_rename_template:
	        	doRenameTemplate();
	            return true;
	            
	        case R.id.menu_delete_template:
	        	doDeleteTemplate();
	            return true;
	            
	        default:
	            return super.onContextItemSelected(item);
	    }
    }//onContextItemSelected
    
    private View.OnClickListener importButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Intent importTemplateIntent = new Intent(getBaseContext(), ImportTemplate.class);
        	//importTemplateIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.ImportTemplate");
        	startActivityForResult(importTemplateIntent, 0);
        }
    };
    
    private View.OnClickListener openButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	ListView listView = (ListView)findViewById(android.R.id.list);	        	
        	final String selectedFromList = (String)(listView.getItemAtPosition(selectedItemIndex));
            	
        	nameNewSessionDialog.setNewSessionName(selectedFromList);
        	nameNewSessionDialog.show();  
        }
    };
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        
        reloadTemplateList();
    }//onActivityResult
    
    private void reloadTemplateListHelper(java.io.InputStream inputStream, java.util.Hashtable<String, Element> templateMap)
    {
    	Document doc = null;
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	doc = db.parse(inputStream);
        } catch (IOException ex) {
        	//int a;
        	//a = 5;
        } catch (SAXException e) {
        	//int a;
        	//a = 5;
		} catch (ParserConfigurationException e) {
			//int a;
        	//a = 5;
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
				templateMap.put(name, templateNode);
			}//if
		}//for
    }//reloadTemplateListHelper
    
    private void reloadTemplateList()
    {
    	ListView listView = (ListView)findViewById(android.R.id.list);
        templateNames = new java.util.Hashtable<String, Element>();
        templateNamesUser = new java.util.Hashtable<String, Element>();
        selectedItemIndex = 0;
        
        java.io.InputStream templateManifestStream = getResources().openRawResource(R.raw.built_in_manifest);
        reloadTemplateListHelper(templateManifestStream, templateNames);
                
    	try {
    		FileInputStream userManifestStream = null;
			userManifestStream = openFileInput("user_import_manifest");
			reloadTemplateListHelper(userManifestStream, templateNamesUser);
		} catch (FileNotFoundException e) {
			//Nothing
		}
		
		ArrayList<String> templateNamesStr = new ArrayList<String>();
		
		Enumeration<String> strEnum = Collections.enumeration(templateNames.keySet());

		 while(strEnum.hasMoreElements()) {
			 templateNamesStr.add(strEnum.nextElement());
		 }//while
		 
		 strEnum = Collections.enumeration(templateNamesUser.keySet());

		 while(strEnum.hasMoreElements()) {
			 templateNamesStr.add(strEnum.nextElement());
		 }//while
		
		java.util.Collections.sort(templateNamesStr, String.CASE_INSENSITIVE_ORDER);

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, templateNamesStr));
        listView.setItemChecked(0, true);
        
        registerForContextMenu(getListView());
    }//reloadTemplateList
    
    public void invokeSessionOpened(String newSessionName)
    {
		FileInputStream userManifestStream = null;
		try {
			userManifestStream = openFileInput("user_session_manifest");
		} catch (FileNotFoundException e1) {
			java.io.FileOutputStream fos;
			try {
				fos = openFileOutput("user_session_manifest", android.content.Context.MODE_PRIVATE);
				fos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
				fos.write("<sessions>\n".getBytes());
				fos.write("</sessions>\n".getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				
			} catch (IOException e) {
				
			}			
			fos = null;
		}		
		
		try {
			userManifestStream = openFileInput("user_session_manifest");
		} catch (FileNotFoundException e1) {
			return;
		}
		
    	Document doc = null;
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	doc = db.parse(userManifestStream);
        } catch (IOException ex) {
        
        } catch (SAXException e) {
			
		} catch (ParserConfigurationException e) {
			
		}
				
		//Element templatesNode = (Element)doc.getFirstChild();
		NodeList topLevelNodeList = doc.getChildNodes();
		Element templatesNode = null;
		for (int index = 0; index < topLevelNodeList.getLength(); ++index) {
			Node childNodeBase = topLevelNodeList.item(index);
			if (childNodeBase instanceof Element) {
				templatesNode = (Element)childNodeBase;
				break;
			}//if
		}//for
		
		NodeList templateNodeList = templatesNode.getChildNodes();
		/*
		for (int index = 0; index < templateNodeList.getLength(); ++index) {
			Node childNodeBase = templateNodeList.item(index);
			if (childNodeBase instanceof Element) {
				Element templateNode = (Element)templateNodeList.item(index);
			}//if
		}//for
		*/
		
		//Rewrite template
		try {
			userManifestStream.close();
		} catch (IOException e) {
			return;
		}
		
		
		boolean superBadError = false;
		
		java.io.FileOutputStream fos;
		String newFile = "ERROR";
		try {
			newFile = createNewSessionFile();
			
			fos = openFileOutput("user_session_manifest", android.content.Context.MODE_PRIVATE);
				
			fos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
			fos.write("<sessions>\n".getBytes());
			
			templateNodeList = templatesNode.getChildNodes();
			for (int index = 0; index < templateNodeList.getLength(); ++index) {
				Node childNodeBase = templateNodeList.item(index);
				if (childNodeBase instanceof Element) {
					Element templateNode = (Element)templateNodeList.item(index);
				
					String name = templateNode.getAttribute("name");
					String filename = templateNode.getAttribute("file");
					
					fos.write(("<session file=\"" + filename + "\" name=\"" + name + "\"/>\n").getBytes());
				}//if
			}//for
			
			if (newFile.length() > 0) {			
				fos.write(("<session file=\"" + newFile + "\" name=\"" + newSessionName + "\"/>\n").getBytes());
			} else {
				superBadError = true;
			}//if
			
			fos.write("</sessions>\n".getBytes());
			fos.close();		
		} catch (FileNotFoundException e) {
			//What to do? We're boned...
			superBadError = true;
		} catch (IOException e) {
			//What to do? We're boned...
			superBadError = true;
		}		
		
		if (true == superBadError) {
			AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.import_sample_template_manifest_write_err), getString(R.string.OK), this);			
			return;
		} else {
			SharedPreferences settings = getSharedPreferences("BrainLearninKanji", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            
            editor.putString("lastSessionName", newSessionName);
            editor.putString("lastSessionFile", newFile);
            editor.commit();
        	
        	Intent openSessionIntent = new Intent(getBaseContext(), SessionOpened.class);
        	startActivity(openSessionIntent);
		}//if
    }//invokeSessionOpened
    
    Document openBuiltInSessionDocument(String selectedFromList)
    {
    	if (templateNames.containsKey(selectedFromList) == false) {
    		return null;
    	}//if
    	
    	String file = templateNames.get(selectedFromList).getAttribute("file");
    	Field fileIdField;
		try {
			fileIdField = R.raw.class.getField(file);
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchFieldException e) {
			return null;
		}
    	
    	int builtInFileId;
		try {
			builtInFileId = fileIdField.getInt(R.raw.class);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
		
    	java.io.InputStream inStream = getResources().openRawResource(builtInFileId);
    	
    	Document doc = null;
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	doc = db.parse(inStream);
        } catch (IOException ex) {
        	return null;
        } catch (SAXException e) {
        	return null;
		} catch (ParserConfigurationException e) {
			return null;
		}
    	
		return doc;
    }//openBuiltInSessionDocument
    
    Document openUserSessionDocument(String selectedFromList)
    {
    	if (templateNamesUser.containsKey(selectedFromList) == false) {
    		return null;
    	}//if
    	
    	String file = templateNamesUser.get(selectedFromList).getAttribute("file");    	
		
    	FileInputStream inStream = null;
		try {
			inStream = openFileInput(file);
		} catch (FileNotFoundException e1) {
			return null;
		}
    	
    	Document doc = null;
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	doc = db.parse(inStream);
        } catch (IOException ex) {
        	return null;
        } catch (SAXException e) {
        	return null;
		} catch (ParserConfigurationException e) {
			return null;
		}
    	
		return doc;
    }//openUserSessionDocument
    
    private String createNewSessionFile()
    {
    	//create guid
		java.util.UUID guid = java.util.UUID.randomUUID();
				
    	//copy file over to guid
		String outFile = guid.toString();
		
		ListView listView = (ListView)findViewById(android.R.id.list);    	
    	final String selectedFromList = (String)(listView.getItemAtPosition(selectedItemIndex));
		
    	Document doc = openBuiltInSessionDocument(selectedFromList);
    	
    	if (doc == null) {
    		doc = openUserSessionDocument(selectedFromList);
    	}//if
    	
    	if (doc == null) {
    		AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.import_sample_template_manifest_write_err), getString(R.string.OK), this);
			return "";
    	}//if
    	
    	KanjiQuiz kanjiQuiz = new KanjiQuiz();
    	kanjiQuiz.quizPreferences = Preferences.getGlobalInstance(this);
    	
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
				if (templateNode.getNodeName().equals("GlyphStats")) {
					kanjiQuiz.importGlyphs(templateNode);
					break;
				}//if
			}//if
		}//for
		
		Iterator<Entry<Integer, KanjiQuiz.GlyphStat>> iter = kanjiQuiz.glyphs.entrySet().iterator();
        while (iter.hasNext()) {
                Entry<Integer, KanjiQuiz.GlyphStat> entry = iter.next();
                kanjiQuiz.quizPreferences.testSet.add(entry.getValue().glyph);
                kanjiQuiz.quizPreferences.flashcardSet.add(entry.getValue().glyph);
        }//while
		
		boolean ret = kanjiQuiz.exportQuiz(outFile, this);
		
		if (ret == false) {
			return "";
		}//if
    	    	
		return outFile;
    }//createNewSessionFile

}

