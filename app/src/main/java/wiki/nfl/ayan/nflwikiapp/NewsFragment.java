package wiki.nfl.ayan.nflwikiapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import wiki.nfl.ayan.nflwikiapp.objects.NFLNews;
import wiki.nfl.ayan.nflwikiapp.objects.NFLPreference;
import wiki.nfl.ayan.nflwikiapp.objects.NFLTeam;
import wiki.nfl.ayan.nflwikiapp.objects.RosterPlayer;

public class NewsFragment extends Fragment {

    EditText enterTeamForNews;
    Button searchForNews;
    TextView textForNews;
    ListView listView;
    ConstraintLayout layout;


    View fragmentView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_news, null);

        enterTeamForNews = fragmentView.findViewById(R.id.id_editText_EnterTeamForNews);
        enterTeamForNews.setTextColor(Color.WHITE);
        searchForNews = fragmentView.findViewById(R.id.id_button_searchNews);
        textForNews = fragmentView.findViewById(R.id.id_textView_newsTeam);
        listView = fragmentView.findViewById(R.id.id_listViewForNews);
        layout = fragmentView.findViewById(R.id.id_layout);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        layout.getBackground().setAlpha(100);


        bindView();

        return fragmentView;

    }

    public void bindView() {

        new MyAsyncTask(getActivity(), fragmentView).execute();

        searchForNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = enterTeamForNews.getText().toString();
                new MyAsyncTask(getActivity(), fragmentView).execute(searchText);
            }
        });


    }

    NFLTeam currentNFLTeam;
    List<NFLTeam> allTeams = new ArrayList<>();
    List<NFLNews> teamNews = new ArrayList<NFLNews>();
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
                    for (NFLTeam t : allTeams) {
                        if (t.getName().toLowerCase().contains(team.toLowerCase())  ||
                                t.getKey().toLowerCase().contains(team.toLowerCase())
                        ) {
                            currentNFLTeam = t;
                        }
                    }

                if (currentNFLTeam != null) {
                    String teamId = currentNFLTeam.getKey();
                    if(currentNFLTeam.getName().toLowerCase().contains("cardinals")){
                        teamNews = helper.getTeamNews("ARI");
                    }else if(currentNFLTeam.getName().toLowerCase().contains("rams")) {
                        teamNews = helper.getTeamNews("LAR");
                    }else{
                        teamNews = helper.getTeamNews(teamId);
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

            if(currentNFLTeam!=null) {
                CustomAdapter customAdapter = new CustomAdapter(fragmentView.getContext(), R.layout.newscustom_layout, teamNews);
                listView.setAdapter(customAdapter);
            }else{
                Toast.makeText(fragmentView.getContext(),"Please Enter A Team",Toast.LENGTH_SHORT).show();

            }

        }





    }

    public class CustomAdapter extends ArrayAdapter<NFLNews> {
        Context context;
        int resource;
        List<NFLNews> list;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<NFLNews> objects) {
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


            TextView news = adapterView.findViewById(R.id.id_newsForTeam);

            news.setText(list.get(position).getContent());
            textForNews.setText("News For: "+list.get(position).getTitle());

            return adapterView;
        }
    }
}
