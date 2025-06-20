package com.example.smartgrid_iqra_fyp;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
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

public class LoadA extends AppCompatActivity {
    TextView mainread;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference node, node2, ampsnode, powernode, voltnode, loadnode,swnode;
    private ImageView img;
    private Switch load1SW;
    float amp,volts,pow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        TextView amps = (TextView) findViewById(R.id.amps);
        mainread = (TextView) findViewById(R.id.tagline);
        TextView power = (TextView) findViewById(R.id.power);
        TextView volt = (TextView) findViewById(R.id.acvolt);
        img = (ImageView) findViewById(R.id.imageView1);
        load1SW = (Switch) findViewById(R.id.switch1);

        node2 = db.getReference("Buttons");
        node = db.getReference("Data");
        ampsnode = node.child("amps1");
        powernode = node.child("pow1");
        voltnode = node.child("Tvolt");
        loadnode = node.child("Load1state");
        swnode = node2.child("sw1");

        ampsnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                try{
                    amp = Float.parseFloat(value);
                } catch (Exception e) {
                    Toast.makeText(LoadA.this, "Error"+e.getMessage(), Toast.LENGTH_LONG).show();
                }
                amps.setText(String.format("%.2f",amp) + " A");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoadA.this, "Connection Error", Toast.LENGTH_LONG).show();
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
//                Toast.makeText(LoadA.this, "Connection Error", Toast.LENGTH_LONG).show();
//            }
//        });

        voltnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                volt.setText(value + " V");
                try{
                    volts = Float.parseFloat(value);
                } catch (Exception e) {
                    Toast.makeText(LoadA.this, "Error"+e.getMessage(), Toast.LENGTH_LONG).show();
                }
                pow = volts * amp;
                power.setText(String.format("%.2f",pow) + " W");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoadA.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        loadnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class); //load status
                if (value.equals("ON")) {
                    img.setImageResource(R.drawable.on);
                } else if (value.equals("OFF")) {
                    img.setImageResource(R.drawable.off);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoadA.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        swnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class); //load status
                if(value.equals("ON")) {
                    load1SW.setChecked(true);
                }
                else if(value.equals("OFF")){
                    load1SW.setChecked(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoadA.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        load1SW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    node2.child("sw1").setValue("ON");
                } else {
                    node2.child("sw1").setValue("OFF");
                }
            }
        });

        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (((Boolean) snapshot.getValue(Boolean.class)).booleanValue()) {
                    LoadA.this.mainread.setText("INTERNET CONNECTED ");
                } else {
                    LoadA.this.mainread.setText("INTERNET CONNECTION FAILED");
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