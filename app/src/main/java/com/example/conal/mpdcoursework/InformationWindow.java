package com.example.conal.mpdcoursework;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;


/*
Conall McLuskie
S1509449
 */
public class InformationWindow extends Activity {

    private TextView dateView;
    private String dateString;
    private TextView timeView;
    private String timeString;
    private TextView locationView;
    private String locationString;
    private TextView latView;
    private String latString;
    private TextView longView;
    private String longString;
    private TextView depthView;
    private String depthString;
    private TextView magView;
    private String magString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e("Window", "OnCreateStart");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.details_menu);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * .8) , (int)(height*.6));
        Log.e("Window", "OnCreateEnd");
        SetValues();
        SetViews();
        SetViewValues();
    }


    private void SetValues()
    {
        Bundle extras = getIntent().getExtras();
        if(extras!= null)
        {
            dateString = (String)extras.get("date");
			timeString = (String)extras.get("time");
			locationString = (String)extras.get("location");
			latString = (String)extras.get("latitude");
			longString = (String)extras.get("longitude");
			depthString = (String)extras.get("depth");
			magString = (String)extras.get("magnitude");
        }
    }

    private void SetViews(){
    dateView = (TextView) findViewById(R.id.dateDetails);
    timeView = (TextView) findViewById(R.id.timeDetails);
    locationView = (TextView) findViewById(R.id.locationDetails);
    latView = (TextView)findViewById(R.id.latitudeDetails);
   // longView = (TextView)findViewById(R.id.);
    depthView = (TextView)findViewById(R.id.depthDetails);
    magView = (TextView)findViewById(R.id.magnitudeDetails);

    }

    private void SetViewValues()
    {
       // Log.e("Window", dateString);
        dateView.setText(dateString);
        timeView.setText(timeString);
        locationView.setText(locationString);
        latView.setText(latString + ", " + longString);
        depthView.setText(depthString + "km");
        magView.setText(magString);
    }
}
