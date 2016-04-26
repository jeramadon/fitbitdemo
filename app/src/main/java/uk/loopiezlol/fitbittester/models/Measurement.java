package uk.loopiezlol.fitbittester.models;

/**
 * Created by jerryamadon on 25/04/16.
 */
public class Measurement {

    private String time;
    private Integer value;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String toString() {
        return time + " : " + value;
    }
}
