package june.second.lunchmatchmaker.Item;

public class MatchRoom {

    private int matchIndex;
    private String makerId;
    private int matchMaxPeople;
    private int matchNowPeople;

    private String firstMemberId;



    private String firstMemberNickname;

    private String secondMemberId;
    private String secondMemberNickname;

    private String thirdMemberId;
    private String thirdMemberNickname;

    private String fourthMemberId;
    private String fourthMemberNickname;



    public MatchRoom(){}

    public MatchRoom(int matchIndex, String makerId, int matchMaxPeople) {
        this.matchIndex = matchIndex;
        this.makerId = makerId;
        this.matchMaxPeople = matchMaxPeople;
        this.firstMemberId = makerId;

    }


    public int getMatchNowPeople() {
        return matchNowPeople;
    }

    public void setMatchNowPeople(int matchNowPeople) {
        this.matchNowPeople = matchNowPeople;
    }

    public int getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(int matchIndex) {
        this.matchIndex = matchIndex;
    }

    public String getMakerId() {
        return makerId;
    }

    public void setMakerId(String makerId) {
        this.makerId = makerId;
    }

    public int getMatchMaxPeople() {
        return matchMaxPeople;
    }

    public void setMatchMaxPeople(int matchMaxPeople) {
        this.matchMaxPeople = matchMaxPeople;
    }

    public String getFirstMemberId() {
        return firstMemberId;
    }

    public void setFirstMemberId(String firstMemberId) {
        this.firstMemberId = firstMemberId;
    }

    public String getSecondMemberId() {
        return secondMemberId;
    }

    public void setSecondMemberId(String secondMemberId) {
        this.secondMemberId = secondMemberId;
    }

    public String getThirdMemberId() {
        return thirdMemberId;
    }

    public void setThirdMemberId(String thirdMemberId) {
        this.thirdMemberId = thirdMemberId;
    }

    public String getFourthMemberId() {
        return fourthMemberId;
    }

    public void setFourthMemberId(String fourthMemberId) {
        this.fourthMemberId = fourthMemberId;
    }


    public String getFirstMemberNickname() {
        return firstMemberNickname;
    }

    public void setFirstMemberNickname(String firstMemberNickname) {
        this.firstMemberNickname = firstMemberNickname;
    }

    public String getSecondMemberNickname() {
        return secondMemberNickname;
    }

    public void setSecondMemberNickname(String secondMemberNickname) {
        this.secondMemberNickname = secondMemberNickname;
    }

    public String getThirdMemberNickname() {
        return thirdMemberNickname;
    }

    public void setThirdMemberNickname(String thirdMemberNickname) {
        this.thirdMemberNickname = thirdMemberNickname;
    }

    public String getFourthMemberNickname() {
        return fourthMemberNickname;
    }

    public void setFourthMemberNickname(String fourthMemberNickname) {
        this.fourthMemberNickname = fourthMemberNickname;
    }
}
