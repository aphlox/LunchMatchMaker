package june.second.lunchmatchmaker.Item;

public class Story {

    private String storyKey;
    private String storyValue;
    private String storyContent;

    public Story(String storyKey, String storyValue , String storyContent) {
        this.storyKey = storyKey;
        this.storyValue = storyValue;
        this.storyContent = storyContent;
    }

    public String getStoryKey() {
        return storyKey;
    }

    public void setStoryKey(String storyKey) {
        this.storyKey = storyKey;
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
