package com.example.smartgrid_iqra_fyp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Battery extends AppCompatActivity {
    TextView mainread;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference node, node2, ampsnode, powernode, voltnode, loadnode,swnode;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        TextView amps = (TextView) findViewById(R.id.amps);
        mainread = (TextView) findViewById(R.id.tagline);
//        TextView power = (TextView) findViewById(R.id.power);
        TextView volt = (TextView) findViewById(R.id.acvolt);
        img = (ImageView) findViewById(R.id.imageView1);

        node = db.getReference("Data");
        ampsnode = node.child("BatteryAmps");
        powernode = node.child("BatteryPower");
        voltnode = node.child("BatteryVolt");
        loadnode = node.child("Batterystate");

        ampsnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                amps.setText(value + " A");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Battery.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

//        powernode.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue().toString();
//                power.setText(value + " W");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Toast.makeText(Battery.this, "Connection Error", Toast.LENGTH_LONG).show();
//            }
//        });

        voltnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                volt.setText(value + " V");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Battery.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        loadnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class); //load status
                if (value.equals("ON")) {
                    img.setImageResource(R.drawable.onn);
                } else if (value.equals("OFF")) {
                    img.setImageResource(R.drawable.offf);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Battery.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });


        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (((Boolean) snapshot.getValue(Boolean.class)).booleanValue()) {
                    Battery.this.mainread.setText("INTERNET CONNECTED ");
                } else {
                    Battery.this.mainread.setText("INTERNET CONNECTION FAILED");
                }
            }

            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}