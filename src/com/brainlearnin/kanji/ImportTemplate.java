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
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;


public class ImportTemplate extends Activity {
	String selectedFile = "";
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_template);
  
        Button browseButton = (Button)findViewById(R.id.button_import_template_browse);
        browseButton.setOnClickListener(browseButtonClick);
        
        Button exportButton = (Button)findViewById(R.id.button_import_template_export_sample);
        exportButton.setOnClickListener(exportButtonClick);
        
        Button importButton = (Button)findViewById(R.id.button_import_template_import);
        importButton.setOnClickListener(importButtonClick);
        
    }//onCreate
    
    private boolean copyTemplate(String selectedFile, String templateName)
    {
    	FileInputStream userManifestStream = null;
    	try {
			userManifestStream = openFileInput("user_import_manifest");
		} catch (FileNotFoundException e) {
			try {
				java.io.FileOutputStream fos = openFileOutput("user_import_manifest", android.content.Context.MODE_PRIVATE);
				fos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
				fos.write("<templates>\n".getBytes());
				fos.write("</templates>\n".getBytes());
				fos.close();
				
				userManifestStream = openFileInput("user_import_manifest");
			} catch (FileNotFoundException e1) {
				
			} catch (IOException e2) {
				
			}
		}//try/catch
		
		if (userManifestStream == null) {
			AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.import_template_manifest_open_err), getString(R.string.OK), this);
			
			return false;
		}//if
		
		boolean manifestError = false;
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(userManifestStream);
		} catch (ParserConfigurationException e) {
			manifestError = true;
		} catch (SAXException e) {
			manifestError = true;
		} catch (IOException e) {
			manifestError = true;
		}   
		
		if (true == manifestError) {
			AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.import_template_manifest_open_err), getString(R.string.OK), this);			
			return false;
		}//if
    	
		//ensure no name collision in manifest
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
				if (name.equals(templateName) == true) {
					AlertHelpers.BasicAlert(getString(R.string.err_title), getString(R.string.import_template_manifest_duplicate_err), getString(R.string.OK), this);					
					return false;
				}
			}//if
		}//for
    	
    	//create guid
		java.util.UUID guid = java.util.UUID.randomUUID();
				
    	//copy file over to guid
		String outFile = guid.toString();
						
		try {	
			java.io.OutputStream outputStream = openFileOutput(outFile, android.content.Context.MODE_PRIVATE);		
			java.io.InputStream inputStream = new java.io.FileInputStream(selectedFile);
			
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
			java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(outputStream));
	        
	        String line = null;
	        while ((line=reader.readLine()) != null) {
	            writer.write(line);
	            writer.newLine();
	        }//while
	       
	        reader.close();
	        writer.close(); 
	        
	        outputStream.close();
	        inputStream.close();	        
		} catch (IOException e) {
			AlertHelpers.BasicAlert(getString(R.string.err_title), "[" + outFile + "]: " + getString(R.string.import_sample_template_template_err), getString(R.string.OK), this);			
			return false;
		}//try / catch
		
    	//map guid to name in manifest
		Element newTemplateEntry = doc.createElement("template");
		newTemplateEntry.setAttribute("file", outFile);
		newTemplateEntry.setAttribute("name", templateName);
		
		templatesNode.appendChild(newTemplateEntry);
		
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
		
		return true;
    }//copyTemplate
    
    private View.OnClickListener browseButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Intent importTemplateIntent = new Intent(getBaseContext(), LoadTemplateBrowser.class);
        	//importTemplateIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.LoadTemplateBrowser");
        	startActivityForResult(importTemplateIntent, 0);
        }
    };
    
    private View.OnClickListener exportButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Intent exportTemplateIntent = new Intent(getBaseContext(), SelectDirectoryBrowser.class);
        	//exportTemplateIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.SelectDirectoryBrowser");
        	startActivityForResult(exportTemplateIntent, 0);
        }
    };
    
    private View.OnClickListener importButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Document doc = null;
            try {            	
            	java.io.FileInputStream importStream = new java.io.FileInputStream(selectedFile);
            	
            	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            	DocumentBuilder db = dbf.newDocumentBuilder();
            	doc = db.parse(importStream);
            	
            	String templateName = "";
            	
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
        				
        				String templateNodeName = templateNode.getNodeName();
        				if (templateNodeName.equals("TemplateName") == true) {
        					templateName = ((Text)templateNode.getFirstChild()).getNodeValue();
        					break;
        				}//if
        			}//if
        		}//for
        		
        		importStream.close();        		
        		
        		if (templateName != "") {
        			boolean result = copyTemplate(selectedFile, templateName);
            	
        			if (result == true) {
        				setResult(0, null);
        				finish();
        			}//if
        			return;
        		}//if
        		     
        		AlertHelpers.BasicAlert(getString(R.string.err_title), "[" + selectedFile + "]: " + getString(R.string.import_template_template_no_title), getString(R.string.OK), ImportTemplate.this);
            } catch (IOException ex) {
            
            } catch (SAXException e) {
    			
    		} catch (ParserConfigurationException e) {
    			
    		}
    		
    		AlertHelpers.BasicAlert(getString(R.string.err_title), "[" + selectedFile + "]: " + getString(R.string.import_template_template_err), getString(R.string.OK), ImportTemplate.this);    		
        }
    };
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (intent == null) {
        	return;
        }//if
        
        Bundle extras = intent.getExtras();
        
        switch (resultCode) {
	        case 0:
		        if (extras != null) {
		        	selectedFile = extras.getString("file");
		        	
		        	TextView textView = (TextView)findViewById(R.id.text_import_template);
		        	textView.setText(selectedFile);		        			        	
		        }//if
		        break;
		        
	        case 1:
	        	if (extras != null) {
	        		String directory = extras.getString("directory");
	        		String outFile = directory + "/sample_kanji_template.xml";
	        		
	        		java.io.OutputStream sampleExportStream = null;
	        		
	        		try {	        			
	        			sampleExportStream = new java.io.FileOutputStream(outFile);
	        			java.io.InputStream templateManifestStream = getResources().openRawResource(R.raw.sample_template);
	        			
	        			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(templateManifestStream));
	        			java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(sampleExportStream));
	        	        
	        	        String line = null;
	        	        while ((line=reader.readLine()) != null) {
	        	            writer.write(line);
	        	            writer.newLine();
	        	        }//while
	        	       
	        	        reader.close();
	        	        writer.close(); 
	        	        
	        	        AlertHelpers.BasicAlert(getString(R.string.success), "[" + outFile + "]: " + getString(R.string.export_sample_template_template_success), getString(R.string.OK), this);
	        		} catch (IOException e) {
	        			sampleExportStream = null;
	        		}//try / catch
	        		
	        		if (sampleExportStream == null) {
	        			AlertHelpers.BasicAlert(getString(R.string.err_title), "[" + outFile + "]: " + getString(R.string.export_sample_template_template_err), getString(R.string.OK), this);
	        		}//if
	        		
		        }//if
	        	break;
	        	
	        case 2:
	        	//Cancel
	        	break;
        }//switch
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.front_door_menu, menu);
        return true;
    }//onCreateOptionsMenu
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.menu_about:
		        {
		        	Intent aboutIntent = new Intent(getBaseContext(), About.class);
		        	//aboutIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.About");
		        	startActivity(aboutIntent);
		        }
	            break;
	        case R.id.menu_global_preferences:
	        	{
	        		Intent preferencesIntent = new Intent();
	            	//preferencesIntent.setClassName("com.brainlearnin", "com.brainlearnin.GlobalPreferencesDialog");
	            	preferencesIntent = new Intent(getBaseContext(), GlobalPreferencesDialog.class);
	            	startActivity(preferencesIntent);
	        	}
	        	break;
	        	
	        case R.id.menu_help:
	        	{
	        		Intent aboutIntent = new Intent(getBaseContext(), About.class);
	        		aboutIntent.putExtra("titleRes", R.string.import_template_title);
	        		aboutIntent.putExtra("stringVal", R.string.import_template_help_text);
	        	
	        		startActivity(aboutIntent);
	        	}
	        	
	        default:
	            return super.onOptionsItemSelected(item);
        }
        
        return true;
    }//onOptionsItemSelected   

}

