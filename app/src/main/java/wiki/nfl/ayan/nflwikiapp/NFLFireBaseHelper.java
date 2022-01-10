package wiki.nfl.ayan.nflwikiapp;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import wiki.nfl.ayan.nflwikiapp.objects.NFLPreference;
import wiki.nfl.ayan.nflwikiapp.objects.NFLTrivia;

public class NFLFireBaseHelper {


    private DatabaseReference mDatabase;

    private static GoogleSignInClient googleSignInClient;
    private static GoogleSignInAccount account;
    private static String userName;



    public NFLPreference getNFLPreference() {
        NFLPreference preference = new NFLPreference();
        final TaskCompletionSource<NFLPreference> tcs = new TaskCompletionSource<>();

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            System.out.println("DB is: " + mDatabase.toString());

            DatabaseReference pref = mDatabase.child("/preferences/"+userName+"/favTeam");
            System.out.println("DB is: " + pref.toString());

            pref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // for example: if you're expecting your user's data as an object of the "User" class.
                            Object data = dataSnapshot.getValue();
                            String prefValue = dataSnapshot.getValue().toString();

                            NFLPreference p = new NFLPreference();
                            p.setFavoriteTeam(prefValue);
                            tcs.setResult(p);
                            //latch.countDown();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println(databaseError);
                            // read query is cancelled.
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        Task<NFLPreference> t = tcs.getTask();
        try {
            Tasks.await(t);
        } catch (ExecutionException | InterruptedException e) {
            t = Tasks.forException(e);
        }

        if (t.isSuccessful()) {
            preference = t.getResult();
        }
        return preference;
    }

    public List<NFLTrivia> getTriviaQuestionsFireBase() {

        final TaskCompletionSource<List<NFLTrivia>> tcs = new TaskCompletionSource<>();

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            System.out.println("DB is: " + mDatabase.toString());

            DatabaseReference pref = mDatabase.child("trivia");
            System.out.println("DB is: " + pref.toString());

            pref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // for example: if you're expecting your user's data as an object of the "User" class.
                            List<NFLTrivia> allTrivia = new ArrayList<>();
                            List<Map> data = (List<Map>) dataSnapshot.getValue();

                            for (Map m : data) {
                                NFLTrivia q = new NFLTrivia();
                                q.setChoice1((String) m.get("choice1"));
                                q.setChoice2((String) m.get("choice2"));
                                q.setQuestion((String) m.get("question"));
                                q.setCorrectAnswer((String) m.get("correctAnswer"));
                                allTrivia.add(q);

                            }
                            String prefValue = dataSnapshot.getValue().toString();
                            tcs.setResult(allTrivia);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println(databaseError);
                            // read query is cancelled.
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        Task<List<NFLTrivia>> t = tcs.getTask();
        try {
            Tasks.await(t);
        } catch (ExecutionException | InterruptedException e) {
            t = Tasks.forException(e);
        }
        List<NFLTrivia> results = null;
        if (t.isSuccessful()) {
            results = t.getResult();
        }
        return results;
    }

    public void savePreferences(NFLPreference  preference) {
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            System.out.println("DB is: " + mDatabase.toString());

            DatabaseReference pref = mDatabase.child("/preferences/"+userName+"/favTeam");
            System.out.println("DB is: " + pref.toString());
            pref.setValue(preference.getFavoriteTeam());

            pref = mDatabase.child("/preferences/"+userName+"/favPosition");
            pref.setValue(preference.getFavoritePosition());


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<NFLTrivia> getTriviaQuestions() throws JSONException {
        List<NFLTrivia> allTrivia = new ArrayList<>();
        JSONArray array = new JSONArray(questions);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            NFLTrivia q = new NFLTrivia();
            q.setChoice1(obj.getString("choice1"));
            q.setChoice2(obj.getString("choice2"));
            q.setQuestion(obj.getString("question"));
            q.setCorrectAnswer(obj.getString("correctAnswer"));
            allTrivia.add(q);

        }
        return allTrivia;

    }

    public static GoogleSignInAccount getAccount() {
        return account;
    }

    public static void setAccount(GoogleSignInAccount account) {
        if(account == null){
            userName = null;
        }else{
            String email = account.getEmail();
            userName = email.substring(0,email.indexOf("@"));
        }
        NFLFireBaseHelper.account = account;
    }

    public static GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public static void setGoogleSignInClient(GoogleSignInClient googleSignInClient) {
        NFLFireBaseHelper.googleSignInClient = googleSignInClient;
    }

    private String questions = "[\n" +
            "  {\n" +
            "    \"choice1\": \"3\",\n" +
            "    \"choice2\": \"5\",\n" +
            "    \"correctAnswer\": \"5\",\n" +
            "    \"question\": \"How many years must a player be retired to be eligible for the Pro Football Hall of Fame?\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"choice1\": \"John Madden\",\n" +
            "    \"choice2\": \"Joe Montana\",\n" +
            "    \"correctAnswer\": \"Joe Montana\",\n" +
            "    \"question\": \"What record-setting quarterback was the NFL's 82nd draft pick in 1979?\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"choice1\": \"Padding\",\n" +
            "    \"choice2\": \"Helmets\",\n" +
            "    \"correctAnswer\": \"Helmets\",\n" +
            "    \"question\": \"What were NFL players required to wear in games for the first time in 1943?\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"choice1\": \"11\",\n" +
            "    \"choice2\": \"10\",\n" +
            "    \"correctAnswer\": \"11\",\n" +
            "    \"question\": \"How many football teams play in the Big Ten Conference?\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"choice1\": \"Eagles\",\n" +
            "    \"choice2\": \"Giants\",\n" +
            "    \"correctAnswer\": \"Giants\",\n" +
            "    \"question\": \"What pro football franchise did Tim Mara buy in 1925 for $500?\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"choice1\": \"Bobby Brown\",\n" +
            "    \"choice2\": \"Barry Sanders\",\n" +
            "    \"correctAnswer\": \"Barry Sanders\",\n" +
            "    \"question\": \"What elusive Detroit running back has been dubbed 'the Lion King'\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"choice1\": \"Buffalo Bills\",\n" +
            "    \"choice2\": \"Dallas Cowboys\",\n" +
            "    \"correctAnswer\": \"Buffalo Bills\",\n" +
            "    \"question\": \"Who did the New York Giants beat by a point in the closest Super Bowl ever?\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"choice1\": \"Green Bay Packers\",\n" +
            "    \"choice2\": \"Dallas Cowboys\",\n" +
            "    \"correctAnswer\": \"Green Bay Packers\",\n" +
            "    \"question\": \"What team won the first Super Bowl?\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"choice1\": \"Los Angeles\",\n" +
            "    \"choice2\": \"Orlando\",\n" +
            "    \"correctAnswer\": \"Los Angeles\",\n" +
            "    \"question\": \"What city did the Rams play in before St. Louis?\"\n" +
            "  }\n" +
            "]";

}
