package uk.loopiezlol.fitbittester.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerryamadon on 25/04/16.
 */
public class DataSet {

    @SerializedName("dataset")
    private List<Measurement> measurementList;

    public DataSet() {
        measurementList = new ArrayList<>();
    }

    public List<Measurement> getMeasurementList() {
        return measurementList;
    }

    public void setMeasurementList(List<Measurement> measurementList) {
        this.measurementList = measurementList;
    }
}
