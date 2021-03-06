
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
import android.support.design.widget.NavigationView;
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
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Button startButton;
    private String result = null;
    private String url1 = "";
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    private enum Orientation {landscape, portrait}

    private float aspectRatio;


    private RecyclerView dataDisplay;
    private DataAdapter dataAdapter;
    private RecyclerView.LayoutManager dataLayoutMgr;

    private List<Earthquake> earthquakes;
    private NavigationView navigationView;
    private Menu menu;


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

        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        InitialiseRecycleView();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private Float FindRatio() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return ((float) metrics.heightPixels / (float) metrics.widthPixels);


    }

    public void onClick(View aview) {
        if (aview.getId() == R.id.startButton)
            startProgress();
        if (aview.getId() == R.id.infoButton)
            startActivity(new Intent(MainActivity.this, InformationWindow.class));
        if (aview.getId() == R.id.sortNorthButton)
            Sort(SortCondition.North, SortValue.Direction);
        if (aview.getId() == R.id.sortEastButton)
            Sort(SortCondition.East, SortValue.Direction);
        if (aview.getId() == R.id.sortSouthButton)
            Sort(SortCondition.South, SortValue.Direction);
        if (aview.getId() == R.id.sortWestButton)
            Sort(SortCondition.West, SortValue.Direction);


    }

    public void startProgress() {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String text = parent.getItemAtPosition(pos).toString();
        Log.e("Sort", text);
        if (parent.getId() == R.id.dataDropdown) {
            if (text.equals("Location"))
                dropDownValue = DropDownValues.Location;
            else if (text.equals("Date + Time"))
                dropDownValue = DropDownValues.DateTime;
            else if (text.equals("Magnitude"))
                dropDownValue = DropDownValues.Magnitude;
            else if (text.equals("Depth"))
                dropDownValue = DropDownValues.Depth;
        }

        int iD = parent.getId();
        Log.e("Sort", Integer.toString(iD));
        if (parent.getId() == R.id.sortConditionDropdown) {
            if (text.equals("Ascending"))
                dropDownCondition = DropDownConditions.Ascending;
            if (text.equals("Descending"))
                dropDownCondition = DropDownConditions.Descending;
        }

        DropDownSort();
    }

    void DropDownSort() {
        switch (dropDownCondition) {
            case Ascending:
                switch (dropDownValue) {
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
                switch (dropDownValue) {
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Toast toast = null;
        MenuItem item = null;

        switch (menuItem.getItemId()) {
            case R.id.northMenu:
                Sort(SortCondition.North, SortValue.Direction);
                toast = Toast.makeText(this, "Sorted by North Most", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.eastMenu:
                Sort(SortCondition.East, SortValue.Direction);
                toast = Toast.makeText(this, "Sorted by East Most", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.southMenu:
                Sort(SortCondition.South, SortValue.Direction);
                toast = Toast.makeText(this, "Sorted by South Most", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.westMenu:
                Sort(SortCondition.West, SortValue.Direction);
                toast = Toast.makeText(this, "Sorted by West Most", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.ascending:
                dropDownCondition = DropDownConditions.Ascending;
                item = menu.findItem(R.id.descending);
                item.setChecked(false);
                DropDownSort();
                return true;
            case R.id.descending:
                dropDownCondition = DropDownConditions.Descending;
                item = menu.findItem(R.id.ascending);
                item.setChecked(false);
                DropDownSort();
                return true;
            case R.id.locationSort:
                dropDownValue = DropDownValues.Location;
                DropDownSort();
                toast = Toast.makeText(this, "Sorted by Location", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.magnitudeSort:
                dropDownValue = DropDownValues.Magnitude;
                DropDownSort();
                toast = Toast.makeText(this, "Sorted by Magnitude", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.depthSort:
                dropDownValue = DropDownValues.Depth;
                DropDownSort();
                toast = Toast.makeText(this, "Sorted by Depth", Toast.LENGTH_SHORT);
                toast.show();
                return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                Collections.sort(earthquakes, new SortNorthMost());
                break;
            case East:
                Collections.sort(earthquakes, new SortEastMost());
                break;
            case South:
                Collections.sort(earthquakes, new SortSouthMost());
                break;
            case West:
                Collections.sort(earthquakes, new SortWestMost());
                break;
            case Ascending:
                switch (value) {
                    case LocationName:
                        Collections.sort(earthquakes, new LocationNameAscending());
                        break;
                    case DateTime:
                        // Collections.sort(earthquakes, new )
                        break;
                    case Magnitude:
                        Collections.sort(earthquakes, new SortMagAscending());
                        break;
                    case Depth:
                        Collections.sort(earthquakes, new SortDepthAscending());

                        break;
                }
                break;
            case Descending:
                switch (value) {
                    case LocationName:
                        Collections.sort(earthquakes, new LocationNameDescending());
                        break;
                    case DateTime:

                        break;
                    case Magnitude:
                        Collections.sort(earthquakes, new SortMagDescending());
                        break;
                    case Depth:
                        Collections.sort(earthquakes, new SortDepthDescending());
                        break;
                }
                break;
        }
        InitialiseRecycleView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;

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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.northMenu:
                Sort(SortCondition.North, SortValue.Direction);
                return true;
            case R.id.eastMenu:
                Sort(SortCondition.East, SortValue.Direction);
                return true;
            case R.id.southMenu:
                Sort(SortCondition.South, SortValue.Direction);
                return true;
            case R.id.westMenu:
                Sort(SortCondition.West, SortValue.Direction);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long)
        {
            String text = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        }*/
    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;
        private XmlPullParserFactory factory = null;
        private XmlPullParser parser = null;
        private String text = null;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {

            URL aurl;
            URLConnection yc;


            Log.e("MyTag", "in run");

            try {
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
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagName = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if (tagName.equalsIgnoreCase("description")) {
                                TextParse();
                                //InitialiseRecycleView();
                            }
                            break;

                    }
                    eventType = parser.next();

                }
            } catch (IOException ae) {
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

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    //  setTextViews();
                }
            });


        }

        void TextParse() {
            if (text.contains(";")) {
                String[] information = text.split(";");
                String date = information[0];
                String location = information[1];
                String bearings = information[2];
                String depth = information[3];
                String magnitude = information[4];


                //Date
                String day = date.substring(18, 21);
                String dayNum = date.substring(22, 25);
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


    }

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> implements Filterable {
        List<Earthquake> earthquakes;
        List<Earthquake> earthquakesComplete;
        Context context;

        public DataAdapter(List<Earthquake> earthquakes, Context context) {
            this.earthquakes = earthquakes;
            earthquakesComplete = new ArrayList<>(earthquakes);

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
            viewHolder.dateView.setText("Date: " + String.format("%.0f", date.dayNumber) + "/" + date.month + "/" + String.format("%.0f", date.year) + " ");
            Time time = earthquake.time;
            viewHolder.timeView.setText("Time: " + String.format("%.0f", time.hour) + ":" + String.format("%.0f", time.minutes) + ":" + String.format("%.0f", time.seconds) + " ");
            viewHolder.locationView.setText(earthquake.location);
        }

        @Override
        public int getItemCount() {
            return earthquakes.size();
        }

        @Override
        public Filter getFilter() {
            return earthquakeFilter;
        }

        private Filter earthquakeFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Earthquake> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0)
                    filteredList.addAll(earthquakesComplete);
                else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Earthquake e : earthquakesComplete) {
                        if (e.location.toLowerCase().contains(filterPattern))
                            filteredList.add(e);
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                earthquakes.clear();
                earthquakes.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };

        public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
            //2
            public TextView dateView;
            public TextView timeView;
            public TextView locationView;
            public Button infoButton;
            Earthquake earthquake;

            public ViewHolder(@NonNull View itemView, Earthquake aearthquake) {
                super(itemView);
                earthquake = aearthquake;
                dateView = itemView.findViewById(R.id.dateDisplay);
                timeView = itemView.findViewById(R.id.timeDisplay);
                locationView = itemView.findViewById(R.id.locationDisplay);
                infoButton = itemView.findViewById(R.id.infoButton);
                infoButton.setOnClickListener(this);

            }

            public void onClick(View view) {
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
                intent.putExtra("magnitude", String.format("%.1f", earthquake.magnitude));
                startActivity(intent);


            }
        }

    }

    class Earthquake {
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

    class Date {
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

    class Time {
        public float hour;
        public float minutes;
        public float seconds;

        public Time(float hourOccured, float minuteOccured, float secondOccured) {
            hour = hourOccured;
            minutes = minuteOccured;
            seconds = secondOccured;
        }
    }

    class LocationNameAscending implements Comparator<Earthquake> {

        public int compare(Earthquake a, Earthquake b) {
            return a.location.compareTo(b.location);
        }
    }

    class LocationNameDescending implements Comparator<Earthquake> {

        public int compare(Earthquake a, Earthquake b) {
            return b.location.compareTo(a.location);
        }
    }

    class SortNorthMost implements Comparator<Earthquake> {
        public int compare(Earthquake a, Earthquake b) {
            float aLat = a.latitude * 1000;
            float bLat = b.latitude * 1000;

            return (int) bLat - (int) aLat;
        }
    }

    class SortSouthMost implements Comparator<Earthquake> {
        public int compare(Earthquake a, Earthquake b) {
            float aLat = a.latitude * 1000;
            float bLat = b.latitude * 1000;

            return (int) aLat - (int) bLat;
        }
    }

    class SortEastMost implements Comparator<Earthquake> {
        public int compare(Earthquake a, Earthquake b) {
            float aLong = a.longitude * 1000;
            float bLong = b.longitude * 1000;

            return (int) bLong - (int) aLong;
        }
    }

    class SortWestMost implements Comparator<Earthquake> {
        public int compare(Earthquake a, Earthquake b) {
            float aLong = a.longitude * 1000;
            float bLong = b.longitude * 1000;

            return (int) aLong - (int) bLong;
        }
    }

    class SortMagAscending implements Comparator<Earthquake> {
        public int compare(Earthquake a, Earthquake b) {
            float aMag = a.magnitude * 10;
            float bMag = b.magnitude * 10;

            return (int) aMag - (int) bMag;
        }
    }

    class SortMagDescending implements Comparator<Earthquake> {
        public int compare(Earthquake a, Earthquake b) {
            float aMag = a.magnitude * 10;
            float bMag = b.magnitude * 10;

            return (int) bMag - (int) aMag;
        }
    }

    class SortDepthAscending implements Comparator<Earthquake> {
        public int compare(Earthquake a, Earthquake b) {
            float aDepth = a.depth;
            float bDepth = b.depth;

            return (int) aDepth - (int) bDepth;
        }
    }

    class SortDepthDescending implements Comparator<Earthquake> {
        public int compare(Earthquake a, Earthquake b) {
            float aDepth = a.depth;
            float bDepth = b.depth;

            return (int) bDepth - (int) aDepth;
        }
    }

}


