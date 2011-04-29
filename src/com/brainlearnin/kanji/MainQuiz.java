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

import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

import com.brainlearnin.kanji.KanjiQuiz.QuizGlyphStat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainQuiz extends Activity
{
	public static final int QuizMode_On = 1;
	public static final int QuizMode_Kun = 2;
	public static final int QuizMode_OnKun = 3;
	public static final int QuizMode_Meanings = 4;
	
	int quizMode = 0;
	EditText answerTextEdit;
	TextView onTextView;
	TextView kunTextView;
	TextView meaningsTextView;
	TextView kanjiTextView;
	TextView wordCountTextView;
	TextView progressCountText;
	ProgressBar progressBar;
	boolean quickQuiz;
	boolean enableGrading;
	int originalQuizSize;
	
	Random randomGenerator = new Random();

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainquiz);
        
        Bundle bundle = this.getIntent().getExtras();
        quizMode = bundle.getInt("quizMode");
        quickQuiz = bundle.getBoolean("quickQuiz", false);
        enableGrading = bundle.getBoolean("enableGrading", false);
        
        Button failButton = (Button)findViewById(R.id.main_quiz_fail_button);
        failButton.setOnClickListener(failButtonClick);
        
        Button verifyButton = (Button)findViewById(R.id.main_quiz_verity_button);
        verifyButton.setOnClickListener(verifyButtonClick);
        
        Button cheatButton = (Button)findViewById(R.id.main_quiz_cheat_button);
        cheatButton.setOnClickListener(cheatButtonClick);
        
        answerTextEdit = (EditText)findViewById(R.id.main_quiz_edit);
        onTextView = (TextView)findViewById(R.id.main_quiz_on_text);
        kunTextView = (TextView)findViewById(R.id.main_quiz_kun_text);
        meaningsTextView = (TextView)findViewById(R.id.main_quiz_meanings_text);
        kanjiTextView = (TextView)findViewById(R.id.main_quiz_kanji_text_view);        
        wordCountTextView = (TextView)findViewById(R.id.main_quiz_word_text);
        progressBar = (ProgressBar)findViewById(R.id.main_quiz_progressbar);
        progressCountText = (TextView)findViewById(R.id.main_quiz_progress_count);
        
        answerTextEdit.addTextChangedListener(answerTextEditTextChangedListener);
        
        ////answerTextEdit.setOnClickListener(answerTextEditClick);
                        
        if (KanjiQuiz.currentQuiz.quizPreferences.showWordCycle == true) {
        	wordCountTextView.setVisibility(View.VISIBLE);
        } else {
        	wordCountTextView.setVisibility(View.GONE);
        }//if        
        
        createQuiz();
        
        if (KanjiQuiz.currentQuiz.currentQuizGlyphs.size() != 0) {        
        	setNextQuestion();
        } else {
        	kanjiTextView.setText("");
    		answerTextEdit.setText(getString(R.string.main_quiz_no_entries_message));
        	AlertHelpers.BasicAlert(getString(R.string.main_quiz_no_entries_title), getString(R.string.main_quiz_no_entries_message), getString(R.string.OK), MainQuiz.this);        	
        }//if
    }//onCreate
	
	private TextWatcher answerTextEditTextChangedListener = new TextWatcher() 
	{
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) 
        {
        	if (s.length() == 0) {
        		return;
        	}//if
        	
        	if (s.charAt(s.length()-1) == '\n') {
        		if (s.length() > 1) {
        			verifyButtonClick.onClick(null);
        		} else {
        			answerTextEdit.setText("");
        		}//if
        	}//if
        }

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}
    };//answerTextEditTextChangedListener

	
	private java.util.ArrayList<String> getAnswerList(String answer)
	{
		java.util.ArrayList<String> returnList = new java.util.ArrayList<String>();
		
		if (answer.length() == 0) {
			return returnList;
		}//if
	
		for (String separator : KanjiQuiz.currentQuiz.quizPreferences.acceptedSeparators) {
			answer = answer.replace(separator, ",");
		}//for		
	
		String[] readings = answer.split(",");
		for (String str : readings) {
			returnList.add(str.trim());			
		}//for
		
		return returnList;
	}//getAnswerList
	
	private String getCorrectListStr(KanjiQuiz.QuizGlyphStat firstGlyphStat)
	{
		String correctAnswer = "";
    	switch (firstGlyphStat.questionMode) {
			case QuizMode_On:
				correctAnswer = firstGlyphStat.glyphStat.onReadings.toString();
				break;
				
			case QuizMode_Kun:
				correctAnswer = firstGlyphStat.glyphStat.kunReadings.toString();
				break;
				
			case QuizMode_Meanings:
				correctAnswer = firstGlyphStat.glyphStat.meanings.toString();
				break;
		}//switch
    	
    	correctAnswer = correctAnswer.substring(1, correctAnswer.length() - 1);
    	
    	return correctAnswer;
	}//getCorrectListStr
	
	private View.OnClickListener failButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	if (KanjiQuiz.currentQuiz.currentQuizGlyphs.size() == 0) {
        		goToScoring();
    			return;
    		}//if
    				
    		KanjiQuiz.QuizGlyphStat firstGlyphStat = KanjiQuiz.currentQuiz.currentQuizGlyphs.get(0);
    		
        	String correctAnswer = getCorrectListStr(firstGlyphStat);
        	incorrectToast(correctAnswer);
        	
        	firstGlyphStat.attempted++;
        	if (KanjiQuiz.currentQuiz.quizPreferences.resetCorrectCountOnWrongAnswer == true) {
        		if (KanjiQuiz.currentQuiz.quizPreferences.doRealLeitner == true) {
        			firstGlyphStat.correctAnswer = 0;
        		} else {
        			--firstGlyphStat.correctAnswer;
        			if (firstGlyphStat.correctAnswer < 0) {
        				firstGlyphStat.correctAnswer = 0;
        			}//if
        		}//if
        	}//if
        	
        	if (firstGlyphStat.attempted == 1) {
        		firstGlyphStat.firstAnswerCorrect = false;
        	}//if
        	
        	setNextQuestion();
        }
    };//failButtonClick
    
    private View.OnClickListener verifyButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	if (KanjiQuiz.currentQuiz.currentQuizGlyphs.size() == 0) {
        		goToScoring();
    			return;
    		}//if
    				
    		KanjiQuiz.QuizGlyphStat firstGlyphStat = KanjiQuiz.currentQuiz.currentQuizGlyphs.get(0);
    		
    		String answerText = convertKatanaToHiragana(answerTextEdit.getText().toString());
    		
        	java.util.ArrayList<String> answerList = getAnswerList(answerText);
        	java.util.ArrayList<String> correctList = new java.util.ArrayList<String>();
        	
        	switch (firstGlyphStat.questionMode) {
				case QuizMode_On:
					correctList = firstGlyphStat.glyphStat.onReadings;
					break;
					
				case QuizMode_Kun:
					correctList = firstGlyphStat.glyphStat.kunReadings;
					break;
					
				case QuizMode_Meanings:
					correctList = firstGlyphStat.glyphStat.meanings;
					break;
			}//switch
        	
        	java.util.Collections.sort(answerList, String.CASE_INSENSITIVE_ORDER);
        	java.util.Collections.sort(correctList, String.CASE_INSENSITIVE_ORDER);
        	
        	boolean isSame = true;
        	if (answerList.size() == correctList.size()) {
        		int listSize = answerList.size();
        		for (int pos = 0; pos < listSize; ++pos) {
        			String correctStr = convertKatanaToHiragana(correctList.get(pos));
        			if (answerList.get(pos).equals(correctStr) == false) {
        				isSame = false;
        				break;
        			}//if
        		}//for
        	} else {
        		isSame = false;
        	}//if
        	
        	firstGlyphStat.attempted++;
        	
        	if (isSame == true) {
        		String correctAnswerStr = getCorrectListStr(firstGlyphStat);
        		correctToast(correctAnswerStr);
        		
        		firstGlyphStat.correctAnswer++;
        		
        		if (firstGlyphStat.attempted == 1) {
            		firstGlyphStat.firstAnswerCorrect = true;
            	}//if
        	} else {
        		String correctAnswerStr = getCorrectListStr(firstGlyphStat);        		
        		incorrectToast(correctAnswerStr);
        		
        		if (KanjiQuiz.currentQuiz.quizPreferences.resetCorrectCountOnWrongAnswer == true) {
            		if (KanjiQuiz.currentQuiz.quizPreferences.doRealLeitner == true) {
            			firstGlyphStat.correctAnswer = 0;
            		} else {
            			--firstGlyphStat.correctAnswer;
            			if (firstGlyphStat.correctAnswer < 0) {
            				firstGlyphStat.correctAnswer = 0;
            			}//if
            		}//if
            	}//if
        		
        		if (firstGlyphStat.attempted == 1) {
            		firstGlyphStat.firstAnswerCorrect = false;
            	}//if
        	}//if
        	
        	setNextQuestion();
        }
    };//verifyButtonClick
    
    private View.OnClickListener cheatButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	if (KanjiQuiz.currentQuiz.currentQuizGlyphs.size() == 0) {
        		goToScoring();
    			return;
    		}//if
    				
    		KanjiQuiz.QuizGlyphStat firstGlyphStat = KanjiQuiz.currentQuiz.currentQuizGlyphs.get(0);
    		
    		String correctAnswer = getCorrectListStr(firstGlyphStat);
    		correctToast(correctAnswer);
        	
        	firstGlyphStat.attempted++;
        	firstGlyphStat.correctAnswer++;
        	
        	if (firstGlyphStat.attempted == 1) {
        		firstGlyphStat.firstAnswerCorrect = true;
        	}//if
        	
        	setNextQuestion();
        }
    };//cheatButtonClick
    
    private void correctToast(String correctAnswer)
    {
    	LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_answer_correct, (ViewGroup)findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.toastText);
        text.setText(correctAnswer);
        
        ImageView myImage = (ImageView) layout.findViewById(R.id.toastImage);
        myImage.setAlpha(127);
     
        Toast t = new Toast(getApplicationContext());
        t.setDuration(Toast.LENGTH_SHORT);
        t.setView(layout);
        t.show();    	
    }//correctToast
    
    private void incorrectToast(String incorrectAnswer)
    {
    	LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_answer_incorrect, (ViewGroup)findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.toastText);
        text.setText(incorrectAnswer);
        
        ImageView myImage = (ImageView) layout.findViewById(R.id.toastImage);
        myImage.setAlpha(127);
     
        Toast t = new Toast(getApplicationContext());
        t.setDuration(Toast.LENGTH_SHORT);
        t.setView(layout);
        t.show();    	
    }//incorrectToast
    
    /*
    private View.OnClickListener answerTextEditClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Toast.makeText(getBaseContext(), "answerTextEditClick", Toast.LENGTH_SHORT).show();
        }
    };//answerTextEditClick
    */
    
    private void goToScoring()
    {    	
    	KanjiQuiz.currentQuiz.exportQuiz(KanjiQuiz.currentQuiz.quizFile, this);
    	progressBar.setProgress(KanjiQuiz.currentQuiz.currentQuizGlyphsOrig.size());
    	progressCountText.setText(Integer.toString(progressBar.getMax()) + " / " + Integer.toString(progressBar.getMax()));
    	wordCountTextView.setText(Integer.toString(KanjiQuiz.currentQuiz.quizPreferences.numTimesToRepeatCorrectUntilAccepted) + " / " + KanjiQuiz.currentQuiz.quizPreferences.numTimesToRepeatCorrectUntilAccepted);
    	
    	Intent scoringIntent = new Intent(getBaseContext(), Scoring.class);

    	scoringIntent.putExtra("originalQuizSize", originalQuizSize);
    	startActivity(scoringIntent);
    }//goToScoring
	
	private void setNextQuestion()
	{
		if (KanjiQuiz.currentQuiz.currentQuizGlyphs.size() == 0) {			
			return;
		}//if
				
		KanjiQuiz.QuizGlyphStat firstGlyphStat = KanjiQuiz.currentQuiz.currentQuizGlyphs.get(0);
		KanjiQuiz.currentQuiz.currentQuizGlyphs.remove(0);		
		
		if (quickQuiz == true) {
			//Nothing to do.. we don't touch grading and only ask the question once
		} else {		
			if (firstGlyphStat.correctAnswer != KanjiQuiz.currentQuiz.quizPreferences.numTimesToRepeatCorrectUntilAccepted) {		
				int minLocality = KanjiQuiz.currentQuiz.quizPreferences.minQuestionLocality;
				if (minLocality > KanjiQuiz.currentQuiz.currentQuizGlyphs.size()) {
					minLocality = 1;
				}//if
				
				int maxLocality = KanjiQuiz.currentQuiz.quizPreferences.maxQuestionLocality;
				if (maxLocality > KanjiQuiz.currentQuiz.currentQuizGlyphs.size()) {
					maxLocality = KanjiQuiz.currentQuiz.currentQuizGlyphs.size();
				}//if
				
				if (maxLocality < 0) {
					maxLocality = Integer.MAX_VALUE;
				}//if
				
				int nextPos = randomGenerator.nextInt(maxLocality - minLocality) + minLocality;
				if (nextPos > KanjiQuiz.currentQuiz.currentQuizGlyphs.size()) {
					nextPos = KanjiQuiz.currentQuiz.currentQuizGlyphs.size();
				}//if
				
				KanjiQuiz.currentQuiz.currentQuizGlyphs.add(nextPos, firstGlyphStat);
			} else {
				if (enableGrading == true) {
					if (firstGlyphStat.attempted == firstGlyphStat.correctAnswer) {
						switch (firstGlyphStat.questionMode) {
							case QuizMode_On:
								firstGlyphStat.glyphStat.onGrade++;
								if (KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() <= firstGlyphStat.glyphStat.onGrade) {
									firstGlyphStat.glyphStat.onGrade = KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() - 1;
								}//if							
								
								firstGlyphStat.glyphStat.lastOnAnswer = Calendar.getInstance().getTime();
								firstGlyphStat.glyphStat.lastOnAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastOnAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastOnAnswer.setHours(0);							
								break;
								
							case QuizMode_Kun:
								firstGlyphStat.glyphStat.kunGrade++;
								if (KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() <= firstGlyphStat.glyphStat.kunGrade) {
									firstGlyphStat.glyphStat.kunGrade = KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() - 1;
								}//if							
								
								firstGlyphStat.glyphStat.lastKunAnswer = Calendar.getInstance().getTime();
								firstGlyphStat.glyphStat.lastKunAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastKunAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastKunAnswer.setHours(0);
								break;
								
							case QuizMode_Meanings:
								firstGlyphStat.glyphStat.meaningsGrade++;
								if (KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() <= firstGlyphStat.glyphStat.meaningsGrade) {
									firstGlyphStat.glyphStat.meaningsGrade = KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() - 1;
								}//if							
								
								firstGlyphStat.glyphStat.lastMeaningAnswer = Calendar.getInstance().getTime();
								firstGlyphStat.glyphStat.lastMeaningAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastMeaningAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastMeaningAnswer.setHours(0);
								break;
						}//switch
					} else {
						switch (firstGlyphStat.questionMode) {
							case QuizMode_On:
								firstGlyphStat.glyphStat.onGrade = 0;
								
								firstGlyphStat.glyphStat.lastOnAnswer = Calendar.getInstance().getTime();
								firstGlyphStat.glyphStat.lastOnAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastOnAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastOnAnswer.setHours(0);							
								break;
								
							case QuizMode_Kun:
								firstGlyphStat.glyphStat.kunGrade = 0;
								
								firstGlyphStat.glyphStat.lastKunAnswer = Calendar.getInstance().getTime();
								firstGlyphStat.glyphStat.lastKunAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastKunAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastKunAnswer.setHours(0);
								break;
								
							case QuizMode_Meanings:
								firstGlyphStat.glyphStat.meaningsGrade = 0;
								
								firstGlyphStat.glyphStat.lastMeaningAnswer = Calendar.getInstance().getTime();
								firstGlyphStat.glyphStat.lastMeaningAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastMeaningAnswer.setHours(0);
								firstGlyphStat.glyphStat.lastMeaningAnswer.setHours(0);
								break;
						}//switch
					}//if
				}//if
			}//if
		}//if (quickQuiz == true) {
		
		if (KanjiQuiz.currentQuiz.currentQuizGlyphs.size() == 0) {
			goToScoring();
			return;
		}//if
		
		firstGlyphStat = KanjiQuiz.currentQuiz.currentQuizGlyphs.get(0);
		
		kanjiTextView.setText(firstGlyphStat.glyphStat.glyph);
		answerTextEdit.setText("");
		answerTextEdit.requestFocus();
		answerTextEdit.requestFocusFromTouch();		
		
		switch (firstGlyphStat.questionMode) {
			case QuizMode_On:
				onTextView.setVisibility(View.VISIBLE);
				kunTextView.setVisibility(View.INVISIBLE);
				meaningsTextView.setVisibility(View.INVISIBLE);
				break;
				
			case QuizMode_Kun:
				onTextView.setVisibility(View.INVISIBLE);
				kunTextView.setVisibility(View.VISIBLE);
				meaningsTextView.setVisibility(View.INVISIBLE);
				break;
				
			case QuizMode_Meanings:
				onTextView.setVisibility(View.INVISIBLE);
				kunTextView.setVisibility(View.INVISIBLE);
				meaningsTextView.setVisibility(View.VISIBLE);
				break;
		}//switch
		
		wordCountTextView.setText(Integer.toString(firstGlyphStat.correctAnswer) + " / " + KanjiQuiz.currentQuiz.quizPreferences.numTimesToRepeatCorrectUntilAccepted);
		
		progressBar.setProgress(KanjiQuiz.currentQuiz.currentQuizGlyphsOrig.size() - KanjiQuiz.currentQuiz.currentQuizGlyphs.size());
		progressCountText.setText(Integer.toString(KanjiQuiz.currentQuiz.currentQuizGlyphsOrig.size() - KanjiQuiz.currentQuiz.currentQuizGlyphs.size()) + " / " + Integer.toString(progressBar.getMax()));
	}//setNextQuestion
	
	public static boolean isExpired(int grade, java.util.Date date, int offset)
	{
		if (KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() == 0) {
			return true;
		}//if
				
		if (KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() <= grade) {
			grade = KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() - 1;
		}//if
		
		int timeout = KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.get(grade);
		
		java.util.Date curDate = Calendar.getInstance().getTime();
		curDate.setHours(0);
		curDate.setMinutes(0);
		curDate.setSeconds(0);
		
		//Note: Fails for leap years and daylight savings days 			
		int diffDays = (int)( (curDate.getTime() - date.getTime()) / (1000 * 60 * 60 * 24)) + offset;
		
		if (diffDays >= timeout) {
			return true;
		} else {
			return false;
		}//if
	}//isExpired
	
	private void createQuiz()
	{
		java.util.List<QuizGlyphStat> newQuizList = new java.util.ArrayList<QuizGlyphStat>(); 

        KanjiQuiz curQuiz = KanjiQuiz.currentQuiz;
        Preferences quizPreferences = curQuiz.quizPreferences;

        java.util.HashMap<String, KanjiQuiz.GlyphStat> glyphsMap = new java.util.HashMap<String, KanjiQuiz.GlyphStat>();
        Iterator<Entry<Integer, KanjiQuiz.GlyphStat>> iter = curQuiz.glyphs.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, KanjiQuiz.GlyphStat> entry = iter.next();

            glyphsMap.put(entry.getValue().glyph, entry.getValue());
        }//while

        for (String glyphStr : quizPreferences.testSet) {
            if (glyphsMap.containsKey(glyphStr) == true) {
            	boolean onExpired = false;
            	boolean kunExpired = false;
            	boolean meaningsExpired = false;
            	
            	KanjiQuiz.GlyphStat curGlyph = glyphsMap.get(glyphStr); 
            	
            	if ((enableGrading == false) || (isExpired(curGlyph.onGrade, curGlyph.lastOnAnswer, 0) == true)) {
            		onExpired = true;
            	}//if
            	
            	if ((enableGrading == false) || (isExpired(curGlyph.kunGrade, curGlyph.lastKunAnswer, 0) == true)) {
            		kunExpired = true;
            	}//if
            	
            	if ((enableGrading == false) || (isExpired(curGlyph.meaningsGrade, curGlyph.lastMeaningAnswer, 0) == true)) {
            		meaningsExpired = true;
            	}//if
            	
            	switch(quizMode) {
            		case QuizMode_On:
            			if (true == onExpired) {
            				KanjiQuiz.QuizGlyphStat newQuizGlyphStat = new KanjiQuiz.QuizGlyphStat();
            				newQuizGlyphStat.questionMode = QuizMode_On;
            				newQuizGlyphStat.glyphStat = curGlyph;
            				newQuizGlyphStat.originalGrade = curGlyph.onGrade;
            				newQuizList.add(newQuizGlyphStat);
            			}//if
            			break;
            			
            		case QuizMode_Kun:
            			if (true == kunExpired) {
            				KanjiQuiz.QuizGlyphStat newQuizGlyphStat = new KanjiQuiz.QuizGlyphStat();
            				newQuizGlyphStat.questionMode = QuizMode_Kun;
            				newQuizGlyphStat.glyphStat = curGlyph;
            				newQuizGlyphStat.originalGrade = curGlyph.kunGrade;
            				newQuizList.add(newQuizGlyphStat);
            			}//if
            			break;
            			
            		case QuizMode_OnKun:
            			if (true == onExpired) {
            				KanjiQuiz.QuizGlyphStat newQuizGlyphStat = new KanjiQuiz.QuizGlyphStat();
            				newQuizGlyphStat.questionMode = QuizMode_On;
            				newQuizGlyphStat.glyphStat = curGlyph;
            				newQuizGlyphStat.originalGrade = curGlyph.onGrade;
            				newQuizList.add(newQuizGlyphStat);
            			}//if
            			
            			if (true == kunExpired) {
            				KanjiQuiz.QuizGlyphStat newQuizGlyphStat = new KanjiQuiz.QuizGlyphStat();
            				newQuizGlyphStat.questionMode = QuizMode_Kun;
            				newQuizGlyphStat.glyphStat = curGlyph;
            				newQuizGlyphStat.originalGrade = curGlyph.kunGrade;
            				newQuizList.add(newQuizGlyphStat);
            			}//if
            			
            			break;
            			
            		case QuizMode_Meanings:
            			if (true == meaningsExpired) {
            				KanjiQuiz.QuizGlyphStat newQuizGlyphStat = new KanjiQuiz.QuizGlyphStat();
            				newQuizGlyphStat.questionMode = QuizMode_Meanings;
            				newQuizGlyphStat.glyphStat = curGlyph;
            				newQuizGlyphStat.originalGrade = curGlyph.meaningsGrade;
            				newQuizList.add(newQuizGlyphStat);
            			}//if
            			break;
            	}//switch            	                
            }//if
        }//for
        
        originalQuizSize = newQuizList.size();

        java.util.Collections.shuffle(newQuizList);
        
        if ((quickQuiz == true) && (newQuizList.size() > 20)) {
        	newQuizList = newQuizList.subList(0, 20);
        }//if
        
        if (newQuizList.size() > KanjiQuiz.currentQuiz.quizPreferences.maxEntriesPerSession) {
        	KanjiQuiz.currentQuiz.currentQuizGlyphs = newQuizList.subList(0, KanjiQuiz.currentQuiz.quizPreferences.maxEntriesPerSession - 1);
        } else {
        	KanjiQuiz.currentQuiz.currentQuizGlyphs = newQuizList;
        }//if
        
        progressBar.setMax(KanjiQuiz.currentQuiz.currentQuizGlyphs.size());
        progressBar.setProgress(0);
        progressCountText.setText(Integer.toString(progressBar.getMax()) + " / " + Integer.toString(progressBar.getMax()));
        
        KanjiQuiz.currentQuiz.currentQuizGlyphsOrig = new java.util.ArrayList<QuizGlyphStat>(); 
        for (QuizGlyphStat glyphStat : KanjiQuiz.currentQuiz.currentQuizGlyphs) {
        	KanjiQuiz.currentQuiz.currentQuizGlyphsOrig.add(glyphStat);
        }//for
	}//createQuiz
	
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
                aboutIntent.putExtra("titleRes", R.string.mainQuizTitle);
                aboutIntent.putExtra("stringVal", R.string.mainQuiz_help_text);
        
                startActivity(aboutIntent);
                break;
            }
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected   
	
	private String convertKatanaToHiragana(String str)
	{
		String retStr = "";
		
		for (int pos = 0; pos < str.length(); ++pos) {
			char curChar = str.charAt(pos);
			
			if ((curChar >= '\u30A1') && (curChar <= '\u30FF')) {
				curChar -= ('\u30A1' - '\u3041');
			}//if
			
			retStr = retStr + curChar;
		}//for
		
		return retStr;
	}//convertKatanaToHiragana
	
}//MainQuiz
