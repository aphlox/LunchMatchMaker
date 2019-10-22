package june.second.lunchmatchmaker.Item;

import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;

public class Match  {
    public int getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(int matchIndex) {
        this.matchIndex = matchIndex;
    }

    private int matchIndex;
    private String matchTitle;
    private String matchTime;
    private String matchPlace;
    private int matchPeople;
    private ArrayList<String> matchKeyword;
    private LatLng latLng;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Match(int matchIndex, String matchTitle, String matchTime, String matchPlace, int matchPeople, ArrayList<String> matchKeyword, LatLng latLng) {
        this.matchIndex = matchIndex;
        this.matchTitle = matchTitle;
        this.matchTime = matchTime;
        this.matchPlace = matchPlace;
        this.matchPeople = matchPeople;
        this.matchKeyword = matchKeyword;
        this.latLng = latLng;
    }

    public String getMatchTitle() {
        return matchTitle;
    }

    public void setMatchTitle(String matchTitle) {
        this.matchTitle = matchTitle;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getMatchPlace() {
        return matchPlace;
    }

    public void setMatchPlace(String matchPlace) {
        this.matchPlace = matchPlace;
    }

    public int getMatchPeople() {
        return matchPeople;
    }

    public void setMatchPeople(int matchPeople) {
        this.matchPeople = matchPeople;
    }

    public ArrayList<String> getMatchKeyword() {
        return matchKeyword;
    }

    public void setMatchKeyword(ArrayList<String> matchKeyword) {
        this.matchKeyword = matchKeyword;
    }


    @Override
    public String toString() {
        return "Match{" +
                "matchIndex=" + matchIndex +
                ", matchTitle='" + matchTitle + '\'' +
                ", matchTime='" + matchTime + '\'' +
                ", matchPlace='" + matchPlace + '\'' +
                ", matchPeople=" + matchPeople +
                ", matchKeyword=" + matchKeyword +
                ", latLng=" + latLng +
                '}';
    }
}
