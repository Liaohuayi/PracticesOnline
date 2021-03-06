package net.lzzy.practicesonline.activities.models.view;

import net.lzzy.practicesonline.activities.constants.ApiConstants;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/8.
 * Description:
 */
public class PracticeResult {
    private static final String SPLITTER = ",";
    private List<QuestionResult> results;
    private int id;
    private String info;

    public PracticeResult(List<QuestionResult> results, int apiId, String info) {
        this.id = apiId;
        this.info = info;
        this.results = results;
    }

    public List<QuestionResult> getResults() {
        return results;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {

        return info;
    }

    private double getRetio() {
        //todo:算分数
        int rightCount=0;
        for (QuestionResult result:results){
            if (result.isRight()){
                rightCount++;
            }
        }
        return rightCount*1.0/results.size();
    }

    private String getWrongOrders() {
        //todo:错题的序号，例如：1.2.3
        int i=0;
        String ids="";
        for (QuestionResult result:results){
            i++;
            if (!result.isRight()){
                ids=ids.concat(i+ SPLITTER);
            }
        }
        if (ids.endsWith(SPLITTER)){
            ids=ids.substring(0,ids.length()-1);
        }
        return ids;

    }
    public JSONObject tojson() throws JSONException {
        JSONObject json=new JSONObject();
        json.put(ApiConstants.JSON_RESULT_API_ID,id);
        json.put(ApiConstants.JSON_RESULT_PERSON_INFO,info);
        json.put(ApiConstants.JSON_RESULT_SCORE_RATION,new DecimalFormat("#.00").format(getRetio()));
        json.put(ApiConstants.JSON_RESULT_WRONG_IDS,getWrongOrders());
        return json;
    }
}
