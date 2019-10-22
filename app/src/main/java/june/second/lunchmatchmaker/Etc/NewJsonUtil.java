package june.second.lunchmatchmaker.Etc;

import android.util.Log;

import java.util.ArrayList;

import june.second.lunchmatchmaker.Item.Timeline;

public class NewJsonUtil {

    String dataString;
    ArrayList<Timeline> finalTimelineArrayList;
    ArrayList highKeyArrayList = new ArrayList();
    ArrayList highValueArrayList = new ArrayList();
    ArrayList keyArrayList = new ArrayList();
    ArrayList valueArrayList = new ArrayList();
    ArrayList getObjectArrayList = new ArrayList();

    public NewJsonUtil() {
        this.dataString = dataString;


    }


    public ArrayList<Timeline> newJsonToDataOfTimeline(String dataString, ArrayList<Timeline> timelineArrayList) {
        //들어오면 일단은 객체이기때문에 객체로 분리함
        //문자열의 앞뒤 공백을 제거한다
        dataString = dataString.trim();

        //json의 시작은
        if (dataString.startsWith("{")) {
            //{ 제외
            dataString = dataString.substring(1);
            //공백 제거
            dataString = dataString.trim();

            //키 값이 아니면 "{"가 나온다
            if (dataString.startsWith("}")) {

            } else {//키값나오는 경우
                String key = dataString.split(":", 2)[0].trim();
                highKeyArrayList.add(key);
                Log.w("newJsonToDataOfTimeline", "key  @" + key);


                String value = dataString.split(":", 2)[1].trim();
                Log.w("newJsonToDataOfTimeline", "value  @" + value);
                highValueArrayList.add(value);
                jsonValueToString(value);


            }


        }
        for (int i = 0; i < keyArrayList.size(); i++) {
            timelineArrayList.add(new Timeline(valueArrayList.get(i).toString().split(",")[0].trim(),
                    valueArrayList.get(i).toString().split(",")[1].split(":")[1].trim().substring(0,valueArrayList.get(i).toString().split(",")[1].split(":")[1].trim().length()-1)));
            Log.w("newJsonToDataOfTimeline", "keyArrayList"+i+": "+keyArrayList.get(i).toString() );
            Log.w("newJsonToDataOfTimeline", "valueArrayList"+i+": "+valueArrayList.get(i).toString());
            Log.w("newJsonToDataOfTimeline", "valueArrayList.get(i).toString().split(,)[0].trim()  @ "+valueArrayList.get(i).toString().split(",")[0].trim());
            Log.w("newJsonToDataOfTimeline", "valueArrayList.get(i).toString().split(\",\")[1].split(\":\")[1].trim()  @ "+valueArrayList.get(i).toString().split(",")[1].split(":")[1].trim());

        }


        highKeyArrayList.clear();
        highValueArrayList.clear();
        keyArrayList.clear();
        valueArrayList.clear();
        getObjectArrayList.clear();

        return timelineArrayList;

    }


    public String jsonValueToString(String dataString) {
        dataString = dataString.trim();

        //value가 array인 경우
        if (dataString.startsWith("[")) {

            Log.w("jsonValueToString", "this is Array Value");
            Log.w("jsonValueToString", "Array Value  @" + dataString);
            // "["제외
            dataString = dataString.substring(1);
            dataString = dataString.trim();
            while (!dataString.startsWith("]")) {


                String valueString = jsonValueToString(dataString);
                getObjectArrayList.add(valueString);
                Log.w("jsonValueToString", "this is Array Value  :" + valueString);
                dataString = dataString.substring(valueString.length());

                dataString = dataString.trim();
                if (dataString.startsWith(",")) {
                    // ","제외
                    dataString = dataString.substring(1);
                    dataString = dataString.trim();

                }
            }


        }
        //value가 object인 경우
        else if (dataString.startsWith("{")) {
            Log.w("jsonValueToString", "this is Object Value");
            Log.w("jsonValueToString", "Object Value  @" + dataString);
            // "{"제외
//            String afterObjectString = dataString.substring(dataString.indexOf("}") + 1);
            String objectString = dataString.substring(0, dataString.indexOf("}") + 1);
            jsonObjectToString(objectString);
            //value 값을 돌려주고
            //value안에 있는 키값은 관련 메소드에서 얻기
            return objectString;

        } else {
            Log.w("jsonValueToString", "this is String Value");
            Log.w("jsonValueToString", "String Value  @" + dataString);
            return dataString;


        }
        return dataString;

    }

    public void jsonObjectToString(String dataString) {
        //들어오면 일단은 객체이기때문에 객체로 분리함
        //문자열의 앞뒤 공백을 제거한다
        dataString = dataString.trim();

        //json의 시작은
        if (dataString.startsWith("{")) {
            //{ 제외
            dataString = dataString.substring(1);
            //공백 제거
            dataString = dataString.trim();

            //키 값이 아니면 "{"가 나온다
            if (dataString.startsWith("}")) {

            } else {//키값나오는 경우
                String key = dataString.split(":", 2)[0].trim();
                keyArrayList.add(key);
                Log.w("jsonObjectToString", "key  @" + key);


                String value = dataString.split(":", 2)[1].trim();
                valueArrayList.add(value);
                jsonValueToString(value);

            }

        }

    }

    public String newDataToJsonOfTimeline(String dataString, ArrayList<Timeline> timelineArrayList) {

        //데이터 다시 쉐어드화
        dataString = "{ timeArrayList : [ ";
        for (int i = 0; i < timelineArrayList.size() - 1; i++) {
            dataString = dataString.concat(timelineArrayList.get(i).toString() + " , ");
        }
        dataString = dataString.concat(timelineArrayList.get(timelineArrayList.size() - 1).toString() + " ] } ");

        return dataString;
    }

    public String DataToJsonOfOneTimeline(String dataString, ArrayList<Timeline> timelineArrayList) {

        //데이터 다시 쉐어드화
        dataString = timelineArrayList.get(0).toString();

        return dataString;
    }


}












