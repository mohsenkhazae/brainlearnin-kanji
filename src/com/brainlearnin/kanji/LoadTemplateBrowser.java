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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class LoadTemplateBrowser extends ListActivity 
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
	        setContentView(R.layout.load_template_browser);
	        myPath = (TextView)findViewById(R.id.load_template_browse_path);
	        getDir(root);
	    }
	    
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
	       path.add(file.getPath());
	       if(file.isDirectory())
	        item.add(file.getName() + "/");
	       else
	        item.add(file.getName());
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
		  Intent intent = new Intent();
          intent.putExtra("file", curDirectory + "/" + file.getName());
                    
          setResult(0, intent);
          finish();
	  }
	 }
}
