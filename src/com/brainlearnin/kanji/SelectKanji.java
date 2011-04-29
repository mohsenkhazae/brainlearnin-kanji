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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.brainlearnin.kanji.KanjiQuiz.GlyphStat;

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
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SelectKanji extends ListActivity 
{
	final static int SelectionMode_Test = 1;
	final static int SelectionMode_Flashcard = 2;
	
	android.widget.ListView tableLayout = null;
	int selectionMode;
	TreeSet<String> selected;
	int curOffset;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_kanji);
        
        curOffset = 0;
        
        tableLayout = this.getListView();        
        
        Bundle bundle = this.getIntent().getExtras(); 
        selectionMode = bundle.getInt("selectionMode");
        
        if (selectionMode == SelectionMode_Flashcard) {        	
        	selected = KanjiQuiz.currentQuiz.quizPreferences.flashcardSet;
        } else {        	
        	selected = KanjiQuiz.currentQuiz.quizPreferences.testSet;
        }//if
        
        doFillGrid();
        
        Button clearButton = (Button)findViewById(R.id.select_kanji_clear_button);
        clearButton.setOnClickListener(clearButtonClick);
        
        Button allButton = (Button)findViewById(R.id.select_kanji_all_button);
        allButton.setOnClickListener(allButtonClick);
        
        Button prevButton = (Button)findViewById(R.id.select_kanji_prev_button);
        prevButton.setOnClickListener(prevButtonClick);
        
        Button nextButton = (Button)findViewById(R.id.select_kanji_next_button);
        nextButton.setOnClickListener(nextButtonClick);
    }//onCreate
    
    private View.OnClickListener prevButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	curOffset -= 20;
        	if (curOffset < 0) {
        		curOffset = 0;
        	}//if
        	
        	doFillGrid();
        }
    };//prevButtonClick
    
    private View.OnClickListener nextButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	curOffset += 20;
        	if (curOffset >= KanjiQuiz.currentQuiz.glyphs.size()) {
        		curOffset = KanjiQuiz.currentQuiz.glyphs.size() - 20;
        	}//if
        	
        	if (curOffset < 0) {
        		curOffset = 0;
        	}//if
        	
        	doFillGrid();
        }
    };//nextButtonClick
    
    private View.OnClickListener clearButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	selected.clear();
        	
        	doFillGrid();
        }
    };//clearButtonClick
    
    private View.OnClickListener allButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	selected.clear();
        	
        	Iterator<Entry<Integer, KanjiQuiz.GlyphStat>> iter = KanjiQuiz.currentQuiz.glyphs.entrySet().iterator();
        	while (iter.hasNext()) {
        		Entry<Integer, KanjiQuiz.GlyphStat> entry = iter.next();
        		
        		selected.add(entry.getValue().glyph);
        	}//while
        	
        	doFillGrid();
        }
    };//allButtonClick
    
    private void doFillGrid()
    {
    	if (selectionMode == SelectionMode_Flashcard) {
        	this.setTitle(getString(R.string.select_kanji_title_base) + " " + getString(R.string.select_kanji_title_flashcard)); 
        	fillGrid(KanjiQuiz.currentQuiz.quizPreferences.flashcardSet, curOffset);
        } else {
        	this.setTitle(getString(R.string.select_kanji_title_base) + " " + getString(R.string.select_kanji_title_test));
        	fillGrid(KanjiQuiz.currentQuiz.quizPreferences.testSet, curOffset);
        }//if
    	
    	registerForContextMenu(getListView());
    }//doFillGrid
    
    private void fillGrid(TreeSet<String> selectedKanjiSet, int offset)
    {
    	int endKanji = offset + 20;
    	
    	if (KanjiQuiz.currentQuiz.glyphs.size() >= 20) {
	    	if (endKanji >= KanjiQuiz.currentQuiz.glyphs.size()) {
	    		offset = KanjiQuiz.currentQuiz.glyphs.size() - 20;
	    		curOffset = offset;
	    		endKanji = KanjiQuiz.currentQuiz.glyphs.size();
	    	}//if
    	} else {
    		endKanji = KanjiQuiz.currentQuiz.glyphs.size();
    	}//if
    	  	
    	ArrayList<Integer> kanjiList = new ArrayList<Integer>();
    	
    	for (int kanji = offset; kanji < endKanji; ++kanji) {
    		KanjiQuiz.GlyphStat glyph = KanjiQuiz.currentQuiz.glyphs.get(kanji);
    		
    		kanjiList.add(glyph.index);
    	}//for
    	
    	tableLayout.setCacheColorHint(0);

  	    SelectKanjiListAdapter adapter = new SelectKanjiListAdapter(this, R.layout.select_kanji_item, kanjiList, selectedKanjiSet, false);
    	tableLayout.setAdapter(adapter);
    	tableLayout.setItemsCanFocus(false);
    	tableLayout.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }//fillGrid
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_kanji_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent aboutIntent = new Intent(getBaseContext(), About.class);
    	//aboutIntent.setClassName("com.brainlearnin", "com.brainlearnin.About");
    	
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_about:
        	startActivity(aboutIntent);
            return true;
            
        case R.id.menu_help:
        {                        	
            aboutIntent.putExtra("titleRes", R.string.select_kanji_base_title);
            aboutIntent.putExtra("stringVal", R.string.select_kanji_help_text);
    
            startActivity(aboutIntent);
            return true;
        }
        	
        case R.id.menu_add_kanji:
        {
        	Intent addKanjiIntent = new Intent(getBaseContext(), EditKanji.class);	        	
        	addKanjiIntent.putExtra("doingEdit", false);
        	startActivityForResult(addKanjiIntent, 0);
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
    	inflater.inflate(R.menu.select_kanji_context_menu, menu);
    }//onCreateContextMenu
    
    @Override
    public boolean onContextItemSelected(MenuItem item) 
    {    	
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
        int selectedItemIndex = menuInfo.position;
        
        ListView listView = (ListView)findViewById(android.R.id.list);
        listView.setItemChecked(selectedItemIndex, true);
    	
    	Intent aboutIntent = new Intent(getBaseContext(), About.class);
    	//aboutIntent.setClassName("com.brainlearnin", "com.brainlearnin.About");
    	
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.menu_about:
	        	startActivity(aboutIntent);
	            return true;
	        case R.id.menu_edit_selected_kanji:
	        {
	        	//AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	        	//int itemIndex = curOffset + info.position;
	        	int itemIndex = curOffset + selectedItemIndex;
	        	
	        	Intent editKanjiIntent = new Intent(getBaseContext(), EditKanji.class);	        	
	        	editKanjiIntent.putExtra("kanji", itemIndex);
	        	editKanjiIntent.putExtra("doingEdit", true);
	        	startActivityForResult(editKanjiIntent, 0);
	        }	
	        	return true;
	        	
	        case R.id.menu_delete_selected_kanji:
	        {
	        	int itemIndex = curOffset + selectedItemIndex;
	        		        	
	        	KanjiQuiz.GlyphStat glyph = KanjiQuiz.currentQuiz.glyphs.get(itemIndex);
	        	doDeleteGlyph(glyph);
	        }
	        	return true;
	        	
	        case R.id.menu_add_kanji:
	        {
	        	Intent editKanjiIntent = new Intent(getBaseContext(), EditKanji.class);	        	
	        	editKanjiIntent.putExtra("doingEdit", false);
	        	startActivityForResult(editKanjiIntent, 0);
	        }	
	        	return true;
	        	
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }//onContextItemSelected
    
    private void actuallyDeleteGlyph(KanjiQuiz.GlyphStat glyph)
    {
    	ArrayList<KanjiQuiz.GlyphStat> glyphs = new ArrayList<KanjiQuiz.GlyphStat>();
    	
    	Iterator<Entry<Integer, KanjiQuiz.GlyphStat>> iter = KanjiQuiz.currentQuiz.glyphs.entrySet().iterator();
        while (iter.hasNext()) {
                Entry<Integer, KanjiQuiz.GlyphStat> entry = iter.next();
                
                if (entry.getValue() != glyph) {
                	glyphs.add(entry.getValue());
                }//if
        }//while

        int curIndex = 0;
        KanjiQuiz.currentQuiz.glyphs.clear();        
        for (GlyphStat newGlyphStat : glyphs) {
        	newGlyphStat.index = curIndex;        	        	
        	KanjiQuiz.currentQuiz.glyphs.put(newGlyphStat.index, newGlyphStat);
        	curIndex++;
        }//for
    }//actuallyDeleteGlyph

    private void doDeleteGlyph(final KanjiQuiz.GlyphStat glyph)
    {
    	DialogInterface.OnClickListener okHandler = new DialogInterface.OnClickListener() {  	       
 	        @Override
 	        public void onClick(DialogInterface dialog, int which) {
 	        	actuallyDeleteGlyph(glyph);
 	        	doFillGrid();
 	        }
    	};
    	
    AlertHelpers.BasicOkCancel(getString(R.string.delete_session_conf_title), getString(R.string.delete_glyph_confirm) + " " + glyph.glyph, this, 
    							getString(R.string.OK), getString(R.string.Cancel), okHandler);
    }//doDeleteGlyph
    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {     
    	super.onActivityResult(requestCode, resultCode, data);
    	doFillGrid();
    }//onActivityResult

}//SelectKanji
