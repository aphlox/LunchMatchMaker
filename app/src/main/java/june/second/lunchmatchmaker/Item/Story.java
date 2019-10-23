package june.second.lunchmatchmaker.Item;

public class Story {

    private String storyKey;
    private String storyMakerProfileImage;
    private String storyMakerNickname;

    private String storyValue;
    private String storyContent;

    public Story(String storyKey, String storyMakerProfileImage, String storyMakerNickname, String storyValue, String storyContent) {
        this.storyKey = storyKey;
        this.storyMakerProfileImage = storyMakerProfileImage;
        this.storyMakerNickname = storyMakerNickname;
        this.storyValue = storyValue;
        this.storyContent = storyContent;
    }


    public String getStoryKey() {
        return storyKey;
    }

    public void setStoryKey(String storyKey) {
        this.storyKey = storyKey;
    }

    public String getStoryMakerProfileImage() {
        return storyMakerProfileImage;
    }

    public void setStoryMakerProfileImage(String storyMakerProfileImage) {
        this.storyMakerProfileImage = storyMakerProfileImage;
    }

    public String getStoryMakerNickname() {
        return storyMakerNickname;
    }

    public void setStoryMakerNickname(String storyMakerNickname) {
        this.storyMakerNickname = storyMakerNickname;
    }

    public String getStoryValue() {
        return storyValue;
    }

    public void setStoryValue(String storyValue) {
        this.storyValue = storyValue;
    }

    public String getStoryContent() {
        return storyContent;
    }

    public void setStoryContent(String storyContent) {
        this.storyContent = storyContent;
    }

    @Override
    public String toString() {
        return "Story{" +
                "storyKey='" + storyKey + '\'' +
                ", storyValue='" + storyValue + '\'' +
                ", storyContent='" + storyContent + '\'' +
                '}';
    }
}
