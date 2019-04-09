
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
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.util.DisplayMetrics;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
Conall McLuskie
S1509449
 */
public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private String result = null;
    private String url1 = "";
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    private enum Orientation {landscape, portrait}

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
    private DataAdapter dataAdapter;
    private RecyclerView.LayoutManager dataLayoutMgr;

    private List<Earthquake> earthquakes;


    private Button northSortButton;
    private Button eastSortButton;
    private Button southSortButton;
    private Button westSortButton;
    private Spinner dropDownValues;
    private Spinner dropDownValueCondition;

    private enum DropDownValues {Location, DateTime, Magnitude, Depth}

    DropDownValues dropDownValue = DropDownValues.Location;

    private enum DropDownConditions {Ascending, Descending}

    private DropDownConditions dropDownCondition = DropDownConditions.Ascending;

    private Toolbar sortBar = null;
    private DrawerLayout drawer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startProgress();

        earthquakes = new ArrayList<Earthquake>();
        aspectRatio = FindRatio();


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            InitialiseView(Orientation.landscape);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            InitialiseView(Orientation.portrait);
        }


        // More Code goes here
    }

    void InitialiseRecycleView() {
        if (dataDisplay != null)
            dataDisplay = null;

        dataDisplay = findViewById(R.id.dataDisplay);
        dataDisplay.setHasFixedSize(true);
        dataLayoutMgr = new LinearLayoutManager(this);
        dataDisplay.setLayoutManager(dataLayoutMgr);
        dataAdapter = new DataAdapter(earthquakes, this);
        dataDisplay.setAdapter(dataAdapter);
    }

    void InitialiseView(Orientation orientation) {
        Log.e("Window", Float.toString(aspectRatio));
        if (aspectRatio > 1.6 && aspectRatio < 1.7 || aspectRatio > .5 && aspectRatio < .6) {
            switch (orientation) {
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
        } else if (aspectRatio == .75 || (aspectRatio > 1.2 && aspectRatio < 1.4)) //4:3 screen ratio
        {

            switch (orientation) {
                case landscape:

                    SetOrientation(Orientation.landscape);

                    InitialiseRecycleView();
                    break;
                case portrait:
                    SetOrientation(Orientation.portrait);
                    InitialiseRecycleView();
                    break;

            }
        } else {
            setContentView(R.layout.error);
        }
        sortBar = findViewById(R.id.toolbar);
        setSupportActionBar(sortBar);

        drawer = findViewById(R.id.sortDrawer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, sortBar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        InitialiseRecycleView();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    private Float FindRatio() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return ((float) metrics.heightPixels / (float) metrics.widthPixels);


    }

    public void onClick(View aview) {


    }

    public void startProgress() {
        // Run network access on a separate thread;
        new Thread(new Parse(urlSource)).start();
    } //

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            startProgress();
            SetOrientation(Orientation.landscape);
            InitialiseRecycleView();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            startProgress();
            SetOrientation(Orientation.portrait);
            InitialiseRecycleView();

        } else {
            setContentView(R.layout.error);
        }
    }

    void DropDownSort()
    {
        switch (dropDownCondition)
        {
            case Ascending:
                switch (dropDownValue)
                {
                    case Location:
                        Sort(SortCondition.Ascending, SortValue.LocationName);
                        break;
                    case Magnitude:
                        Sort(SortCondition.Ascending, SortValue.Magnitude);
                        break;
                    case DateTime:
                        Sort(SortCondition.Ascending, SortValue.DateTime);
                        break;
                    case Depth:
                        Sort(SortCondition.Ascending, SortValue.Depth);
                        break;
                        default:
                            Log.e("Sort", "Default");
                            break;
                }
                break;
            case Descending:
                switch (dropDownValue)
                {
                    case Location:
                        Sort(SortCondition.Descending, SortValue.LocationName);
                        break;
                    case Magnitude:
                        Sort(SortCondition.Descending, SortValue.Magnitude);
                        break;
                    case DateTime:
                        Sort(SortCondition.Descending, SortValue.DateTime);
                        break;
                    case Depth:
                        Sort(SortCondition.Descending, SortValue.Depth);
                        break;
                }
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void SetOrientation(Orientation type) {
        if (aspectRatio > 1.6 && aspectRatio < 1.7 || aspectRatio > .5 && aspectRatio < .6) //16:9
        {
            switch (type) {
                case landscape:
                    setContentView(R.layout.phone_landscape);
                    break;
                case portrait:
                    setContentView(R.layout.phone_portrait);
                    break;

            }
        } else if ((aspectRatio == .75) || (aspectRatio > 1.2 && aspectRatio < 1.4)) {
            switch (type) {
                case landscape:
                    setContentView(R.layout.landscape);

                    break;
                case portrait:
                    setContentView(R.layout.portrait);
                    break;

            }
        } else {
            setContentView(R.layout.error);
        }

    }

    private enum SortCondition {
        North, East, South, West,
        Ascending, Descending
    }

    private enum SortValue {
        LocationName, DateTime,
        Magnitude, Depth, Direction
    }

    private void Sort(SortCondition condition, SortValue value) {
        switch (condition) {
            case North:
                Collections.sort(earthquakes, new Sort.SortNorthMost());
                break;
            case East:
                Collections.sort(earthquakes, new Sort.SortEastMost());
                break;
            case South:
                Collections.sort(earthquakes, new Sort.SortSouthMost());
                break;
            case West:
                Collections.sort(earthquakes, new Sort.SortWestMost());
                break;
            case Ascending:
                switch (value) {
                    case LocationName:
                        Collections.sort(earthquakes, new Sort.LocationNameAscending());
                        break;
                    case DateTime:
                        // Collections.sort(earthquakes, new )
                        break;
                    case Magnitude:
                        Collections.sort(earthquakes, new Sort.SortMagAscending());
                        break;
                    case Depth:
                        						Collections.sort(earthquakes, new Sort.SortDepthAscending());

                        break;
                }
                break;
            case Descending:
                switch (value) {
                    case LocationName:
                        Collections.sort(earthquakes, new Sort.LocationNameDescending());
                        break;
                    case DateTime:

                        break;
                    case Magnitude:
                        Collections.sort(earthquakes, new Sort.SortMagDescending());
                        break;
                    case Depth:
						Collections.sort(earthquakes, new Sort.SortDepthDescending());
                        break;
                }
                break;
        }
        InitialiseRecycleView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                dataAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.northMenu:
                Sort(SortCondition.North, SortValue.Direction);
                return true;
            case  R.id.eastMenu:
                Sort(SortCondition.East, SortValue.Direction);
                return true;
            case  R.id.southMenu:
                Sort(SortCondition.South, SortValue.Direction);
                return true;
            case  R.id.westMenu:
                Sort(SortCondition.West, SortValue.Direction);
                return true;



                        }
        return super.onOptionsItemSelected(item);
    }

    public static class Earthquake {
        public Date date;
        public Time time;
        public String location;
        public float latitude;
        public float longitude;
        public float depth;
        public float magnitude;


        public Earthquake(Date dateOccurred, Time timeOccurred, String locationOccurred,
                          float latitudeOccurred, float longitudeOccurred,
                          float depthOccurred, float magnitudeOccurred) {
            date = dateOccurred;
            time = timeOccurred;
            location = locationOccurred;
            latitude = latitudeOccurred;
            longitude = longitudeOccurred;
            depth = depthOccurred;
            magnitude = magnitudeOccurred;
        }


    }

    public static class Date {
        public String dayName;
        public float dayNumber;
        public String month;
        public float year;

        public Date(String dName, float dNumber, String monthName, float yr) {
            dayName = dName;
            dayNumber = dNumber;
            month = monthName;
            year = yr;
        }
    }

    public static class Time {
        public float hour;
        public float minutes;
        public float seconds;

        public Time(float hourOccured, float minuteOccured, float secondOccured) {
            hour = hourOccured;
            minutes = minuteOccured;
            seconds = secondOccured;
        }
    }



}


