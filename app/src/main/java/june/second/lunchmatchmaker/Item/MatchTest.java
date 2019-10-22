package june.second.lunchmatchmaker.Item;

public class MatchTest {


    private int matchIndex;
    private String matchTitle;


    public MatchTest(int matchIndex, String matchTitle) {
        this.matchIndex = matchIndex;
        this.matchTitle = matchTitle;
    }


    public int getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(int matchIndex) {
        this.matchIndex = matchIndex;
    }

    @Override
    public String toString() {
        return "MatchTest{" +
                "matchIndex=" + matchIndex +
                ", matchTitle='" + matchTitle + '\'' +
                '}';
    }

    public String getMatchTitle() {
        return matchTitle;
    }

    public void setMatchTitle(String matchTitle) {
        this.matchTitle = matchTitle;
    }
}
