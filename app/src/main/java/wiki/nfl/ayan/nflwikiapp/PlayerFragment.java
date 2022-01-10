package wiki.nfl.ayan.nflwikiapp;

import android.app.Activity;
import android.bluetooth.le.AdvertisingSetParameters;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.nio.file.FileStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wiki.nfl.ayan.nflwikiapp.objects.NFLPlayer;
import wiki.nfl.ayan.nflwikiapp.objects.NFLPreference;
import wiki.nfl.ayan.nflwikiapp.objects.RosterPlayer;

public class PlayerFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    Button search;
    EditText enterPlayer;
    TextView firstName;
    TextView lastName;
    TextView playerStats;
    ImageView playerImage;
    View fragmentView;
    TextView preferences;
    ListView listView;
    ConstraintLayout layout;
    Button moreInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_players,null);

        search = fragmentView.findViewById(R.id.id_button_searchPlayer);
        firstName = fragmentView.findViewById(R.id.id_textView_firstName);
        enterPlayer = fragmentView.findViewById(R.id.id_editText_EnterPlayer);
        playerImage = fragmentView.findViewById(R.id.id_imageView_playerImage);
        playerStats = fragmentView.findViewById(R.id.id_textView_playerStats);
        lastName = fragmentView.findViewById(R.id.id_textView_lastName);
        layout = fragmentView.findViewById(R.id.id_layout);
        preferences = fragmentView.findViewById(R.id.id_textView_preferences);
        moreInfo = fragmentView.findViewById(R.id.id_button_findOutMoreInformation);
        layout.getBackground().setAlpha(80);

        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);






        bindView();

        return fragmentView;
    }

    public void bindView() {

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText =enterPlayer.getText().toString();
                new MyAsyncTask(getActivity(), fragmentView).execute(searchText);
            }
        });

    }

    private static Map<String,String> nameMap = new HashMap<>();

    class MyAsyncTask extends AsyncTask<String, String, String> {
        View myView;
        Activity mContex;


        NFLPlayer currentPlayer;
        RosterPlayer rosterPlayer;
        NFLFireBaseHelper fbHelper = new NFLFireBaseHelper();
        NFLPreference preference = null;
        NFLPlayer player = null;



        public MyAsyncTask(Activity contex, View v) {
            this.myView = v;
            this.mContex = contex;
        }

        @Override
        protected String doInBackground(String... parameters) {
            NFLRestAPIHelper helper = new NFLRestAPIHelper();
            if(parameters.length > 0 ){

                String playerSearchText = parameters[0];
                try {
                    if(nameMap.isEmpty()){
                        nameMap = helper.getAllPlayersMap();
                        helper.writeToFile(nameMap,mContex);
                        nameMap = helper.getAllPlayerMapFromFile(mContex);
                    }

                    Collection<String> keys = nameMap.keySet();
                    String id = null;
                    for (String name : keys) {
                        if(name.toLowerCase().contains(playerSearchText.toLowerCase().trim())){
                            id = nameMap.get(name);
                            break;
                        }
                    }
                    currentPlayer = helper.getPlayerById(id);
                    if(currentPlayer != null){
                        String team = currentPlayer.getTeam();
                        List<RosterPlayer> rosterPlayers = helper.getRosterPlayers(team);
                        for (RosterPlayer r: rosterPlayers){
                            if(r.getPlayerId().equals(currentPlayer.getPlayerId())){
                                rosterPlayer = r;
                                break;
                            }
                        }
                    }
                    preference = fbHelper.getNFLPreference();
                   // player.setPosition(preference.getFavoritePosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(fragmentView.getContext(),"Please Enter A Player",Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            if(currentPlayer!= null) {
                if (currentPlayer.getPosition().equals("QB")) {
                    playerStats.setText("Position: " + currentPlayer.getPosition() + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");
                    playerStats.setText(playerStats.getText() + "Passing Yards: " + currentPlayer.getPassingYards() + "\n");
                    playerStats.setText(playerStats.getText() + "Passing Touchdowns: " + currentPlayer.getPassingTouchDowns() + "\n");
                    playerStats.setText(playerStats.getText() + "Passer Rating: " + currentPlayer.getPasserRating() + "\n");
                }

                if (currentPlayer.getPosition().equals("WR") || currentPlayer.getPosition().equals("TE")) {
                    playerStats.setText("Position: " + currentPlayer.getPosition() + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");
                    playerStats.setText(playerStats.getText() + "Receiving Yards: " + currentPlayer.getReceivingYards() + "\n");
                    playerStats.setText(playerStats.getText() + "Receiving Touchdowns: " + currentPlayer.getReceivingTouchDowns() + "\n");
                    playerStats.setText(playerStats.getText() + "Receptions: " + currentPlayer.getReceptions() + "\n");
                }

                if (currentPlayer.getPosition().equals("RB")) {
                    playerStats.setText("Position: " + currentPlayer.getPosition() + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");
                    playerStats.setText(playerStats.getText() + "Rushing Yards: " + currentPlayer.getRushingYards() + "\n");
                    playerStats.setText(playerStats.getText() + "Rushing Attempts: " + currentPlayer.getRushingAttempts() + "\n");
                    playerStats.setText(playerStats.getText() + "Rushing Touchdowns: " + currentPlayer.getRushingTouchDowns() + "\n");
                }

                if (currentPlayer.getPosition().equals("DT") || (currentPlayer.getPosition().equals("DE")) || (currentPlayer.getPosition().equals("OLB")) || (currentPlayer.getPosition().equals("LB"))) {
                    playerStats.setText("Position: " + currentPlayer.getPosition() + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");
                    playerStats.setText(playerStats.getText() + "Tackles: " + currentPlayer.getTackles() + "\n");
                    playerStats.setText(playerStats.getText() + "Sacks: " + currentPlayer.getSacks() + "\n");
                    playerStats.setText(playerStats.getText() + "Fumbles Forced: " + currentPlayer.getFumblesForced() + "\n");
                    playerStats.setText(playerStats.getText() + "Fumbles Recovered: " + currentPlayer.getFumblesRecovered() + "\n");
                }

                if (currentPlayer.getPosition().equals("CB") || (currentPlayer.getPosition().equals("SS")) || (currentPlayer.getPosition().equals("FS"))) {
                    playerStats.setText("Position: " + currentPlayer.getPosition() + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");
                    playerStats.setText(playerStats.getText() + "Tackles: " + currentPlayer.getTackles() + "\n");
                    playerStats.setText(playerStats.getText() + "Sacks: " + currentPlayer.getSacks() + "\n");
                    playerStats.setText(playerStats.getText() + "Fumbles Forced: " + currentPlayer.getFumblesForced() + "\n");
                    playerStats.setText(playerStats.getText() + "Fumbles Recovered: " + currentPlayer.getFumblesRecovered() + "\n");
                    playerStats.setText(playerStats.getText() + "Interceptions: " + currentPlayer.getInterceptions());
                }
                if (currentPlayer.getPosition().equals("K")) {
                    playerStats.setText("Position: " + currentPlayer.getPosition() + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");
                    playerStats.setText(playerStats.getText() + "Field Goals Made: " + currentPlayer.getFieldGoalsMade() + "\n");
                }

                if (currentPlayer.getPosition().equals("P")) {
                    playerStats.setText("Position: " + currentPlayer.getPosition() + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");
                    playerStats.setText(playerStats.getText() + " Punt Yards: " + currentPlayer.getPuntYards() + "\n");
                }

                if (currentPlayer.getPosition().equals("PR")) {
                    playerStats.setText("Position: " + "Punt Returner" + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");
                    playerStats.setText(playerStats.getText() + " Punt Return Yards: " + currentPlayer.getPuntReturnYards() + "\n");
                }

                if (currentPlayer.getPosition().equals("KR")) {
                    playerStats.setText("Position: " + currentPlayer.getPosition() + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");
                    playerStats.setText(playerStats.getText() + " Kick Return Yards: " + currentPlayer.getKickReturnYards() + "\n");
                }

                if (currentPlayer.getPosition().equals("OT") || currentPlayer.getPosition().equals("C") || currentPlayer.getPosition().equals("LS")) {
                    playerStats.setText("Position: " + currentPlayer.getPosition() + "\n");
                    playerStats.setText(playerStats.getText() + "Team: " + currentPlayer.getTeam() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Played: " + currentPlayer.getGamesPlayed() + "\n");
                    playerStats.setText(playerStats.getText() + "Games Started: " + currentPlayer.getGamesStarted() + "\n");

                }


                firstName.setText(rosterPlayer.getFirstName());
                lastName.setText(rosterPlayer.getSecondName());
                loadFromUflr(rosterPlayer.getPhotoURL());
                playerImage.getLayoutParams().height = 200;
                playerImage.getLayoutParams().width = 200;

                /*if(preference.getFavoritePosition().equals(currentPlayer.getPosition())){
                    preferences.setText("You will like this player.");
                }else{
                    preferences.setText("You will not like this player.");
                }
                */

                moreInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse("https://en.wikipedia.org/wiki/"+firstName.getText()+"_"+lastName.getText()+"_"));
                        startActivity(intent);
                    }
                });



            }else{
                Toast.makeText(fragmentView.getContext(),"Player was not found",Toast.LENGTH_SHORT).show();

            }




        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    public void loadFromUflr(String url){
        Picasso.with(this.getContext()).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(playerImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }



}
