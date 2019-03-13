
//
// Name                 Conall McLuskie
// Student ID           s1509449
// Programme of Study   Computer Games Software Development
//

// Update the package name to include your Student Identifier
package com.example.conal.mpdcoursework;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.DisplayMetrics;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/*
Conall McLuskie
S1509449
 */
public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private Button startButton;
    private String result = null;
    private String url1="";
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private enum Orientation{ landscape, portrait}
    private float aspectRatio;

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

    private RecyclerView dataDisplay;
    private RecyclerView.Adapter dataAdapter;
    private RecyclerView.LayoutManager dataLayoutMgr;

    private List<Earthquake> earthquakes;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startProgress();

        earthquakes = new ArrayList<Earthquake>();
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

    void InitialiseRecycleView()
    {
        dataDisplay = (RecyclerView)findViewById(R.id.dataDisplay);
        dataDisplay.setHasFixedSize(true);
        dataLayoutMgr = new LinearLayoutManager(this);
        dataDisplay.setLayoutManager(dataLayoutMgr);
        dataAdapter = new DataAdapter(earthquakes, this);
        dataDisplay.setAdapter(dataAdapter);
    }

    void InitialiseView(Orientation orientation)
    {
        Log.e("Window", Float.toString(aspectRatio));
        if(aspectRatio >1.6 && aspectRatio <1.7|| aspectRatio > .5 && aspectRatio <.6)
        {
            switch(orientation)
            {
                case landscape:
                    setContentView(R.layout.phone_landscape);
                    SetOrientation(Orientation.landscape);
                    InitialiseRecycleView();
                    break;
                case portrait:
                    setContentView(R.layout.phone_portrait);
                    SetOrientation(Orientation.portrait);
                    InitialiseRecycleView();
                    break;

            }
        }
        else if(aspectRatio == .75 || (aspectRatio > 1.2 && aspectRatio<1.4)) //4:3 screen ratio
        {

            switch(orientation)
            {
                case landscape:

                    SetOrientation(Orientation.landscape);
                    InitialiseRecycleView();
                    break;
                case portrait:
                    SetOrientation(Orientation.portrait);
                    InitialiseRecycleView();
                    break;

            }
        }
        else
        {
            setContentView(R.layout.error);
        }
    }
    private Float FindRatio() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return ((float) metrics.heightPixels / (float) metrics.widthPixels);


    }

    public void onClick(View aview)
    {
        if(aview.getId() == R.id.startButton)
        startProgress();
        if(aview.getId() == R.id.infoButton)
            startActivity(new Intent(MainActivity.this, InformationWindow.class));

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
            startProgress();
            SetOrientation(Orientation.landscape);
            InitialiseRecycleView();

        }
        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            startProgress();
            SetOrientation(Orientation.portrait);
            InitialiseRecycleView();

        }
        else {
            setContentView(R.layout.error);
        }
    }

    private void SetOrientation(Orientation type)
    {
        if(aspectRatio >1.6 && aspectRatio <1.7 || aspectRatio > .5 && aspectRatio <.6) //16:9
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
        else if((aspectRatio == .75 )|| (aspectRatio > 1.2 && aspectRatio<1.4))
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
        private XmlPullParserFactory factory= null;
        private XmlPullParser parser = null;
        private String text = null;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;



            Log.e("MyTag","in run");

            try
            {
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware((true));
                parser = Xml.newPullParser();

                aurl = new URL(url);
                yc = aurl.openConnection();
                parser.setInput(yc.getInputStream(), null);
                //
                // Throw away the first 2 header lines before parsing
                //
                parser.nextTag();
                parser.nextTag();
                //inputLine = in.readLine();
                //inputLine = in.readLine();
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    String tagName = parser.getName();
                    switch (eventType)
                    {
                        case XmlPullParser.START_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if(tagName.equalsIgnoreCase("description")) {
                                TextParse();
                                //InitialiseRecycleView();
                            }
                            break;

                    }
                   eventType =  parser.next();

                }
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
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
                  //  setTextViews();
                }
            });


        }

        void TextParse()
        {
            if (text.contains(";"))
            {
                String[] information = text.split(";");
                String date = information[0];
                String location = information[1];
                String bearings = information[2];
                String depth = information[3];
                String magnitude = information[4];


                //Date
                String day = date.substring(18, 21);
                String dayNum = date.substring(22 , 25);
                float dayNumValue = Float.parseFloat(dayNum);
                String month = date.substring(26, 29);
                String year = date.substring(30, 35);
                float yearValue = Float.parseFloat(year);

                //Time
                String hour = date.substring(35, 37);
                float hourValue = Float.parseFloat(hour);
                String minute = date.substring(38, 40);
                float minuteValue = Float.parseFloat(minute);
                String second = date.substring(41, 43);
                float secondValue = Float.parseFloat(second);


                Log.e("Info", day);
                Log.e("Info", Float.toString(dayNumValue));
                Log.e("Info", month);
                Log.e("Info", year);

                Log.e("Info", hour);
                Log.e("Info", minute);
                Log.e("Info", second);

                Date dateClass = new Date(day, dayNumValue, month, yearValue);
                Time timeClass = new Time(hourValue, minuteValue, secondValue);

                //Location
                String locationName = location.substring(11, location.length()).trim();
                Log.e("Info", locationName);
                String latitude = bearings.substring(11, 17);
                float latitudeValue = Float.parseFloat(latitude);
                String longitude = bearings.substring(18, bearings.length());
                float longitudeValue = Float.parseFloat(longitude);
                Log.e("Info", latitude);
                Log.e("Info", longitude);
                //Power
                String depthValue = depth.substring(8, 10);
                float depthFloat = Float.parseFloat(depthValue);
                Log.e("Info", depthValue);
                String magnitudeValue = magnitude.substring(13, 16);
                float magnitudeFloat = Float.parseFloat(magnitudeValue);
                Log.e("Info", magnitudeValue);

                Earthquake earthquake = new Earthquake(dateClass, timeClass,
                        locationName, latitudeValue, longitudeValue,
                        depthFloat, magnitudeFloat);

                earthquakes.add(earthquake);
            }
        }

        void DisplayList()
        {
           /* for(int i = 0; i < earthquakes.size(); i++)
            {

                Earthquake earthquake = earthquakes.get(i);
                Date date = earthquake.date;
                Time time = earthquake.time;

                if(dateString == null)
                {
                    NullNullifier();
                }

                dateString +=  date.dayName + " " +
                        String.format("%.0f", date.dayNumber) + "/" + date.month + "/" + String.format("%.0f", date.year)+ " " + "\n";
                timeString +=
                        String.format("%.0f", time.hour) + ":" + String.format("%.0f", time.minutes)+ ":" + String.format("%.0f", time.seconds)+ " " + "\n";
                locationString +=  earthquake.location + " " + "\n";
                //Shit Under Here doesnt display
                latString += String.format("%.2f", earthquake.latitude) + " " + "\n";
                longString +=  String.format("%.2f", earthquake.longitude) + " " + "\n";
                depthString +=  String.format("%.0f", earthquake.depth) + "km " + "\n";
                latString += Float.toString(earthquake.latitude) + " " + "\n";
                longString += Float.toString(earthquake.longitude) + " " + "\n";
                depthString += Float.toString(earthquake.depth) + " " + "\n";
                //this displays
                 magString +=  String.format("%.0f", earthquake.magnitude) + " " + "\n";


            }*/
           InitialiseRecycleView();
        }

        void NullNullifier()
        {
            dateString = "";
            timeString="";
            locationString = "";
            latString = "";
            longString = "";
            depthString = "";
            magString = "";
        }
    }

    private class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>
    {
        List<Earthquake> earthquakes;
        Context context;

        public DataAdapter(List<Earthquake> earthquakes, Context context) {
            this.earthquakes = earthquakes;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //1
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.earthquake_layout, viewGroup, false);
            return new ViewHolder(v, earthquakes.get(i));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            Earthquake earthquake = earthquakes.get(i);
//3
            viewHolder.earthquake = earthquake;
            Date date = earthquake.date;
            viewHolder.dateView.setText("Date: "+ String.format("%.0f", date.dayNumber) + "/" + date.month + "/" + String.format("%.0f", date.year) + " ");
            Time time = earthquake.time;
            viewHolder.timeView.setText("Time: "+ String.format("%.0f", time.hour) + ":" + String.format("%.0f", time.minutes) + ":" + String.format("%.0f", time.seconds) + " ");
            viewHolder.locationView.setText( earthquake.location);
        }

        @Override
        public int getItemCount() {
            return earthquakes.size();
        }

        public class ViewHolder extends  RecyclerView.ViewHolder implements OnClickListener
       {
           //2
            public TextView dateView;
            public TextView timeView;
            public TextView locationView;
           public Button infoButton;
           Earthquake earthquake;

           public ViewHolder(@NonNull View itemView, Earthquake aearthquake) {
               super(itemView);
               earthquake = aearthquake;
               dateView = (TextView)itemView.findViewById(R.id.dateDisplay);
               timeView = (TextView)itemView.findViewById(R.id.timeDisplay);
               locationView = (TextView) itemView.findViewById(R.id.locationDisplay);
               infoButton = (Button) itemView.findViewById(R.id.infoButton);
               infoButton.setOnClickListener(this);

           }

           public void onClick(View view){
               Intent intent = new Intent(MainActivity.this, InformationWindow.class);
               Date date = earthquake.date;
               Time time = earthquake.time;
               Log.e("Window", earthquake.location);
               intent.putExtra("date", String.format("%.0f", date.dayNumber) + "/" + date.month + "/" + String.format("%.0f", date.year));
               intent.putExtra("time", String.format("%.0f", time.hour) + ":" + String.format("%.0f", time.minutes) + ":" + String.format("%.0f", time.seconds));
               intent.putExtra("location", earthquake.location);
               intent.putExtra("latitude", String.format("%.3f", earthquake.latitude));
               intent.putExtra("longitude", String.format("%.3f", earthquake.longitude));
               intent.putExtra("depth", String.format("%.0f", earthquake.depth));
               intent.putExtra("magnitude", dateString.format("%.1f", earthquake.magnitude));
               startActivity(intent);


           }
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
        public float dayNumber;
        public String month;
        public float year;

        public Date(String dName, float dNumber, String monthName, float yr)
        {
             dayName = dName;
             dayNumber = dNumber;
             month = monthName;
             year = yr;
        }
    }

    class Time
    {
        public float hour;
        public float minutes;
        public float seconds;

        public  Time(float hourOccured, float minuteOccured, float secondOccured)
        {
            hour = hourOccured;
            minutes = minuteOccured;
            seconds = secondOccured;
        }
    }
}


