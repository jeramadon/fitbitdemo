package uk.loopiezlol.fitbittester;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import uk.loopiezlol.fitbittester.models.Alarms;
import uk.loopiezlol.fitbittester.models.Device;
import uk.loopiezlol.fitbittester.models.HeartRateInfo;
import uk.loopiezlol.fitbittester.models.StepLogInfo;

//  https://dev.fitbit.com/
public interface FitBitEndpointInterface {

    @GET("1/user/-/devices.json")
    Call<List<Device>> getDevices(@Header("Authorization")String accessToken);

    @GET("1/user/-/devices/tracker/{trackerId}/alarms.json")
    Call<Alarms> getAlarms(@Header("Authorization")String accessToken, @Path("trackerId") String trackerID);

    @GET("1/user/-/activities/steps/date/2016-04-21/1d/15min/time/11:00/20:00.json")
    Call<StepLogInfo> getStepLogInfo(@Header("Authorization")String accessToken);

    @GET("1/user/-/activities/heart/date/2016-04-21/1d/15min/time/11:00/20:00.json")
    Call<HeartRateInfo> getHeartRateInfo(@Header("Authorization")String accessToken);
}
