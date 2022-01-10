package wiki.nfl.ayan.nflwikiapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wiki.nfl.ayan.nflwikiapp.objects.NFLPlayer;
import wiki.nfl.ayan.nflwikiapp.objects.RosterPlayer;

public class ComparePlayersFragment extends Fragment {

    EditText enterPlayer1;
    EditText enterPlayer2;
    Button comparePlayers;
    ImageView player1Image;
    ImageView player2Image;
    TextView name1;
    TextView name2;
    TextView position1;
    TextView position2;
    TextView height1;
    TextView height2;
    TextView weight1;
    TextView weight2;
    TextView team1;
    TextView team2;
    View fragmentView;
    ConstraintLayout layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView= inflater.inflate(R.layout.fragment_compareplayers,null);

        enterPlayer1 = fragmentView.findViewById(R.id.id_editText_EnterPlayer1);
        enterPlayer2 = fragmentView.findViewById(R.id.id_editText_player2);
        comparePlayers = fragmentView.findViewById(R.id.id_button_search);
        player1Image = fragmentView.findViewById(R.id.id_imageView_Player1image);
        player2Image = fragmentView.findViewById(R.id.id_imageView_Player2);
        name1 = fragmentView.findViewById(R.id.id_textView_namePlayer1);
        name2 = fragmentView.findViewById(R.id.id_textView_namePlayer2);
        position1 = fragmentView.findViewById(R.id.id_textView_positionPlayer1);
        position2 = fragmentView.findViewById(R.id.id_textView_positionPlayer2);
        height1 = fragmentView.findViewById(R.id.id_textView_player1Height);
        height2 = fragmentView.findViewById(R.id.id_textView_player2Height2);
        weight1 = fragmentView.findViewById(R.id.id_textView_player1Weight);
        weight2 = fragmentView.findViewById(R.id.id_textView_player2Weight);
        team1 = fragmentView.findViewById(R.id.id_textView_player1Team);
        team2 = fragmentView.findViewById(R.id.id_textView_player2Team);
        layout = fragmentView.findViewById(R.id.id_layout);
        layout.getBackground().setAlpha(51);

        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        bindView();

        return fragmentView;
    }

    public void bindView() {

        comparePlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText =enterPlayer1.getText().toString();
                String otherSearchText = enterPlayer2.getText().toString();
                new MyAsyncTask(getActivity(), fragmentView).execute(searchText, otherSearchText);

            }
        });

    }

    private static Map<String,String> nameMap = new HashMap<>();

    class MyAsyncTask extends AsyncTask<String, String, String> {
        View myView;
        Activity mContex;


        NFLPlayer currentPlayer;
        NFLPlayer currentPlayer2;

        RosterPlayer rosterPlayer;
        RosterPlayer rosterPlayer2;


        public MyAsyncTask(Activity contex, View v) {
            this.myView = v;
            this.mContex = contex;
        }

        @Override
        protected String doInBackground(String... parameters) {
            NFLRestAPIHelper helper = new NFLRestAPIHelper();
            if(parameters.length > 1 ){

                String playerSearch1 = parameters[0];
                String playerSearch2 = parameters[1];
                try {
                    if(nameMap.isEmpty()){
                        nameMap = helper.getAllPlayersMap();
                        helper.writeToFile(nameMap,mContex);
                        nameMap = helper.getAllPlayerMapFromFile(mContex);
                    }

                    Collection<String> keys = nameMap.keySet();
                    String id = null;
                    String id2 = null;
                    for (String name : keys) {
                        if(name.toLowerCase().contains(playerSearch1.toLowerCase().trim())){
                            id = nameMap.get(name);
                            break;
                        }
                    }

                    for (String name : keys) {
                        if(name.toLowerCase().contains(playerSearch2.toLowerCase().trim())){
                            id2 = nameMap.get(name);
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

                    currentPlayer2 = helper.getPlayerById(id2);
                    if(currentPlayer2 != null){
                        String team2 = currentPlayer2.getTeam();
                        List<RosterPlayer> rosterPlayers2 = helper.getRosterPlayers(team2);
                        for (RosterPlayer r: rosterPlayers2){
                            if(r.getPlayerId().equals(currentPlayer2.getPlayerId())){
                                rosterPlayer2 = r;
                                break;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("No Value Set");
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if(currentPlayer != null){
                name1.setText("Name: "+" "+rosterPlayer.getFirstName()+" "+rosterPlayer.getSecondName());
                loadFromUlr1(rosterPlayer.getPhotoURL());
                if(currentPlayer.getPosition().equals("QB") && currentPlayer2.getPosition().equals("QB")) {
                    height1.setText("Yards Thrown " + currentPlayer.getPassingYards());
                    weight1.setText("Passer Rating " + currentPlayer.getPasserRating());
                }
                position1.setText("Position "+currentPlayer.getPosition());
                team1.setText("Team "+currentPlayer.getTeam());
            }else{
                Toast.makeText(fragmentView.getContext(),"Please Enter A First Name",Toast.LENGTH_SHORT).show();

            }
            if(currentPlayer2 != null){
                name2.setText("Name: "+rosterPlayer2.getFirstName()+" "+rosterPlayer2.getSecondName());
                secondLoadFromUrl(rosterPlayer2.getPhotoURL());
                if(currentPlayer.getPosition().equals("QB") && currentPlayer2.getPosition().equals("QB")) {
                    height2.setText("Yards Thrown " + currentPlayer2.getPassingYards());
                    weight2.setText("Passer Rating " + currentPlayer2.getPasserRating());
                }
                position2.setText("Position "+currentPlayer2.getPosition());
                team2.setText("Team "+currentPlayer2.getTeam());

            }else{
                Toast.makeText(fragmentView.getContext(),"Please Enter A Second Name",Toast.LENGTH_SHORT).show();
            }

            if(currentPlayer == null && currentPlayer2 == null){
                Toast.makeText(fragmentView.getContext(),"Please Enter A First And Second Name",Toast.LENGTH_SHORT).show();
            }


        }

    }




    public void loadFromUlr1(String url){
        Picasso.with(this.getContext()).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(player1Image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    public void secondLoadFromUrl(String url){
        Picasso.with(this.getContext()).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(player2Image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }



}
