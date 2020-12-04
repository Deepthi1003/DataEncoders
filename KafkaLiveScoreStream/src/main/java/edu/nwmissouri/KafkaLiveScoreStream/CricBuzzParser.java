package edu.nwmissouri.KafkaLiveScoreStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class CricBuzzParser {
    String url;

    public ArrayList<Match> getMatches() {
        return Matches;
    }

    String resp;
    ArrayList<Match> Matches;

    public CricBuzzParser(String url) {
        this.url = url;
        Matches = new ArrayList<>();
    }

    public void RetrieveURL() {
        resp = getContentsOfURL(this.url);
    }

    private static String getContentsOfURL(String u) {
        try {
            URL url = new URL(u);

            URLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder responseString = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }
            String responseJsonString = responseString.toString();

            return responseJsonString;
        } catch (Exception except) {
            except.printStackTrace();
            return null;
        }
    }

    private static String getCommentaryURL(String mid) {
        String urlCommentary = "http://mapps.cricbuzz.com/cbzios/match/" + mid + "/commentary";
        return urlCommentary;
    }

    public static Commentary RetrieveCommentary(Match m) {
        String comm = getContentsOfURL(getCommentaryURL(m.getMatchID()));
        JSONObject jo;

        try {
            Commentary c = new Commentary(new JSONObject(comm));
            return c;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void putResult(String result) {
        this.resp = result;
    }

    public Boolean Parse() {
        JSONObject jo;

        try {
            jo = new JSONObject(resp);
            JSONArray MatchesList = jo.getJSONArray("matches");
            int i;
            int j = MatchesList.length();
            for (i = 0; i < j; i++) {
                JSONObject individualMatch = MatchesList.getJSONObject(i);
                Match m = new Match(individualMatch);
                Matches.add(m);
                String scorecard = m.getScoreCard();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String Parse1() {
        JSONObject jo;
        jo = new JSONObject(resp);

        JSONArray MatchesList = jo.getJSONArray("matches");
        int i;
        int j = MatchesList.length();
        for (i = 0; i < j; i++) {
            JSONObject individualMatch = MatchesList.getJSONObject(i);
            Match m = new Match(individualMatch);
            Matches.add(m);
            String scorecard = m.getScoreCard();
        }

        String checkString = MatchesList.get(0).toString();
        System.out.println(checkString);
        return checkString;
    }
}
