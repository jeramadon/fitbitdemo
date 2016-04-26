package uk.loopiezlol.fitbittester;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import uk.loopiezlol.fitbittester.models.Alarms;
import uk.loopiezlol.fitbittester.models.DataSet;
import uk.loopiezlol.fitbittester.models.Device;
import uk.loopiezlol.fitbittester.models.HeartRateInfo;
import uk.loopiezlol.fitbittester.models.Measurement;
import uk.loopiezlol.fitbittester.models.StepLogInfo;
import uk.loopiezlol.fitbittester.models.TrackerAlarm;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL ="https://api.fitbit.com/";
    public final String TAG = MainActivity.this.getClass().getSimpleName();
    private FitBitEndpointInterface apiService;
    public ArrayList<Device> myDevices = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private String trackerId;
    private Alarms myAlarms ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String callingIntentName = "";
        if (getCallingActivity() != null) {
            callingIntentName = getCallingActivity().getClassName();
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean haveToken = sharedPreferences.getBoolean(QuickPreferences.HAVE_AUTHORIZATION, false);

        if (!haveToken || callingIntentName.equals("")) {
            LoginFragment loginFragment = new LoginFragment();
            openFragment(loginFragment);
        } else {
            Boolean haveDeviceID = sharedPreferences.getBoolean(QuickPreferences.HAVE_DEVICE_ID, false);
            String token = sharedPreferences.getString(QuickPreferences.FULL_AUTHORIZATION, null);

            if (!haveDeviceID) {
                getDeviceId(token);
            } else {
                startFitBitQueries(token);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                        //.client(httpClient)
                .build();
        apiService = retrofit.create(FitBitEndpointInterface.class);
    }

    private void openFragment(Fragment myFragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.animator.slide_in_left,
                R.animator.slide_out_right,
                R.animator.slide_out_left,
                R.animator.slide_in_right);
        fragmentTransaction.replace(R.id.content_main, myFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getDeviceId(final String token) {
        initRetrofit();
        apiService.getDevices(token).enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Response<List<Device>> response, Retrofit retrofit) {
                Log.d(TAG, response.message());
                for (Device device : response.body()) {
                    myDevices.add(device);

                }

                final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(MainActivity.this);

                for (Device device : myDevices) {
                    adapter.add(new MaterialSimpleListItem.Builder(MainActivity.this)
                            .content(device.getDeviceVersion())
                            .backgroundColor(Color.WHITE)
                            .build());
                }

                new MaterialDialog.Builder(MainActivity.this)
                        .title("Choose your Device")
                        .adapter(adapter, new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                sharedPreferences.edit().putBoolean(QuickPreferences.HAVE_DEVICE_ID, true).apply();
                                sharedPreferences.edit().putString(QuickPreferences.MY_DEVICE_ID, myDevices.get(which).getId()).apply();
                                dialog.dismiss();
                                startFitBitQueries(token);
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void startFitBitQueries(String token) {
        initRetrofit();

        //  getAlarms
        trackerId = sharedPreferences.getString(QuickPreferences.MY_DEVICE_ID,null);
        apiService.getAlarms(token,trackerId).enqueue(new Callback<Alarms>() {
            @Override
            public void onResponse(Response<Alarms> response, Retrofit retrofit) {
                Log.d(TAG, response.message());
                Log.d(TAG, "Alarms:");
                myAlarms = new Alarms();
                myAlarms.setTrackerAlarms(response.body().getTrackerAlarms());
                for(TrackerAlarm alarm: myAlarms.getTrackerAlarms()){
                    Log.d(TAG, alarm.getAlarmId().toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();

            }
        });

        //  getStepLogInfo
        apiService.getStepLogInfo(token).enqueue(new Callback<StepLogInfo>() {
            @Override
            public void onResponse(Response<StepLogInfo> response, Retrofit retrofit) {
                Log.d(TAG, response.message());
                Log.d(TAG, "Step Log:");
                DataSet dataSet = response.body().getDataSet();
                if (dataSet != null) {
                    for (Measurement measurement : dataSet.getMeasurementList()) {
                        Log.d(TAG, measurement.toString());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

        //  getHeartRateInfo
        apiService.getHeartRateInfo(token).enqueue(new Callback<HeartRateInfo>() {
            @Override
            public void onResponse(Response<HeartRateInfo> response, Retrofit retrofit) {
                Log.d(TAG, response.message());
                Log.d(TAG, "Heart rate measurements:");
                DataSet dataSet = response.body().getDataSet();
                if (dataSet != null) {
                    for (Measurement measurement : dataSet.getMeasurementList()) {
                        Log.d(TAG, measurement.toString());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
