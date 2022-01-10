package wiki.nfl.ayan.nflwikiapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wiki.nfl.ayan.nflwikiapp.objects.NFLPreference;

public class Preferences extends Fragment {

    View view;
    Button save;
    Spinner favTeams;
    Spinner favPositions;
    ArrayList<String> positions;
    ArrayList<String> teams;
    ArrayAdapter arrayAdapterForTeam;
    ArrayAdapter arrayAdapterForPosition;
    Map<String,String> teamsMap = new HashMap<>();
    Map<String,String> positionsMap = new HashMap<>();
    ConstraintLayout layout;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.preferencespage,null);

        save = view.findViewById(R.id.id_button_save);
        favTeams = view.findViewById(R.id.id_spinnerFavTeam);
        favPositions = view.findViewById(R.id.id_spinnerFavPos);
        layout = view.findViewById(R.id.id_preferencesLayout);
        positions = new ArrayList<>();
        teams = new ArrayList<>();

        layout.getBackground().setAlpha(100);


        positionsMap.put("C", "Center");
        positionsMap.put("QB", "Quarterback");
        positionsMap.put("OT","Offensive Tackle");
        positionsMap.put("RB", "Running Back");
        positionsMap.put("WR", "Wide Receiver");
        positionsMap.put("TE", "Tight End");
        positionsMap.put("DT", "Defensive Tackle");
        positionsMap.put("DE", "Defensive End");
        positionsMap.put("LB", "Line Backer");
        positionsMap.put("OLB", "Outside Linebacker");
        positionsMap.put("SS", "Strong Safety");
        positionsMap.put("CB", "Corner Back");
        positionsMap.put("FS", "Free Safety");
        positionsMap.put("P", "Punter");
        positionsMap.put("KR", "Kick Returner");
        positionsMap.put("PR", "Punt Returner");
        positionsMap.put("K", "Kicker");
        positionsMap.put("LS", "Long Snapper");

        teamsMap.put("ARI", "Arizona Cardinals");
        teamsMap.put("ATL", "Atlanta Falcons");
        teamsMap.put("BAL", "Baltimore Ravens");
        teamsMap.put("BUF", "Buffalo Bills");
        teamsMap.put("CAR", "Carolina Panthers");
        teamsMap.put("CHI", "Chicago Bears");
        teamsMap.put("CIN", "Cinncinati Bengals");
        teamsMap.put("CLE", "Cleveland Browns");
        teamsMap.put("DEN", "Denver Broncos");
        teamsMap.put("DET", "Detroit Lions");
        teamsMap.put("GB", "Green Bay Packers");
        teamsMap.put("HOU", "Houston Texans");
        teamsMap.put("IND", "Indianapolis Colts");
        teamsMap.put("JAX", "Jacksonville Jaguars");
        teamsMap.put("KC", "Kansas City Chiefs");
        teamsMap.put("LAC", "Los Angeles Chargers");
        teamsMap.put("LAR", "Los Angeles Rams");
        teamsMap.put("LARAID", "Los Angeles Raiders");
        teamsMap.put("MIA", "Miami Dolphins");
        teamsMap.put("MIN", "Minnesota Vikings");
        teamsMap.put("NE", "New England Patriots");
        teamsMap.put("NO", "New Orleans Sains");
        teamsMap.put("NYG", "New York Giants");
        teamsMap.put("NYJ", "New York Jets");
        teamsMap.put("OAK", "Oakland Raiders");
        teamsMap.put("PHI", "Philadelphia Eagles");
        teamsMap.put("PIT", "Pittsburgh Steelers");
        teamsMap.put("SEA", "Seatle Seahawks");
        teamsMap.put("SF", "San Francisco 49ers");
        teamsMap.put("TB", "Tampa Bay Buccaneers");
        teamsMap.put("TEN", "Tennessee Titans");
        teamsMap.put("WAS", "Washington Redskins");
        teams.addAll(teamsMap.values());

        arrayAdapterForTeam = new ArrayAdapter(view.getContext(),R.layout.support_simple_spinner_dropdown_item,teams);
        favTeams.setAdapter(arrayAdapterForTeam);

        positions.addAll(positionsMap.values());
        arrayAdapterForPosition = new ArrayAdapter(view.getContext(),R.layout.support_simple_spinner_dropdown_item,positions);
        favPositions.setAdapter(arrayAdapterForPosition);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedTeam = favTeams.getSelectedItem().toString();
                String selectedPositions = favPositions.getSelectedItem().toString();

                NFLPreference preference = new NFLPreference();
                for (Map.Entry t: teamsMap.entrySet()) {
                    if(t.getValue().equals(selectedTeam)){
                        preference.setFavoriteTeam(t.getKey().toString());
                        NFLFireBaseHelper helper = new NFLFireBaseHelper();
                        helper.savePreferences(preference);
                        break;
                    }
                }

                for (Map.Entry t: positionsMap.entrySet()) {
                    if(t.getValue().equals(selectedPositions)){
                        preference.setFavoritePosition(t.getKey().toString());
                        NFLFireBaseHelper helper = new NFLFireBaseHelper();
                        helper.savePreferences(preference);
                        break;
                    }
                }

                Toast.makeText(view.getContext(), "Saved Your Preferences!", Toast.LENGTH_SHORT).show();

            }
        });




        return view;
    }
}
