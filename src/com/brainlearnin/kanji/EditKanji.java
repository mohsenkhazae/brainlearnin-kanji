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

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

public class EditKanji extends Activity {
	private KanjiQuiz.GlyphStat glyph = null;
	private boolean doingEdit;
	private EditText kanjiEdit = null;
	private EditText onEdit = null;
	private EditText kunEdit = null;
	private EditText meaningsEdit = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_kanji);
        
        kanjiEdit = (EditText)findViewById(R.id.edit_kanji_kanji_edit);
        onEdit = (EditText)findViewById(R.id.edit_kanji_on_edit);
        kunEdit = (EditText)findViewById(R.id.edit_kanji_kun_edit);
        meaningsEdit = (EditText)findViewById(R.id.edit_kanji_meanings_edit);
        
        Bundle bundle = this.getIntent().getExtras();         
        doingEdit = bundle.getBoolean("doingEdit");
        
        Button updateButton = (Button)findViewById(R.id.button_edit_kanji_update);
        updateButton.setOnClickListener(updateButtonClick);
        
        setTitleButtonText(doingEdit);
        
        if (doingEdit == true) {
	        int glyphIndex = bundle.getInt("kanji");
	        glyph = KanjiQuiz.currentQuiz.glyphs.get(glyphIndex);
	        
	        kanjiEdit.setText(glyph.glyph);
	        
	        String onReadings = glyph.onReadings.toString();
	        String kunReadings = glyph.kunReadings.toString();
	        String meanings = glyph.meanings.toString();
	        
	        onReadings = onReadings.substring(1, onReadings.length() - 1);
	        kunReadings = kunReadings.substring(1, kunReadings.length() - 1);
	        meanings = meanings.substring(1, meanings.length() - 1);
	        
	        onEdit.setText(onReadings);
	        kunEdit.setText(kunReadings);
	        meaningsEdit.setText(meanings);
        }//if
    }//onCreate
    
    private void setTitleButtonText(boolean doingEdit)
    {
    	Button updateButton = (Button)findViewById(R.id.button_edit_kanji_update);
    	
    	if (doingEdit == true) {
    		this.setTitle(R.string.editKanjiTitle);
    		updateButton.setText(R.string.button_edit_kanji_update);
    	} else {
    		this.setTitle(R.string.editNewKanjiTitle);
    		updateButton.setText(R.string.button_edit_kanji_add);
    	}//if
    }//setTitleButtonText
    
    private void commitChanges()
    {
    	if (glyph == null) {
    		return;
    	}//if
    	
    	String[] onReadings = onEdit.getText().toString().split(",");
    	String[] kunReadings = kunEdit.getText().toString().split(",");
    	String[] meanings = meaningsEdit.getText().toString().split(",");
    	
    	glyph.glyph = kanjiEdit.getText().toString();
    	
    	glyph.onReadings.clear();
    	glyph.kunReadings.clear();
    	glyph.meanings.clear();
    	
    	for (String str : onReadings) {
    		glyph.onReadings.add(str.trim());
    	}//for
    	
    	for (String str : kunReadings) {
    		glyph.kunReadings.add(str.trim());
    	}//for
    	
    	for (String str : meanings) {
    		glyph.meanings.add(str.trim());
    	}//for
    	
    	Intent resultIntent = new Intent();
    	setResult(Activity.RESULT_OK, resultIntent);
    }//commitChanges
    
    @Override
    public void onBackPressed() {    	
    	commitChanges(); 

        super.onBackPressed();
    }
    
    private View.OnClickListener updateButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	if ((glyph == null) && (kanjiEdit.getText().toString().length() > 0)) {
        		glyph = new KanjiQuiz.GlyphStat();
        		glyph.index = KanjiQuiz.currentQuiz.glyphs.size();
        		KanjiQuiz.currentQuiz.glyphs.put(glyph.index, glyph);
        	}//if
        	
        	commitChanges();
        	finish();
        }
    };//updateButtonClick
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.preferences_menu, menu);
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
	            
	        case R.id.menu_help:
	        {                        	
	        	Intent aboutIntent = new Intent(getBaseContext(), About.class);
	            aboutIntent.putExtra("titleRes", R.string.editKanjiTitle);
	            aboutIntent.putExtra("stringVal", R.string.editKanji_help_text);
	    
	            startActivity(aboutIntent);
	            return true;
	        }
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected    
}//EditKanji
