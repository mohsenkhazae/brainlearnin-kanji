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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class SelectDirectoryBrowser extends ListActivity 
{
	 
	 private List<String> item = null;
	 private List<String> path = null;
	 private String root="/";
	 private TextView myPath;
	 private String curDirectory = ""; 
	 
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.select_directory_browser);
	        
	        Button selectButton = (Button)findViewById(R.id.button_select_directory_select);
	        selectButton.setOnClickListener(selectButtonClick);
	        
	        Button cancelButton = (Button)findViewById(R.id.button_select_directory_cancel);
	        cancelButton.setOnClickListener(cancelButtonClick);
	        
	        myPath = (TextView)findViewById(R.id.select_directory_browse_path);
	        getDir(root);
	    }
	    
	    private View.OnClickListener selectButtonClick = new View.OnClickListener() {
	        public void onClick(View v) {
	        	Intent intent = new Intent();
	            intent.putExtra("directory", curDirectory);
	                      
	            setResult(1, intent);
	            finish();
	        }
	    };
	    
	    private View.OnClickListener cancelButtonClick = new View.OnClickListener() {
	        public void onClick(View v) {
	        	Intent intent = new Intent();	            
	                      
	            setResult(2, intent);
	            finish();
	        }
	    };
	    
	    private void getDir(String dirPath)
	    {
	    	curDirectory = dirPath;		     
		     myPath.setText(getString(R.string.location) + ": " + dirPath);
		     
		     item = new ArrayList<String>();
		     path = new ArrayList<String>();
		     
		     File f = new File(dirPath);
		     File[] files = f.listFiles();
		     
		     if(!dirPath.equals(root))
		     {
	
		      item.add(root);
		      path.add(root);
		      
		      item.add("../");
		      path.add(f.getParent());
		            
		     }
		     
		     for(int i=0; i < files.length; i++)
		     {
		       File file = files[i];
		       
		       if(file.isDirectory()) {
		    	   path.add(file.getPath());
		    	   item.add(file.getName() + "/");
		       }
		       //else
		       // item.add(file.getName());
		     }
	
		     ArrayAdapter<String> fileList =
		      new ArrayAdapter<String>(this, R.layout.file_browse_row, item);
		     setListAdapter(fileList);
	    }

	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {
	  
	  File file = new File(path.get(position));
	  
	  if (file.isDirectory())
	  {
	   if(file.canRead())
	    getDir(path.get(position));
	   else
	   {
		   AlertHelpers.BasicAlert(getString(R.string.err_title), "[" + file.getName() + "]: " + getString(R.string.load_template_browser_dir_err), getString(R.string.OK), this);
	   }
	  }
	  else
	  {
		  /*
		  Intent intent = new Intent();
          intent.putExtra("file", file.getName());
                    
          setResult(1, intent);
          finish();
          */
	  }
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
		        	//aboutIntent.setClassName("com.brainlearnin", "com.brainlearnin.About");
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
                aboutIntent.putExtra("titleRes", R.string.select_directory_browser_title);
                aboutIntent.putExtra("stringVal", R.string.select_directory_browser_help_text);
        
                startActivity(aboutIntent);
                break;
            }
	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected   
}
