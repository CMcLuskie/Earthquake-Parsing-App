package com.example.conal.mpdcoursework;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

public  class EarthquakeList extends Fragment
{

    List<MainActivity.Earthquake> earthquakes;
    private RecyclerView dataDisplay;
    private DataAdapter dataAdapter;
    private RecyclerView.LayoutManager dataLayoutMgr;

    public static MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.earthquake_list, container, false);
    }


    void InitialiseRecycleView() {
        if (dataDisplay != null)
            dataDisplay = null;
        dataDisplay = findViewById(R.id.dataDisplay);
        dataDisplay.setHasFixedSize(true);
        dataLayoutMgr = new LinearLayoutManager(earthquakeList.getContext());
        dataDisplay.setLayoutManager(dataLayoutMgr);
        dataAdapter = new DataAdapter(earthquakes, earthquakeList.getContext());
        dataDisplay.setAdapter(dataAdapter);
    }
}

 class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> implements Filterable {
    List<MainActivity.Earthquake> earthquakes;
    List<MainActivity.Earthquake> earthquakesComplete;
    Context context;

    public DataAdapter(List<MainActivity.Earthquake> earthquakes, Context context) {
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
        MainActivity.Earthquake earthquake = earthquakes.get(i);
//3
        viewHolder.earthquake = earthquake;
        MainActivity.Date date = earthquake.date;
        viewHolder.dateView.setText("Date: " + String.format("%.0f", date.dayNumber) + "/" + date.month + "/" + String.format("%.0f", date.year) + " ");
        MainActivity.Time time = earthquake.time;
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
            List<MainActivity.Earthquake> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0)
                filteredList.addAll(earthquakesComplete);
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (MainActivity.Earthquake e : earthquakesComplete) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //2
        public TextView dateView;
        public TextView timeView;
        public TextView locationView;
        public Button infoButton;
        MainActivity.Earthquake earthquake;

        public ViewHolder(@NonNull View itemView, MainActivity.Earthquake aearthquake) {
            super(itemView);
            earthquake = aearthquake;
            dateView = itemView.findViewById(R.id.dateDisplay);
            timeView = itemView.findViewById(R.id.timeDisplay);
            locationView = itemView.findViewById(R.id.locationDisplay);
            infoButton = itemView.findViewById(R.id.infoButton);
            infoButton.setOnClickListener(this);

        }

        public void onClick(View view) {
//            Intent intent = new Intent(MainActivity.this, InformationWindow.class);
//            MainActivity.Date date = earthquake.date;
//            MainActivity.Time time = earthquake.time;
//            Log.e("Window", earthquake.location);
//            intent.putExtra("date", String.format("%.0f", date.dayNumber) + "/" + date.month + "/" + String.format("%.0f", date.year));
//            intent.putExtra("time", String.format("%.0f", time.hour) + ":" + String.format("%.0f", time.minutes) + ":" + String.format("%.0f", time.seconds));
//            intent.putExtra("location", earthquake.location);
//            intent.putExtra("latitude", String.format("%.3f", earthquake.latitude));
//            intent.putExtra("longitude", String.format("%.3f", earthquake.longitude));
//            intent.putExtra("depth", String.format("%.0f", earthquake.depth));
//            intent.putExtra("magnitude", String.format("%.1f", earthquake.magnitude));
//            startActivity(intent);


        }
    }

}

