package uk.loopiezlol.fitbittester.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jerryamadon on 25/04/16.
 */
public class StepLogInfo {

    @SerializedName("activities-steps-intraday")
    private DataSet dataSet;

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
}
