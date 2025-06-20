package com.example.smartgrid_iqra_fyp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

public class Graph extends AppCompatActivity {
    LineGraphSeries<DataPoint> series;
    double[] dataValues = {
            30.0, 31.0, 32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0,
            40.0, 41.0, 42.0, 43.0, 44.0, 45.0, 46.0, 47.0, 48.0, 49.0,
            50.0, 51.0, 52.0, 53.0, 54.0, 55.0, 56.0, 57.0, 58.0, 59.0
    };
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference node,day0node,day1node,day2node,day3node,day4node,day5node,day6node,day7node,day8node,day9node,day10node,day11node,day12node,day13node,day14node,day15node,
            day16node,day17node,day18node,day19node,day20node,day21node,day22node,day23node,day24node,day25node,day26node,day27node,day28node,day29node;
    float[] units = new float[30]; // All elements default to 0.0f
    int loadedCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        node = db.getReference("DataLog");
        loadedCount = 0;

        setupListenerForDay(0, node.child("day0"));
        setupListenerForDay(1, node.child("day1"));
        setupListenerForDay(2, node.child("day2"));
        setupListenerForDay(3, node.child("day3"));
        setupListenerForDay(4, node.child("day4"));
        setupListenerForDay(5, node.child("day5"));
        setupListenerForDay(6, node.child("day6"));
        setupListenerForDay(7, node.child("day7"));
        setupListenerForDay(8, node.child("day8"));
        setupListenerForDay(9, node.child("day9"));
        setupListenerForDay(10, node.child("day10"));
        setupListenerForDay(11, node.child("day11"));
        setupListenerForDay(12, node.child("day12"));
        setupListenerForDay(13, node.child("day13"));
        setupListenerForDay(14, node.child("day14"));
        setupListenerForDay(15, node.child("day15"));
        setupListenerForDay(16, node.child("day16"));
        setupListenerForDay(17, node.child("day17"));
        setupListenerForDay(18, node.child("day18"));
        setupListenerForDay(19, node.child("day19"));
        setupListenerForDay(20, node.child("day20"));
        setupListenerForDay(21, node.child("day21"));
        setupListenerForDay(22, node.child("day22"));
        setupListenerForDay(23, node.child("day23"));
        setupListenerForDay(24, node.child("day24"));
        setupListenerForDay(25, node.child("day25"));
        setupListenerForDay(26, node.child("day26"));
        setupListenerForDay(27, node.child("day27"));
        setupListenerForDay(28, node.child("day28"));
        setupListenerForDay(29, node.child("day29"));

    }

    private void setupListenerForDay(int index, DatabaseReference dayNode) {
        dayNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                try {
                    units[index] = Float.parseFloat(value);
                    Log.e("MyApp", "Day" + index + " : " + units[index]);
                } catch (NumberFormatException e) {
                    Log.e("MyApp", "Invalid Day " + index + ": " + e.getMessage());
                    units[index] = 0;
                }
                loadedCount++;
                if (loadedCount == 30) {
                    runOnUiThread(() -> OpenGraph());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Graph.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }


//    private void OpenGraph(){
//        double x , y = 0;
//        GraphView graph = (GraphView) findViewById(R.id.graph1);
//        series = new LineGraphSeries<DataPoint>();
//
//        for (int i = 0; i < units.length; i++) {
//            series.appendData(new DataPoint(i, units[i]), true, units.length);
//        }
//        graph.addSeries(series);
//
//        // ✅ Ensure labels are visible and readable
//        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
//        graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
//        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
//        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
//
//        // ✅ Set viewport for X and Y to ensure labels are in range
//        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getViewport().setMinX(0);
//        graph.getViewport().setMaxX(29);
//
//        graph.getViewport().setYAxisBoundsManual(true);
//        graph.getViewport().setMinY(0);
//        graph.getViewport().setMaxY(65);
//    }

    private void OpenGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph1);
        series = new LineGraphSeries<>();

        for (int i = 0; i < units.length; i++) {
            series.appendData(new DataPoint(i, units[i]), true, units.length);
        }
        graph.addSeries(series);

        // Add tap listener to show Toast with X and Y values
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                int x = (int) dataPoint.getX();
                double y = dataPoint.getY();
                String formatted = String.format("%.2f", y);  // "3.14"
                Toast.makeText(Graph.this, "Day: " + x + ", Units: " + formatted, Toast.LENGTH_SHORT).show();
            }
        });



        // Viewport setup
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(30);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(60);

        // Renderer setup
        GridLabelRenderer renderer = graph.getGridLabelRenderer();

//        renderer.setHorizontalAxisTitleTextSize(50f);
//        renderer.setVerticalAxisTitleTextSize(50f);
//        renderer.setLabelVerticalWidth(50);
//        renderer.setPadding(50);  // VERY important!
        renderer.setHorizontalLabelsVisible(true);
        renderer.setVerticalLabelsVisible(true);
        renderer.setHorizontalLabelsColor(Color.BLACK);
        renderer.setVerticalLabelsColor(Color.BLACK);

//        renderer.setTextSize(30f);
//        renderer.setHorizontalAxisTitle("Days");
//        renderer.setVerticalAxisTitle("Units");

        // Force manual steps for Y-axis
        renderer.setNumVerticalLabels(13); // (e.g., 0 to 60 with step size of ~5)

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


