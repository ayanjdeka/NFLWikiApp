package wiki.nfl.ayan.nflwikiapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wiki.nfl.ayan.nflwikiapp.objects.NFLNews;
import wiki.nfl.ayan.nflwikiapp.objects.NFLPlayer;
import wiki.nfl.ayan.nflwikiapp.objects.NFLTeam;
import wiki.nfl.ayan.nflwikiapp.objects.RosterPlayer;

public class NFLRestAPIHelper {

    private String apiKey = "7351efcb4e1b456093533222d31a874d";


    public static void main(String[] args) throws Exception {

        NFLRestAPIHelper helper = new NFLRestAPIHelper();
        helper.getAllTeams();

    }

    public Map<String,String> getAllPlayerMapFromFile(Context context) {
        JSONArray array = null;
        Map<String, String> m = new HashMap<String, String>();
        try {
            InputStream inputStream = context.openFileInput("players.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                String ret = stringBuilder.toString();
                array = new JSONArray(ret);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String name = obj.getString("name");
                    String id = obj.getString("id");
                    m.put(name, id);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return m;
    }

    public void writeToFile(Map<String, String> data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(context.openFileOutput("players.json", Context.MODE_PRIVATE));
            Collection<String> keys = data.keySet();
            JSONArray arr = new JSONArray();
            JSONObject json = new JSONObject();
            for (String key : keys) {
                String value = data.get(key);
                json = new JSONObject();
                json.put("name", key);
                json.put("id", value);
                arr.put(json);
            }
            System.out.println(arr.toString());
            outputStreamWriter.write(arr.toString());
            outputStreamWriter.close();
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public Map<String, String> getAllPlayersMap() throws Exception {
        Map<String, String> playerMap = new HashMap<>();
        Map<String, String> teamsMap = new HashMap<>();
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
        teamsMap.put("IND", "Indianapolis");
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
        teamsMap.put("PIT", "Pittsburge Steelers");
        teamsMap.put("SEA", "Seatle Seahawks");
        teamsMap.put("SF", "San Francisco 49ers");
        teamsMap.put("TB", "Tamba Bay Buccaneers");
        teamsMap.put("TEN", "Tennesee Titans");
        teamsMap.put("WAS", "Washington Redskins");
        Collection<String> keyList = teamsMap.keySet();
        for (String k : keyList) {
            List<RosterPlayer> list = getRosterPlayers(k);
            for (RosterPlayer p : list) {
                playerMap.put(p.getFirstName() + " " + p.getSecondName(), p.getPlayerId());
            }
        }
        //write to json
        return playerMap;
    }

    public List<NFLTeam> getAllTeams() throws Exception {
        List<NFLTeam> allTeams = new ArrayList<>();
        String url = "https://api.sportsdata.io/v3/nfl/scores/json/AllTeams?key="+apiKey;
        JSONArray arr = getURLDataArray(url);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            NFLTeam team = new NFLTeam();
            team.setKey(obj.getString("Key"));
            team.setConference(obj.getString("Conference"));
            team.setName(obj.getString("Name"));
            team.setDivision(obj.getString("Division"));
            team.setName(obj.getString("Name"));
            team.setImageURL(obj.getString("WikipediaLogoUrl"));

            allTeams.add(team);
        }

        return allTeams;
    }

    public NFLPlayer getPlayerById(String id) throws Exception {
        NFLPlayer player = null;
        JSONArray arr = getURLDataArray("https://api.sportsdata.io/v3/nfl/stats/json/PlayerSeasonStatsByPlayerID/2018REG/" + id + "?key="+apiKey);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            player = new NFLPlayer();
            player.setTeam(obj.getString("Team"));
            player.setPlayerName(obj.getString("Name"));
            player.setPlayerId(obj.getString("PlayerID"));
            player.setPosition(obj.getString("Position"));
            player.setGamesPlayed(obj.getString("Played"));
            player.setGamesStarted(obj.getString("Started"));
            player.setFieldGoalsMade(obj.getString("FieldGoalsMade"));
            player.setPuntYards(obj.getString("Punts"));
            player.setPuntReturnYards(obj.getString("PuntReturnYards"));
            player.setKickReturnYards(obj.getString("KickReturnYards"));
            player.setInterceptions(obj.getString("Interceptions"));
            player.setPasserRating(obj.getString("PassingRating"));
            player.setPassingTouchDowns(obj.getString("PassingTouchdowns"));
            player.setPassingYards(obj.getString("PassingYards"));
            player.setReceivingTouchDowns(obj.getString("ReceivingTouchdowns"));
            player.setReceivingYards(obj.getString("ReceivingYards"));
            player.setReceptions(obj.getString("Receptions"));
            player.setRushingAttempts(obj.getString("RushingAttempts"));
            player.setRushingTouchDowns(obj.getString("RushingTouchdowns"));
            player.setRushingYards(obj.getString("RushingYards"));
            player.setTackles(obj.getString("Tackles"));
            player.setSacks(obj.getString("Sacks"));
            player.setFumbles(obj.getString("Fumbles"));
            player.setFumblesForced(obj.getString("FumblesForced"));
            player.setFumblesRecovered(obj.getString("FumblesRecovered"));
        }
        return player;
    }


    public List<NFLNews> getTeamNews(String team) throws Exception {
        List<NFLNews> allNews = new ArrayList<>();
        JSONArray arr = getURLDataArray("https://api.sportsdata.io/v3/nfl/scores/json/NewsByTeam/" + team + "?key="+apiKey);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            NFLNews news = new NFLNews();

            news.setContent(obj.getString("Content"));
            news.setTitle(obj.getString("Title"));
            news.setTeam(obj.getString("Team"));
            allNews.add(news);
        }
        return allNews;
    }

    public List<RosterPlayer> getRosterPlayers(String team) throws Exception {
        List<RosterPlayer> rosters = new ArrayList<>();
        JSONArray arr = getURLDataArray("https://api.fantasydata.net/v3/nfl/stats/JSON/Players/" + team + "?key="+apiKey);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            RosterPlayer roster = new RosterPlayer();

            roster.setPlayerId(obj.getString("PlayerID"));
            roster.setAge(obj.getString("Age"));
            roster.setFirstName(obj.getString("FirstName"));
            roster.setSecondName(obj.getString("LastName"));
            roster.setPosition(obj.getString("Position"));
            roster.setTeam(obj.getString("Team"));
            roster.setPhotoURL(obj.getString("PhotoUrl"));
            roster.setHeight(obj.getString("Height"));
            roster.setWeight(obj.getString("Weight"));

            rosters.add(roster);
        }

        return rosters;

    }

    public JSONObject getURLDataObject(String urlString) {

        JSONObject object = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Ocp-Apim-Subscription-Key", apiKey);


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            StringBuilder builder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                builder.append(output);
            }
            object = new JSONObject(output);

            conn.disconnect();
            conn = null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;

    }


    public JSONArray getURLDataArray(String urlString) {

        JSONArray arr = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Ocp-Apim-Subscription-Key", apiKey);


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            StringBuilder builder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                builder.append(output);
            }
            arr = new JSONArray(builder.toString());

            conn.disconnect();
            conn = null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arr;

    }

}
