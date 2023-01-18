package admin;

import java.net.*;
import java.util.ArrayList;
import java.io.*;  

public class StatCollector {

    Database db;
    int leagueID; 
    long lastMatchID;
    String apiKey;

    public StatCollector(Database db, int l, long la, String a) {
        this.db = db; leagueID= l;
        lastMatchID = la; apiKey = a;
    }


    class GameToProcess {
        public long matchID;
        public boolean radiant, dire;

        public GameToProcess(long l, boolean r, boolean d) {
            matchID = l; radiant = r; dire = d;
        }

        public String getLink() {
            return "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?match_id="+matchID+"&key="+apiKey;
        }
    }

    /**
     * Searches through the match history of the league provided until it 
     * reaches lastMatchID, and adds results to the databse. Only concerns itself
     * with games from teams that already exist in the databse, so we need to 
     * add teams before this.
     * 
     * @param db our database
     * @param leagueID the ID of the league so we can hit the steam API
     * @param lastMatchID the last match we looked at
     * @return the amount of games we looked at, probably
     */
    public int update() {
        String link = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?league_id="+leagueID+"&key="+apiKey;
        ArrayList<GameToProcess> matches;
        try {
            matches = getLeagueMatches(db, link, lastMatchID);
        } catch (Exception e) { 
            e.printStackTrace();
            matches = new ArrayList<>(); 
        }

        for (GameToProcess i : matches) {
            System.out.println("Processing "+i.matchID + " with radiant:dire = "+i.radiant+":"+i.dire);
            if (!processGameResult(i)) {
                System.out.println("Error on game "+ i.matchID);
            }
        }

        return matches.size();
    }

    public ArrayList<GameToProcess> getLeagueMatches(Database db, String link, long lastMatchID) throws Exception {
        ArrayList<GameToProcess> matches = new ArrayList<>();
        ArrayList<Integer> teams = getTeamsInDB(db);

        URL url = new URL(link); // creating a url object  
        URLConnection urlConnection = url.openConnection(); // creating a urlconnection object  
    
        // wrapping the urlconnection in a bufferedreader  
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));  
        String line;  
        long matchID = 0;
        int radID, direID, matchesRemaining;
        boolean radTeam = false, direTeam = false;

        while (!(line = bufferedReader.readLine()).startsWith("\"results_rem"))  { }
        matchesRemaining = Integer.parseInt(line.substring(20, line.indexOf(",")));

        while ((line = bufferedReader.readLine()) != null)  { 
            if (line.startsWith("\"match_id")) {
                matchID = Long.parseLong(line.substring(11, line.indexOf(",")));
                if (matchID <= lastMatchID) 
                    break;

                while (!(line = bufferedReader.readLine()).startsWith("\"radiant_team_id"))  { }
                radID = Integer.parseInt(line.substring(18, line.indexOf(",")));

                while (!(line = bufferedReader.readLine()).startsWith("\"dire_team_id"))  { }
                direID = Integer.parseInt(line.substring(15, line.indexOf(",")));

                if (teams.contains(radID)) {
                    radTeam = true;
                } if (teams.contains(direID)) {
                    direTeam = true;
                } if (radTeam || direTeam) {
                    matches.add(new GameToProcess(matchID, radTeam, direTeam));
                }
                radTeam = direTeam = false;
            }
            if (line == null && matchesRemaining > 0) {
                System.out.println("Dead code?");
                url = new URL(link+"&start_at_match_id="+(matchID-1)); 
                urlConnection = url.openConnection();  
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); 

                while (!(line = bufferedReader.readLine()).startsWith("\"results_rem"))  { }
                matchesRemaining = Integer.parseInt(line.substring(20, line.indexOf(",")));
            }
        }
        bufferedReader.close();
        return matches;
    }

    public ArrayList<Integer> getTeamsInDB(Database db) {
        ArrayList<TeamRow> teams = db.teamsSelectAll();
        ArrayList<Integer> teamIDs = new ArrayList<>();
        for (TeamRow t : teams) {
            teamIDs.add(t.tid);
        }
        return teamIDs;
    }

    /**
     * A disaster of a method to get the data we want from match results
     * @param website the url of the data we want
     * @return the results of the game as a GameResult object
     */
    public boolean processGameResult(GameToProcess game) { 

        boolean radiantWin;
        int radiantScore, direScore, radiantID, direID;

        try  {  
            URL url = new URL(game.getLink()); // creating a url object  
            URLConnection urlConnection = url.openConnection(); // creating a urlconnection object  
        
            // wrapping the urlconnection in a bufferedreader  
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));  
            String line;  
            // reading from the urlconnection using the bufferedreader  

            while (!(line = bufferedReader.readLine()).startsWith("\"radiant_win"))  { }
            radiantWin = line.startsWith("\"radiant_win\":t");

            while (!(line = bufferedReader.readLine()).startsWith("\"radiant_score"))  { }
            radiantScore = Integer.parseInt(line.substring(16, line.indexOf(",")));

            while (!(line = bufferedReader.readLine()).startsWith("\"dire_score"))  { }
            direScore = Integer.parseInt(line.substring(13, line.indexOf(",")));

            while (!(line = bufferedReader.readLine()).startsWith("\"radiant_team_id"))  { }
            radiantID = Integer.parseInt(line.substring(18, line.indexOf(",")));

            while (!(line = bufferedReader.readLine()).startsWith("\"dire_team_id"))  { }
            direID = Integer.parseInt(line.substring(15, line.indexOf(",")));

            bufferedReader.close();
        }
        catch(Exception e)  {  
            e.printStackTrace();  
            return false;
        }  

        // If we care about how the radiant team did 
        if (game.radiant) {
            TeamRow radiantTeam = db.getTeam(radiantID);
            int newPrice = radiantTeam.getNewPrice(radiantWin, radiantScore, direScore);
            int newWins = radiantWin ? radiantTeam.wins + 1 : radiantTeam.wins;
            int newLosses = radiantWin ? radiantTeam.losses : radiantTeam.losses + 1;
            db.teamsUpdateOne(radiantTeam.tid, newPrice, newWins, newLosses, radiantTeam.pointsfor + radiantScore,
                                 radiantTeam.pointsagainst + direScore, radiantTeam.price);
        }
        // If we care about how the dire team did
        if (game.dire) {
            TeamRow direTeam = db.getTeam(direID);
            int newPrice = direTeam.getNewPrice(!radiantWin, direScore, radiantScore);
            int newWins = radiantWin ? direTeam.wins : direTeam.wins + 1;
            int newLosses = radiantWin ? direTeam.losses + 1 : direTeam.losses;
            db.teamsUpdateOne(direTeam.tid, newPrice, newWins, newLosses, direTeam.pointsfor + direScore,
                                 direTeam.pointsagainst + radiantScore, direTeam.price);
        }

        return true;
    }
}
