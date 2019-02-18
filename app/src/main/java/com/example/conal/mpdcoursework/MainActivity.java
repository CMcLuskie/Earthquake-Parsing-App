
//
// Name                 Conall McLuskie
// Student ID           s1509449
// Programme of Study   Computer Games Software Development
//

// Update the package name to include your Student Identifier
package com.example.conal.mpdcoursework;

import android.content.res.Configuration;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView rawDataDisplay;
    private Button startButton;
    private String result;
    private String url1="";
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private enum Orientation{ landscape, portrait}
    private float aspectRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        aspectRatio = FindRatio();
        Log.e("MyTag",Float.toString(aspectRatio));

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            InitialiseView(Orientation.landscape);
        }
        else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            InitialiseView(Orientation.portrait);
        }


        // More Code goes here
    }

    void InitialiseView(Orientation orientation)
    {
        if(aspectRatio >1.9 && aspectRatio <2|| aspectRatio > .5 && aspectRatio <.6)
        {
            Log.e("MyTag","is a phone");
            switch(orientation)
            {
                case landscape:
                    setContentView(R.layout.phone_landscape);

                    SetOrientation(Orientation.portrait);
                    // Set up the raw links to the graphical components
                    rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);

                    startButton = (Button)findViewById(R.id.startButton);
                    startButton.setOnClickListener(this);
                    break;
                case portrait:
                    setContentView(R.layout.phone_portrait);

                    SetOrientation(Orientation.portrait);
                    // Set up the raw links to the graphical components
                    rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);

                    startButton = (Button)findViewById(R.id.startButton);
                    startButton.setOnClickListener(this);
                    break;

            }
        }
        else if(aspectRatio == .75 || (aspectRatio > 1.2 && aspectRatio<1.4)) //4:3 screen ratio
        {
            Log.e("MyTag","is a tablet");

            switch(orientation)
            {
                case landscape:
                   // setContentView(R.layout.landscape);

                    SetOrientation(Orientation.landscape);
                    // Set up the raw links to the graphical components
                    rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);

                    startButton = (Button)findViewById(R.id.startButton);
                    startButton.setOnClickListener(this);
                    break;
                case portrait:
                   // setContentView(R.layout.portrait);

                    SetOrientation(Orientation.portrait);
                    // Set up the raw links to the graphical components
                    rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);

                    startButton = (Button)findViewById(R.id.startButton);
                    startButton.setOnClickListener(this);
                    break;

            }
        }
        else
        {
            setContentView(R.layout.error);
        }
    }
    private Float FindRatio()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return  ((float)metrics.heightPixels / (float)metrics.widthPixels);
    }


    public void onClick(View aview)
    {
        startProgress();
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    } //

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            SetOrientation(Orientation.landscape);
            // Set up the raw links to the graphical components
            rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);

            startButton = (Button)findViewById(R.id.startButton);
            startButton.setOnClickListener(this);
        }
        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            SetOrientation(Orientation.portrait);
            // Set up the raw links to the graphical components
            rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);

            startButton = (Button)findViewById(R.id.startButton);
            startButton.setOnClickListener(this);
        }
        else {
            setContentView(R.layout.error);
        }
    }

    private void SetOrientation(Orientation type)
    {
        if(aspectRatio >1.9 && aspectRatio <2|| aspectRatio > .5 && aspectRatio <.6) //galaxy s8
        {
            switch(type)
            {
                case landscape:
                    setContentView(R.layout.phone_landscape);
                    break;
                case portrait:
                    setContentView(R.layout.phone_portrait);
                    break;

            }
        }
        else if((aspectRatio == .75 )|| (aspectRatio == 12))
        {
            switch(type)
            {
                case landscape:
                    setContentView(R.layout.landscape);
                    break;
                case portrait:
                    setContentView(R.layout.portrait);
                    break;

            }
        }
        else
        {
            setContentView(R.layout.error);
        }
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                //
                // Throw away the first 2 header lines before parsing
                //
                //
                //
                inputLine = in.readLine();
                inputLine = in.readLine();
                while (((inputLine = in.readLine()) != null))
                {
                    if(!inputLine.startsWith("<description>") || inputLine.startsWith("<description>Recent"))
                        continue;


                    String[] information = inputLine.split(";");
                    String date = information[0];
                    String location = information[1];
                    String bearings = information[2];
                    String depth = information[3];
                    String magnitude = information[4];

                    String day = date.substring(31, 34);
                    String dayNum = date.substring(35,38);
                    String month = date.substring(39, 42);
                    String year = date.substring(42, 47);

                    Log.e("Day", day);
                    Log.e("num", dayNum);
                    Log.e("month", month);
                    Log.e("year", year);

                    result = result + inputLine;
                    //Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    rawDataDisplay.setText(result);
                }
            });
        }

    }

    class Earthquake
    {
        public Date date;
        public Time time;
        public String location;
        public float latitude;
        public float longitude;
        public float depth;
        public float magnitude;

        public Earthquake(Date dateOccurred, Time timeOccurred, String locationOccurred,
                          float latitudeOccurred, float longitudeOccurred,
                          float depthOccurred, float magnitudeOccurred)
        {
            date = dateOccurred;
            time = timeOccurred;
            location = locationOccurred;
            latitude = latitudeOccurred;
            longitude = longitudeOccurred;
            depth = depthOccurred;
            magnitude = magnitudeOccurred;
        }


    }

    class Date
    {
        public String dayName;
        public int dayNumber;
        public int year;

        public Date(String dName, int dNumber, int yr)
        {
             dayName = dName;
             dayNumber = dNumber;
             year = yr;
        }
    }

    class Time
    {
        public int hour;
        public int minutes;
        public int seconds;

        public  Time(int hourOccured, int minuteOccured, int secondOccured)
        {
            hour = hourOccured;
            minutes = minuteOccured;
            seconds = secondOccured;
        }
    }
}

