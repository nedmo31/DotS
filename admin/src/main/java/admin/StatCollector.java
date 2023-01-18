package admin;

import java.net.*;  
import java.io.*;  

public class StatCollector {

    /**
     * A disaster of a method to get the data we want from match results
     * @param website the url of the data we want
     * @return the results of the game as a GameResult object
     */
    public static GameResult getGameResult(String website) { 
         
        GameResult res = new GameResult();

        try  {  
            URL url = new URL(website); // creating a url object  
            URLConnection urlConnection = url.openConnection(); // creating a urlconnection object  
        
            // wrapping the urlconnection in a bufferedreader  
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));  
            String line;  
            // reading from the urlconnection using the bufferedreader  

            while (!(line = bufferedReader.readLine()).startsWith("\"radiant_win"))  { }
            res.radiantWin = line.startsWith("\"radiant_win\":t");

            while (!(line = bufferedReader.readLine()).startsWith("\"radiant_score"))  { }
            res.radiantScore = Integer.parseInt(line.substring(16, line.indexOf(",")));

            while (!(line = bufferedReader.readLine()).startsWith("\"dire_score"))  { }
            res.direScore = Integer.parseInt(line.substring(13, line.indexOf(",")));

            while (!(line = bufferedReader.readLine()).startsWith("\"radiant_team_id"))  { }
            res.radiantID = Integer.parseInt(line.substring(18, line.indexOf(",")));

            while (!(line = bufferedReader.readLine()).startsWith("\"dire_team_id"))  { }
            res.direID = Integer.parseInt(line.substring(15, line.indexOf(",")));

            bufferedReader.close();
            return res;
        }

        catch(Exception e)  {  
            e.printStackTrace();  
            return null;
        }  

        
    }
}
