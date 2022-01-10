package wiki.nfl.ayan.nflwikiapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import wiki.nfl.ayan.nflwikiapp.objects.NFLPreference;
import wiki.nfl.ayan.nflwikiapp.objects.NFLTeam;
import wiki.nfl.ayan.nflwikiapp.objects.NFLTrivia;
import wiki.nfl.ayan.nflwikiapp.objects.RosterPlayer;


public class TriviaFragment extends Fragment {

    View fragmentView;
    Button nextButton;
    Button checkAnswer;
    RadioGroup group;
    RadioButton firstChoice;
    RadioButton secondChoice;
    TextView question;
    List<NFLTrivia> trivia;
    int clicks;
    Boolean clickCheckedAnswer = false;
    int randomNumber;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_trivia, null);
        nextButton = fragmentView.findViewById(R.id.id_nextQuestion);
        checkAnswer = fragmentView.findViewById(R.id.id_button_checkAnswer);
        group = fragmentView.findViewById(R.id.id_radioGroup_radioGroup);
        firstChoice = fragmentView.findViewById(R.id.id_radioButton_firstChoice);
        secondChoice = fragmentView.findViewById(R.id.id_radioButton_secondChoice);
        question = fragmentView.findViewById(R.id.id_textView_question);

        bindView();

        return fragmentView;
    }

    public void bindView(){

        new MyAsyncTask(getActivity(), fragmentView).execute();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!clickCheckedAnswer && clicks>0){

                    Toast.makeText(fragmentView.getContext(),"Please Click on Check Answer and Answer Question", Toast.LENGTH_SHORT).show();

                }else if(clicks == 9){
                    clicks =0;
                    randomNumber = (int)(Math.random()*2)+1;
                    clicks++;
                    new MyAsyncTask(getActivity(), fragmentView).execute();
                    clickCheckedAnswer = false;
                }
                else {
                    randomNumber = (int)(Math.random()*2)+1;
                    clicks++;
                    new MyAsyncTask(getActivity(), fragmentView).execute();
                    clickCheckedAnswer = false;
                }

            }
        });
    }


    NFLTrivia triviaQuestion;
    List<NFLTrivia> allTrivia = new ArrayList<>();

    class MyAsyncTask extends AsyncTask<String, String, String> {
        View myView;
        Activity mContex;




        public MyAsyncTask(Activity contex, View v) {
            this.myView = v;
            this.mContex = contex;
        }


        @Override
        protected String doInBackground(String... strings) {

            if(allTrivia.isEmpty()){
                try {
                    NFLFireBaseHelper fireBaseHelper = new NFLFireBaseHelper();
                    allTrivia = fireBaseHelper.getTriviaQuestionsFireBase();
                } catch (   Exception e) {
                    e.printStackTrace();
                }
            }
            if(clicks==0){
                triviaQuestion = allTrivia.get(0);
            }else{
                triviaQuestion = allTrivia.get(clicks-1);
            }


            return null;
        }

        protected void onPostExecute(String result) {
            question.setText(triviaQuestion.getQuestion());



            //if(randomNumber == 1){
                firstChoice.setText(triviaQuestion.getChoice1());
                secondChoice.setText(triviaQuestion.getChoice2());
            //}

            //if(randomNumber == 2){
            //    firstChoice.setText(triviaQuestion.getChoice2());
            //    secondChoice.setText(triviaQuestion.getChoice1());
            //}

            checkAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(group.getCheckedRadioButtonId() == R.id.id_radioButton_firstChoice){
                        if(firstChoice.getText().equals(triviaQuestion.getCorrectAnswer())){
                            Toast.makeText(fragmentView.getContext(), "RIGHT ANSWER!", Toast.LENGTH_LONG).show();
                            clickCheckedAnswer = true;
                        }else{
                            Toast.makeText(fragmentView.getContext(), "WRONG ANSWER!", Toast.LENGTH_LONG).show();
                            clickCheckedAnswer = true;
                        }

                    }
                    if(group.getCheckedRadioButtonId() == R.id.id_radioButton_secondChoice){
                        if(secondChoice.getText().equals(triviaQuestion.getCorrectAnswer())){
                            Toast.makeText(fragmentView.getContext(), "RIGHT ANSWER!", Toast.LENGTH_LONG).show();
                            clickCheckedAnswer = true;
                        }else{
                            Toast.makeText(fragmentView.getContext(), "WRONG ANSWER!", Toast.LENGTH_LONG).show();
                            clickCheckedAnswer = true;
                        }

                    }
                }
            });






        }
    }
}
