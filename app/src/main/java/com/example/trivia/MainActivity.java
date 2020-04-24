package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex = 0;
    private int currentScore = 0;
    private int scoreCounter = 0;
    private TextView scoreTextView;
    private TextView highScoreTextView;
    private Score score;
    private Prefs prefs;
    private List<Question> questionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        score = new Score();
        prefs = new Prefs(MainActivity.this);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        questionCounterTextView = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_textView);
        scoreTextView = findViewById(R.id.scoreBoard);
        highScoreTextView = findViewById(R.id.highScore);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        scoreTextView.setText(MessageFormat.format("Score:{0}", String.valueOf(score.getScore())));
        highScoreTextView.setText(MessageFormat.format("High Score:{0}", String.valueOf(prefs.getHighScore())));
        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());

                Log.d("inside", "onCreate:" + questionArrayList);
//                updateHighScore();

            }
        });

//        Log.d("Main","onCreate:"+questionList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_button:
                prefs.saveHighScore(scoreCounter);
                goToNextQuestion();


                break;
            case R.id.prev_button:
                if (currentQuestionIndex > 0) {
                    goToPreviousQuestion();
                }


                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }


    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (userChooseCorrect == answerIsTrue) {
            fadeView();
            addPoints();
            toastMessageId = R.string.correct_answer;
        } else {
            shakeAnimation();
            removePoints();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId, Toast.LENGTH_SHORT).show();

    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(currentQuestionIndex + " / " + questionList.size());


    }
//    private void updateHighScore()
//    {
//        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
//        int defaultValue = getResources().getInteger(R.integer.default_score);
//        int highScore = sharedPref.getInt(getString(R.string.score), defaultValue);
//        highScoreTextView.setText("High Score:"+ highScore);
//    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreTextView.setText(MessageFormat.format("Score:{0}", String.valueOf(scoreCounter)));

    }

    private void removePoints() {
        scoreCounter -= 100;
        if (scoreCounter > 0) {
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Score:{0}", String.valueOf(scoreCounter)));
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Score:{0}", String.valueOf(scoreCounter)));
        }
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goToNextQuestion();


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void fadeView() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goToNextQuestion();


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void goToNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();

    }

    private void goToPreviousQuestion() {
        currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
        updateQuestion();

    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());

        super.onPause();
    }
}
