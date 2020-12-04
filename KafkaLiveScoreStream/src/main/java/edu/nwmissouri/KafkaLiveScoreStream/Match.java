package edu.nwmissouri.KafkaLiveScoreStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Match
{
	Venue venue;

	String bat_team_name;
	String bowl_team_name;

	String SeriesName;
	String MatchID;
	String SeriesID;
	String DataPath;
	String StartTime;
	String EndTime;
	String MatchDescription;
	String Type;
	String State;
	String StateTitle;
	String Toss;
	String Status;

	String scorecard;

	JSONObject mData;
	JSONObject header;

	Team Team1;
	Team Team2;

	HashMap<Integer, Innings> InningsHashMap = new HashMap<>();


	HashMap<String, String> TeamIDData;

	public Match(JSONObject matchData) throws JSONException
	{
		mData = matchData;
		header = matchData.getJSONObject("header");

		TeamIDData = new HashMap<String, String>();

		JSONObject team1  = matchData.getJSONObject("team1");
		JSONObject team2  = matchData.getJSONObject("team2");

		TeamIDData.put(team1.getString("id"),team1.getString("name"));
		TeamIDData.put(team2.getString("id"),team2.getString("name"));

		Team1 = new Team(matchData.getJSONObject("team1"));
		Team2 = new Team(matchData.getJSONObject("team2"));

		venue = new Venue(matchData.getJSONObject("venue"));
		//venue.print();

		SeriesName = get("series_name");
		MatchID = get("match_id");
		SeriesID = get("series_id");
		DataPath = get("data_path");

		StartTime = geth("start_time");
		EndTime = geth("end_time");
		State = geth("state");
		StateTitle = geth("state_title");
		Toss = geth("toss");
		Type = geth("type");
		MatchDescription = geth("match_desc");
		Status = geth("status");
		ParseScoreCard();
	}
	private String geth(String n) throws JSONException
	{
		return header.getString(n);
	}
	private String get(String n) throws JSONException
	{
		return mData.getString(n);
	}
	public String getScoreCard()
	{
		return scorecard;
	}
	public void ParseScoreCard() throws JSONException
	{
		StringBuilder score = new StringBuilder();

		if(!isMatchStarted() || !mData.has("bat_team") || !mData.has("bow_team"))
		{
			scorecard = "Match not started :  " + Toss;
			return;
		}
		JSONObject battingTeam = mData.getJSONObject("bat_team");
		JSONObject bowlingTeam = mData.getJSONObject("bow_team");

		String BattingTeamName = getTeamNameFromId(battingTeam.getString("id"));
		bat_team_name = BattingTeamName;

		String BowlingTeamName = getTeamNameFromId(bowlingTeam.getString("id"));
		bowl_team_name = BowlingTeamName;

		HashMap<Integer, Innings> InningsToBeSorted = new HashMap<>();

		JSONArray BattingTeamInnings = battingTeam.getJSONArray("innings");

		for(int i = 0; i < BattingTeamInnings.length(); i ++)
		{
			JSONObject inn = BattingTeamInnings.getJSONObject(i);
			String id = inn.getString("id");
			Innings innToBeAdded = new Innings(inn, BattingTeamName);
			InningsToBeSorted.put(new Integer(id), innToBeAdded);
		}

		JSONArray BowlingTeamInnings = bowlingTeam.getJSONArray("innings");

		for(int i = 0; i < BowlingTeamInnings.length(); i ++)
		{
			JSONObject inn = BowlingTeamInnings.getJSONObject(i);
			String id = inn.getString("id");
			Innings innToBeAdded = new Innings(inn, BowlingTeamName);
			InningsToBeSorted.put(new Integer(id), innToBeAdded);
		}
		for(int i = 0; i < InningsToBeSorted.size(); i++)
		{
			Innings inn = InningsToBeSorted.get(new Integer(i+1));
			score.append(/*i+1 + ". " + */inn.getName() + " "+ inn.getInnscore() + "/"+ inn.getWickets() + " in " + inn.getOvers() + " overs\n");
		}


		InningsHashMap = InningsToBeSorted;
		scorecard = score.toString();
	}
	public HashMap<Integer, Innings> getInnings()
	{
		return InningsHashMap; //use getInnings in your RecyclerView to get Innings and
	}
	private Boolean isMatchStarted()
	{
		if(Toss.equals(""))
		{
			return false;
		}
		else return true;
	}
	private String getTeamNameFromId(String Id)
	{
		if(TeamIDData.containsKey(Id))
		{
			return TeamIDData.get(Id);
		}
		else
		{
			return "Error";
		}
	}
	public Commentary getCommentary(){
		return CricBuzzParser.RetrieveCommentary(this);
	}

	public HashMap<String, String> getTeamIDData() {
		return TeamIDData;
	}

	public String getBat_team_name() {
		return bat_team_name;
	}

	public String getBowl_team_name() {
		return bowl_team_name;
	}

	public String getSeriesName() {
		return SeriesName;
	}

	public String getMatchID() {
		return MatchID;
	}

	public String getSeriesID() {
		return SeriesID;
	}

	public String getDataPath() {
		return DataPath;
	}

	public String getStartTime() {
		return StartTime;
	}

	public String getEndTime() {
		return EndTime;
	}

	public String getMatchDescription() {
		return MatchDescription;
	}

	public String getType() {
		return Type;
	}

	public String getState() {
		return State;
	}

	public String getStateTitle() {
		return StateTitle;
	}

	public String getToss() {
		return Toss;
	}

	public String getStatus() {
		return Status;
	}

	public String getScorecard() {
		return scorecard;
	}

	public JSONObject getmData() {
		return mData;
	}

	public JSONObject getHeader() {
		return header;
	}

	public Team getTeam1() {
		return Team1;
	}

	public Team getTeam2() {
		return Team2;
	}

	public Venue getVenue() {
		return venue;
	}
}

