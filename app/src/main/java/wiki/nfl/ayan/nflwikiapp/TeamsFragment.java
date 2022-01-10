package wiki.nfl.ayan.nflwikiapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wiki.nfl.ayan.nflwikiapp.objects.NFLPreference;
import wiki.nfl.ayan.nflwikiapp.objects.NFLTeam;
import wiki.nfl.ayan.nflwikiapp.objects.RosterPlayer;
import wiki.nfl.ayan.nflwikiapp.objects.SVGToImageUtil;

public class TeamsFragment extends Fragment {

    EditText enterTeams;
    Button searchTeams;
    TextView division;
    TextView conference;
    TextView importantPlayersNames;
    TextView importantPlayersAges;
    TextView importantPlayersPositions;
    ListView listView;
    ConstraintLayout layout;
    List<RosterPlayer> teamRosterList = new ArrayList<RosterPlayer>();


    ImageView teamImage;
    String acronyms;

    View fragmentView = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_teams, null);

        enterTeams = fragmentView.findViewById(R.id.id_editText_EnterTeam);
        searchTeams = fragmentView.findViewById(R.id.id_button_searchTeams);
        division = fragmentView.findViewById(R.id.id_textView_division);
        conference = fragmentView.findViewById(R.id.id_textView_conferences);
        listView = fragmentView.findViewById(R.id.id_listViewForNews);
        teamImage = fragmentView.findViewById(R.id.id_imageView_teamImage);
        layout = fragmentView.findViewById(R.id.id_layout);
        layout.getBackground().setAlpha(51);

        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);




        bindView();
        return fragmentView;
    }

    public void bindView() {

        String searchText = enterTeams.getText().toString();
        if(searchText.toString().equals("Enter a Team")){
                new MyAsyncTask(getActivity(), fragmentView).execute();
        }

        searchTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = enterTeams.getText().toString();

                division.setText("Division: ");
                conference.setText("Conference: ");

                new MyAsyncTask(getActivity(), fragmentView).execute(searchText);
            }
        });


    }

    NFLTeam currentNFLTeam;
    List<NFLTeam> allTeams = new ArrayList<>();
    List<RosterPlayer> teamRoster = new ArrayList<RosterPlayer>();
    NFLPreference preference = null;

    class MyAsyncTask extends AsyncTask<String, String, String> {
        View myView;
        Activity mContex;




        public MyAsyncTask(Activity contex, View v) {
            this.myView = v;
            this.mContex = contex;
        }

        @Override
        protected String doInBackground(String... params) {
            NFLRestAPIHelper helper = new NFLRestAPIHelper();
            String team = null;

            try {

                if (allTeams.isEmpty()) {
                    allTeams = helper.getAllTeams();
                }
                if (params.length > 0) {
                    team = params[0];
                }else {
                    NFLFireBaseHelper fbHelper = new NFLFireBaseHelper();
                    preference = fbHelper.getNFLPreference();
                    team = preference.getFavoriteTeam();

                }
                if(team != null){
                    for (NFLTeam t : allTeams) {
                        if (t.getName().toLowerCase().contains(team.toLowerCase())
                               || t.getKey().toLowerCase().contains(team.toLowerCase())) {
                            currentNFLTeam = t;
                        }
                    }
                }
                if (currentNFLTeam != null) {
                    String teamId = currentNFLTeam.getKey();
                    if(currentNFLTeam.getName().toLowerCase().contains("cardinals")){
                        teamRoster = helper.getRosterPlayers("ARI");
                    }else if(currentNFLTeam.getName().toLowerCase().contains("rams")) {
                        teamRoster = helper.getRosterPlayers("LAR");
                    }else{
                       teamRoster = helper.getRosterPlayers(teamId);
                    }

                }

            } catch (Exception e) {
                // show error here
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            String searchText = enterTeams.getText().toString().toLowerCase();


            if (searchText != null) {
                for (int i = 0; i < allTeams.size(); i++) {
                    String teamName = allTeams.get(i).getName().toLowerCase();
                    String teamKey = allTeams.get(i).getName().toLowerCase();


                    if (teamName.contains(searchText) || teamKey.contains(searchText)) {
                        currentNFLTeam = allTeams.get(i);
                        break;
                    }
                }
            } else {
                for (NFLTeam team : allTeams) {
                    if(team.getKey().equals(preference.getFavoriteTeam())){
                        currentNFLTeam = team;
                    }
                }
            }

            if(currentNFLTeam != null && enterTeams.getText().toString().length()>=3){
                System.out.println(currentNFLTeam.getImageURL());
                division.setText("Division:" + " " + currentNFLTeam.getDivision());
                conference.setText("Conference:" + " " + currentNFLTeam.getConference());
                loadImageFromUrl(currentNFLTeam.getImageURL());
            }else{
                Toast.makeText(fragmentView.getContext(),"Please Enter A Team",Toast.LENGTH_SHORT).show();
            }
            CustomAdapter customAdapter = new CustomAdapter(fragmentView.getContext(), R.layout.custom_layout, teamRoster);
            listView.setAdapter(customAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });


        }

    }


    private void loadImageFromUrl(String urlStr){
        try {
            SVGToImageUtil.fetchSvg(this.getContext(), urlStr, teamImage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class CustomAdapter extends ArrayAdapter<RosterPlayer> {
        Context context;
        int resource;
        List<RosterPlayer> list;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<RosterPlayer> objects) {
            super(context,resource,objects);
            this.context = context;
            this.resource = resource;
            list = objects;

        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterView = layoutInflater.inflate(resource,null);


            TextView name = adapterView.findViewById(R.id.id_textView_player);
            TextView age = adapterView.findViewById(R.id.id_textView_age);
            TextView pos = adapterView.findViewById(R.id.id_textView_position);
            name.setText(list.get(position).getFirstName()+" "+list.get(position).getSecondName());
            age.setText(list.get(position).getAge());
            pos.setText(list.get(position).getPosition());

            return adapterView;
        }
    }


}
