package edu.nwmissouri.KafkaLiveScoreStream;

import java.util.ArrayList;

public class Demo
{
    public static void main(String[] args) {
        CricBuzzParser cbp = new CricBuzzParser("http://mapps.cricbuzz.com/cbzios/match/livematches");
        cbp.RetrieveURL();
        cbp.Parse();

        ArrayList<Match> alm = cbp.getMatches();
        for (Match m :
                alm)
        {
            System.out.println(m.getMatchID());

            System.out.println(m.getScoreCard());

            System.out.println(m.getScorecard());


            System.out.println(m.getCommentary());

        }

    }
}
