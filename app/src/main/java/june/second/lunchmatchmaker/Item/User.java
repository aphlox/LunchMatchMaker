package june.second.lunchmatchmaker.Item;

public class User {

    private boolean userApproval;
    private String userId;
    private String userPw;
    private String userName;
    private String userGender;
    private String userBirthday;
    private String userNickName;
    private String userComment;
    private String userProfilePath;


    public User(boolean userApproval, String userId, String userPw, String userName, String userGender, String userBirthday, String userNickName, String userComment, String userProfilePath) {
        this.userApproval = userApproval;
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userGender = userGender;
        this.userBirthday = userBirthday;
        this.userNickName = userNickName;
        this.userComment = userComment;
        this.userProfilePath = userProfilePath;
    }

    public boolean isUserApproval() {
        return userApproval;
    }

    public void setUserApproval(boolean userApproval) {
        this.userApproval = userApproval;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getUserProfilePath() {
        return userProfilePath;
    }

    public void setUserProfilePath(String userProfilePath) {
        this.userProfilePath = userProfilePath;
    }
}
