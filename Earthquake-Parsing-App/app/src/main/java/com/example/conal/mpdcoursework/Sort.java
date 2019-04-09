package com.example.conal.mpdcoursework;

import java.util.Comparator;

public class Sort {
    static class LocationNameAscending implements Comparator<MainActivity.Earthquake> {

        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            return a.location.compareTo(b.location);
        }
    }

    static class LocationNameDescending implements Comparator<MainActivity.Earthquake> {

        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            return b.location.compareTo(a.location);
        }
    }

    static class SortNorthMost implements Comparator<MainActivity.Earthquake> {
        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            float aLat = a.latitude * 1000;
            float bLat = b.latitude * 1000;

            return (int) bLat - (int) aLat;
        }
    }

    static class SortSouthMost implements Comparator<MainActivity.Earthquake> {
        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            float aLat = a.latitude * 1000;
            float bLat = b.latitude * 1000;

            return (int) aLat - (int) bLat;
        }
    }

    static class SortEastMost implements Comparator<MainActivity.Earthquake> {
        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            float aLong = a.longitude * 1000;
            float bLong = b.longitude * 1000;

            return (int) bLong - (int) aLong;
        }
    }

    static class SortWestMost implements Comparator<MainActivity.Earthquake> {
        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            float aLong = a.longitude * 1000;
            float bLong = b.longitude * 1000;

            return (int) aLong - (int) bLong;
        }
    }

    static class SortMagAscending implements Comparator<MainActivity.Earthquake> {
        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            float aMag = a.magnitude * 10;
            float bMag = b.magnitude * 10;

            return (int) aMag - (int) bMag;
        }
    }

    static class SortMagDescending implements Comparator<MainActivity.Earthquake> {
        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            float aMag = a.magnitude * 10;
            float bMag = b.magnitude * 10;

            return (int) bMag - (int) aMag;
        }
    }

    static class SortDepthAscending implements Comparator<MainActivity.Earthquake> {
        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            float aDepth = a.depth;
            float bDepth = b.depth;

            return (int) aDepth - (int) bDepth;
        }
    }

    static class SortDepthDescending implements Comparator<MainActivity.Earthquake> {
        public int compare(MainActivity.Earthquake a, MainActivity.Earthquake b) {
            float aDepth = a.depth;
            float bDepth = b.depth;

            return (int) bDepth - (int) aDepth;
        }
    }
}
