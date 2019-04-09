package com.example.conal.mpdcoursework;

import android.util.Log;
import android.util.Xml;

import com.example.conal.mpdcoursework.MainActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Parse implements Runnable {

    private String url;
    private XmlPullParserFactory factory = null;
    private XmlPullParser parser = null;
    private String text = null;
    private List<MainActivity.Earthquake> earthquakes;

    public Parse(String aurl) {
        url = aurl;
        earthquakes = new ArrayList<>();
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

            MainActivity.Date dateClass = new MainActivity.Date(day, dayNumValue, month, yearValue);
            MainActivity.Time timeClass = new MainActivity.Time(hourValue, minuteValue, secondValue);

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

            MainActivity.Earthquake earthquake = new MainActivity.Earthquake(dateClass, timeClass,
                    locationName, latitudeValue, longitudeValue,
                    depthFloat, magnitudeFloat);

            earthquakes.add(earthquake);
        }
    }

    public List<MainActivity.Earthquake> getEarthquakes()
    {
        return earthquakes;
    }
}
