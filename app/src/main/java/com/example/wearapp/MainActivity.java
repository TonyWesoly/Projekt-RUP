package com.example.wearapp;

import ClothingService.ClothingService;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
//import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.Notifications.AlarmReceiver;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    // widgets
    private EditText adresM;
    private EditText adresP;
    private EditText godzina;
    private EditText editDataTime;
//    private TextView txtShow;

    // for trace api
    private String homeAddress;
    private String workAddress;
    private String hourOfWorkingStart;
    private String dataOfWorkingStart;
    private String patternDate;

    private SimpleDateFormat datetimeFormat;
    private Date timeOfWorkingStart;
    private SimpleDateFormat timeFormat;
    private String timeOfAwaking;
    long epoch;
    String epochS;
    private final List<String> data = new ArrayList<>();

    // for weather api
    private String cloth;
    private int time;

    // for multithreading
    private Handler mainHandler = new Handler();

    Calendar calendar;


    class WeatherAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject info = null;
            try {
                info = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject day = null;
            try {
                assert info != null;
                day = info.getJSONObject("forecast")
                        .getJSONArray("forecastday")
                        .getJSONObject(0)
                        .getJSONObject("day");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            double avgTemp = 0;
            try {
                assert day != null;
                avgTemp = day.getDouble("avgtemp_c");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject condition = null;
            try {
                condition = day.getJSONObject("condition");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String weather = null;
            try {
                assert condition != null;
                weather = condition.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ClothingService clothes = new ClothingService();
            clothes.setClothes(avgTemp);
            assert weather != null;
            clothes.addAccessories(weather);
            cloth = clothes.getClothes();
            time = clothes.getTime();
//            txtShow.setText(cloth + time);
            new TraceDataAsync().execute(calendar);
        }
    }

    class TraceDataAsync extends AsyncTask<Calendar, Integer, String>{
        private static final String TAG = "Get data 0: ";
        private String departure_time_value = "";
        private String departure_time_text = "";
        private String line_transport = "";
        protected void getTraceByJson(String url) throws JSONException {
            //zwraca czas, kiedy trzeba wyjsc z domu i trase
            JSONObject info;
            info = new JSONObject(url);
            departure_time_value = info.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONObject("departure_time")
                    .getString("value");
            departure_time_text = info.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONObject("departure_time")
                    .getString("text");
            line_transport = info.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("steps")
                    .getJSONObject(1)
                    .getJSONObject("transit_details")
                    .getJSONObject("line")
                    .getString("short_name");

//            mainHandler.post(new Runnable() {
//                @Override
//                public void run() {
//            data.clear();
//            data.add(departure_time_value);
//            data.add(departure_time_text);
//            data.add(line_transport);
//
//                }
//            });
        }

        //        @Override
        protected String run() {
            String trace_data = "";
            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/directions" +
                        "/json?key=AIzaSyCNx1cp5ReJvuzJ5XqCBijNxy2B0mAUl_s&mode=transit&origin=" + homeAddress
                        + "&destination=" + workAddress
                        + "&arrival_time=" + epochS);
                /*
                test
                URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=Os.SobieskiegoPoznan&destination=Druzbickiego2,Poznan&key=AIzaSyCNx1cp5ReJvuzJ5XqCBijNxy2B0mAUl_s&mode=transit&arrival_time=1640116800");
                URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=Os.SobieskiegoPoznan&destination=Drużbickiego2,Poznań&key=AIzaSyCNx1cp5ReJvuzJ5XqCBijNxy2B0mAUl_s&arrival_time=1639428974");
                */

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
//                connection.connect();

                InputStream stream = connection.getInputStream();
                Log.d(TAG, "###################################");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                trace_data = buffer.toString();
                getTraceByJson(trace_data);


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            //test return
            return trace_data;
        }
        protected String doInBackground(Calendar... calendars) {
            return run();
        }

        protected void onProgressUpdate(Integer... progress) {
        }


        protected void onPostExecute(String result) {

            // Parsing time for create notification
            timeFormat = new SimpleDateFormat("HH:mm");
            long millis = Long.parseLong(String.valueOf(Integer.parseInt(departure_time_value)-(time*60+20*60)));
            timeOfAwaking = timeFormat.format(new Date(millis * 1000));
            Date awakingDate = new Date(millis * 1000);
            Calendar awakingCalendar = Calendar.getInstance();
            awakingDate.setTime(awakingDate.getTime());
            createNotification("Wake Up","Tramwaj numer: " + line_transport + "/Godzina odjazdu:" +
                    departure_time_text + "/Ubranie:" + cloth, awakingCalendar);

            Toast.makeText(MainActivity.this, "Powiadomienie nadejdzie o " + hourOfWorkingStart, Toast.LENGTH_LONG).show();
        }
    }

    private void createNotification(String title, String message, Calendar calendar){

        //Alarm/notification data
        final int notificationId = 1;
        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);

        //Intent
        alarmIntent.putExtra("notificationId", notificationId);
        alarmIntent.putExtra("title",title);
        alarmIntent.putExtra("message",message);

        //PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        //AlarmManager
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        // Create time.
        long alarmStartTime = calendar.getTimeInMillis();

        // Set Alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adresM = (EditText) findViewById(R.id.adresM);
        adresP = (EditText) findViewById(R.id.adresP);
//        godzina = (EditText) findViewById(R.id.godzina);
        editDataTime = (EditText) findViewById(R.id.dataGodzina);
//        txtShow = (TextView) findViewById(R.id.textTest);
        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);

        calendar=Calendar.getInstance();
        //dla uruchomenia api tras -> new TraceData.start(), resultat w trace_json
        editDataTime.setInputType(InputType.TYPE_NULL);

        editDataTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try{
//                    DatePickerDialog mDatePicker;
//                    mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            calendar.set(Calendar.YEAR, selectedyear);
                            calendar.set(Calendar.MONTH, selectedmonth);
                            calendar.set(Calendar.DAY_OF_MONTH, selectedday);

                            TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hoursOfDay, int minute) {
                                    calendar.set(Calendar.HOUR_OF_DAY, hoursOfDay);
                                    calendar.set(Calendar.MINUTE, minute);

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                    editDataTime.setText(simpleDateFormat.format((calendar.getTime())));
                                }
                            };
                            new TimePickerDialog(MainActivity.this,
                                    timeSetListener,
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false).show();
                        }
                    };
                    new DatePickerDialog(MainActivity.this,
                            dateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    // Information from widgets
                    homeAddress = adresM.getText().toString();
                    workAddress = adresP.getText().toString();
//                    hourOfWorkingStart = godzina.getText().toString();
//                    dataOfWorkingStart = editDataTime.getText().toString();

                    // Parsing date for TraceData
                    SimpleDateFormat hoursFormat = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat dataFormat = new SimpleDateFormat("dd.MM.yyyy");
                    timeOfWorkingStart = calendar.getTime();
                    hourOfWorkingStart = hoursFormat.format(calendar.getTime());
                    dataOfWorkingStart = dataFormat.format(calendar.getTime());

                    assert timeOfWorkingStart != null;
                    epoch = (timeOfWorkingStart.getTime() / 1000);
                    epoch += 3600;
                    epochS = Long.toString(epoch);
                    String city = "Poznan";
                    String key = "992e7cab06164165980213921210812";
                    String url = "https://api.weatherapi.com/v1/history.json?key=" + key +
                            "&q=" + city + "&dt=" + dataOfWorkingStart;
                    new WeatherAPI().execute(url);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    FileInputStream fileIn = openFileInput("test.json");
//                    InputStreamReader reader = new InputStreamReader(fileIn);
//                    BufferedReader bufferread = new BufferedReader(reader);
//                    StringBuffer strbuff = new StringBuffer();
//                    String str;
//                    while ((str = bufferread.readLine()) != null) {
//                        strbuff.append(str + "\n");
//                    }
                    Toast.makeText(MainActivity.this, "Powiadomienie nadejdzie o " + timeOfAwaking, Toast.LENGTH_LONG).show();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
}