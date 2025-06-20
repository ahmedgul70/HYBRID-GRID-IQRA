package com.example.smartgrid_iqra_fyp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Settings extends AppCompatActivity {
    TextView mainread;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference node, units0node, units200node, units700node, peakunitsnode, pk0node, pk1node;
    EditText tbox1, tbox2, tbox3, tbox4, tbox5, tbox6;
    Button but_set;

    int pkStart,pkEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mainread = (TextView) findViewById(R.id.tagline);
        but_set = (Button) findViewById(R.id.SubmitBtn);
        tbox1 = findViewById(R.id.unitR1);
        tbox2 = findViewById(R.id.unitR2);
        tbox3 = findViewById(R.id.unitR3);
        tbox4 = findViewById(R.id.unitR4);
        tbox5 = findViewById(R.id.unitR5);
        tbox6 = findViewById(R.id.unitR6);

        node = db.getReference("UserInput");
        units0node = node.child("units0");
        units200node = node.child("units200");
        units700node = node.child("units700");
        peakunitsnode = node.child("unitsPK");
        pk0node = node.child("PK0");
        pk1node = node.child("PK1");

        units0node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tbox1.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Settings.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        units200node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tbox2.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Settings.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        units700node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tbox3.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Settings.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        peakunitsnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tbox4.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Settings.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        pk0node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tbox5.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Settings.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        pk1node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tbox6.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Settings.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        but_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value1 = tbox1.getText().toString();
                String value2 = tbox2.getText().toString();
                String value3 = tbox3.getText().toString();
                String value4 = tbox4.getText().toString();
                String value5 = tbox5.getText().toString();
                String value6 = tbox6.getText().toString();

                if (value1.isEmpty()) {
                    tbox1.setError("Field is Empty");
                    tbox1.requestFocus();
                    return;
                }
                else if (value2.isEmpty()) {
                    tbox2.setError("Field is Empty");
                    tbox2.requestFocus();
                    return;
                }
                else if (value3.isEmpty()) {
                    tbox3.setError("Field is Empty");
                    tbox3.requestFocus();
                    return;
                }
                else if (value4.isEmpty()) {
                    tbox4.setError("Field is Empty");
                    tbox4.requestFocus();
                    return;
                }
                else if (value5.isEmpty()) {
                    tbox5.setError("Field is Empty");
                    tbox5.requestFocus();
                    return;
                }
                else if (value6.isEmpty()) {
                    tbox6.setError("Field is Empty");
                    tbox6.requestFocus();
                    return;
                }

                try {
                     pkStart = Integer.parseInt(value5);
                     pkEnd = Integer.parseInt(value6);
                } catch (NumberFormatException e) {
                    Toast.makeText(Settings.this, "Conversion Error", Toast.LENGTH_LONG).show();
                }

                if (pkStart <= 0 || pkStart > 12)
                {
                    tbox5.setError("12 hour format only!");
                    tbox5.requestFocus();
                    return;
                }
                if (pkEnd <= 0 || pkEnd > 12)
                {
                    tbox6.setError("12 hour format only!");
                    tbox6.requestFocus();
                    return;
                }

                node.child("units0").setValue(value1);
                node.child("units200").setValue(value2);
                node.child("units700").setValue(value3);
                node.child("unitsPK").setValue(value4);
                node.child("PK0").setValue(value5);
                node.child("PK1").setValue(value6);
                // Write data to Firebase
                Toast.makeText(Settings.this, "Data Sent", Toast.LENGTH_LONG).show();
            }
        });


    }
}