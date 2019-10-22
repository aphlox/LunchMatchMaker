package june.second.lunchmatchmaker.Item;

import android.util.Log;

public class Timeline {

    private String timelineDateNTime;
    private String timelineTitle;


    String here = "getTimeline";

    public Timeline(String timelineDateNTime, String timelineTitle) {
        this.timelineDateNTime = timelineDateNTime;
        this.timelineTitle = timelineTitle;
    }


    public String getTimelineDateNTime() {
        return timelineDateNTime;
    }

    public void setTimelineDateNTime(String timelineDateNTime) {
        this.timelineDateNTime = timelineDateNTime;
    }

    /*
        타임라인 날짜데이터를 split해서 원하는 시간 단위 (년,달,일, 오전/오후, 시간, 분)을 받아올 수 있게 메소드 설정
    */
    public String getTimelineYear() {
        Log.w(here," timelineDateNTime - YEAR  :" + timelineDateNTime.split("/")[0]+"!") ;

        return timelineDateNTime.split("/")[0];
    }
    public int getTimelineMonth() {
        Log.w(here," timelineDateNTime - MONTH  :" + timelineDateNTime.split("/")[1]+"!");

        return  Integer.parseInt(timelineDateNTime.split("/")[1]) ;
    }
    public int getTimelineDate() {
        Log.w(here," timelineDateNTime - DATE  :" + timelineDateNTime.split("/")[2].split("  ")[0].split(" ")[0]+"!");

        return Integer.parseInt(timelineDateNTime.split("/")[2].split("  ")[0].split(" ")[0]);
    }
    public String getTimelineNoon() {
        Log.w(here," timelineDateNTime - NOON  :" + timelineDateNTime.split("/")[2].split("  ")[1].split(" ")[0]+"!");

        return timelineDateNTime.split("/")[2].split("  ")[1].split(" ")[0];
    }
    public int getTimelineHour() {
        Log.w(here," timelineDateNTime - HOUR  :" + timelineDateNTime.split("/")[2].split("  ")[1].split(" ")[1].split("시")[0]+"!");

        return Integer.parseInt(timelineDateNTime.split("/")[2].split("  ")[1].split(" ")[1].split("시")[0]);
    }
    public int getTimelineMin() {
        Log.w(here," timelineDateNTime - MIN  :"   + timelineDateNTime.split("/")[2].split("  ")[1].split(" ")[2].split("분")[0]+"!");

        return Integer.parseInt(timelineDateNTime.split("/")[2].split("  ")[1].split(" ")[2].split("분")[0]);
    }




    public String getTimelineTitle() {
        return timelineTitle;
    }

    public void setTimelineTitle(String timelineTitle) {
        this.timelineTitle = timelineTitle;
    }


    @Override
    public String toString() {
        return " { " +
                "timelineDateNTime : " + timelineDateNTime  +
                " , timelineTitle : " + timelineTitle +" } ";
    }
}
