package june.second.lunchmatchmaker.Item;

public class RealMatch {

    private int matchIndex;
    private String matchTitle;
    private String matchTime;
    private String matchPlace;
    private int matchMaxPeople;
    private int matchNowPeople;

    private double lat;
    private double lng;
    private String makerId;
    private String matchKeywordFirst;
    private String matchKeywordSecond;
    private String matchKeywordThird;
    private String matchImageUrl;


    private String firstMemberId;

    private String firstMemberNickname;

    private String secondMemberId;
    private String secondMemberNickname;

    private String thirdMemberId;
    private String thirdMemberNickname;

    private String fourthMemberId;
    private String fourthMemberNickname;

    public RealMatch(){}
    public RealMatch(int matchIndex, String matchTitle, String matchTime, String matchPlace, int matchMaxPeople, double lat, double lng, String makerId,
                     String matchKeywordFirst, String matchKeywordSecond, String matchKeywordThird) {
        this.matchIndex = matchIndex;
        this.matchTitle = matchTitle;
        this.matchTime = matchTime;
        this.matchPlace = matchPlace;
        this.matchMaxPeople = matchMaxPeople;
        this.lat = lat;
        this.lng = lng;
        this.makerId = makerId;
        this.firstMemberId = makerId;
        this.matchKeywordFirst = matchKeywordFirst;
        this.matchKeywordSecond = matchKeywordSecond;
        this.matchKeywordThird = matchKeywordThird;
    }


    public int getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(int matchIndex) {
        this.matchIndex = matchIndex;
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

    public int getMatchMaxPeople() {
        return matchMaxPeople;
    }

    public void setMatchMaxPeople(int matchMaxPeople) {
        this.matchMaxPeople = matchMaxPeople;
    }

    public int getMatchNowPeople() {
        return matchNowPeople;
    }

    public void setMatchNowPeople(int matchNowPeople) {
        this.matchNowPeople = matchNowPeople;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getMakerId() {
        return makerId;
    }

    public void setMakerId(String makerId) {
        this.makerId = makerId;
    }

    public String getMatchKeywordFirst() {
        return matchKeywordFirst;
    }

    public void setMatchKeywordFirst(String matchKeywordFirst) {
        this.matchKeywordFirst = matchKeywordFirst;
    }

    public String getMatchKeywordSecond() {
        return matchKeywordSecond;
    }

    public void setMatchKeywordSecond(String matchKeywordSecond) {
        this.matchKeywordSecond = matchKeywordSecond;
    }

    public String getMatchKeywordThird() {
        return matchKeywordThird;
    }

    public void setMatchKeywordThird(String matchKeywordThird) {
        this.matchKeywordThird = matchKeywordThird;
    }

    public String getFirstMemberId() {
        return firstMemberId;
    }

    public void setFirstMemberId(String firstMemberId) {
        this.firstMemberId = firstMemberId;
    }

    public String getFirstMemberNickname() {
        return firstMemberNickname;
    }

    public void setFirstMemberNickname(String firstMemberNickname) {
        this.firstMemberNickname = firstMemberNickname;
    }

    public String getSecondMemberId() {
        return secondMemberId;
    }

    public void setSecondMemberId(String secondMemberId) {
        this.secondMemberId = secondMemberId;
    }

    public String getSecondMemberNickname() {
        return secondMemberNickname;
    }

    public void setSecondMemberNickname(String secondMemberNickname) {
        this.secondMemberNickname = secondMemberNickname;
    }

    public String getThirdMemberId() {
        return thirdMemberId;
    }

    public void setThirdMemberId(String thirdMemberId) {
        this.thirdMemberId = thirdMemberId;
    }

    public String getThirdMemberNickname() {
        return thirdMemberNickname;
    }

    public void setThirdMemberNickname(String thirdMemberNickname) {
        this.thirdMemberNickname = thirdMemberNickname;
    }

    public String getFourthMemberId() {
        return fourthMemberId;
    }

    public void setFourthMemberId(String fourthMemberId) {
        this.fourthMemberId = fourthMemberId;
    }

    public String getFourthMemberNickname() {
        return fourthMemberNickname;
    }

    public void setFourthMemberNickname(String fourthMemberNickname) {
        this.fourthMemberNickname = fourthMemberNickname;
    }
}
