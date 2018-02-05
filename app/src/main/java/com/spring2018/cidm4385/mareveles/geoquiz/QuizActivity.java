package com.spring2018.cidm4385.mareveles.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String QUESTION_ANSWERED_KEY = "question_answered";
    private static final String IS_CHEATER = "player_cheated";
    private static final String KEEP_SCORE = "keep_score";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mCheatButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex=0;
    private boolean mIsCheater;
    private int correct = 0;
    private int incorrect=0;
    private int hints = 0;

    private boolean[] mQuestionsAnswered = new boolean[mQuestionBank.length];
    private boolean[] mPlayerCheated = new boolean[mQuestionBank.length];
    private int[] mKeepScore = new int[mQuestionBank.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mQuestionsAnswered = savedInstanceState.getBooleanArray(QUESTION_ANSWERED_KEY);
            mPlayerCheated = savedInstanceState.getBooleanArray(IS_CHEATER);
            mKeepScore = savedInstanceState.getIntArray(KEEP_SCORE);
            retrieveGrade();
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex+1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex != 0){
                    mCurrentIndex = (mCurrentIndex - 1)%mQuestionBank.length;
                    updateQuestion();
                }
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mCurrentIndex = (mCurrentIndex + 1)% mQuestionBank.length;
                    mIsCheater = false;
                    updateQuestion();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT){
            if(data==null){
                return;
            }
            mPlayerCheated[mCurrentIndex] = true;
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        Log.i(TAG, "onSaveInstanceState called");
        saveInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        saveInstanceState.putBooleanArray(QUESTION_ANSWERED_KEY,mQuestionsAnswered);
        saveInstanceState.putBooleanArray(IS_CHEATER,mPlayerCheated);
        saveInstanceState.putIntArray(KEEP_SCORE,mKeepScore);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }

    private void updateQuestion(){
        mFalseButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);
        mTrueButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        checkGrade();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;
        if(mPlayerCheated[mCurrentIndex]== true){
            mIsCheater = true;
        }

        if(mIsCheater){
            messageResId = R.string.judgment_toast;
            Toast customToast = Toast.makeText(this,messageResId,Toast.LENGTH_SHORT);
            customToast.setGravity(Gravity.TOP,0,0);
            customToast.show();
            storeGrade(3);
            mPlayerCheated[mCurrentIndex] = true;
        }else{
            if (userPressedTrue == answerIsTrue){
                messageResId = R.string.correct_toast;
                storeGrade(2);
            }else {
                messageResId = R.string.incorrect_toast;
                storeGrade(1);
            }

            Toast customToast = Toast.makeText(this,messageResId,Toast.LENGTH_SHORT);
            customToast.setGravity(Gravity.TOP,0,0);
            customToast.show();
        }

        mQuestionsAnswered[mCurrentIndex] = true;
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    private void checkGrade(){
        if((correct + incorrect+ hints)== mQuestionBank.length){
            int grade = (correct*100)/mQuestionBank.length;
            String gradeDisplay = "Your total is"+grade;
            Toast gradeToast = Toast.makeText(this, gradeDisplay,Toast.LENGTH_LONG);
            gradeToast.setGravity(Gravity.TOP,0,500);
            gradeToast.show();
        }
    }
    private void storeGrade(int storeCode){
        if(storeCode == 3){
            hints = (hints+1);
            mKeepScore[mCurrentIndex]= 3;
        }
        if(storeCode == 2){
            correct = (correct + 1);
            mKeepScore[mCurrentIndex]= 2;
        }
        if(storeCode==1){
            incorrect = (incorrect + 1);
            mKeepScore[mCurrentIndex]=1;
        }
    }
    private void retrieveGrade(){
        for(int k = 0; k<mKeepScore.length;k++) {
            if (mKeepScore[k] == 3) {
                hints = +1;
            }
            if (mKeepScore[k] == 2) {
                correct = +1;
            }
            if (mKeepScore[k] == 1) {
                incorrect = +1;
            }
            if (mKeepScore[k] == 0) {
            }
        }
    }
}