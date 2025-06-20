package com.example.smartgrid_iqra_fyp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Grid extends AppCompatActivity {
    TextView mainread;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference node, ampsnode, powernode, voltnode, pfnode, unitsnode, loadnode;
    private ImageView img;
    //    A4 Paper Size : 2480 x 3508
    int pageHeight = 300;
    int pagewidth = 600;
    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;
    String units0, units200, units700, TotalAmount, Cunits, Lunits, unitsPKcharges;
    float Tunits, rate, amount, unit0, unit200, unit700 = 0;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");
        Log.e("Email :", email);

        mainread = (TextView) findViewById(R.id.tagline);
        TextView amps = (TextView) findViewById(R.id.amps);
        TextView power = (TextView) findViewById(R.id.power);
        TextView volt = (TextView) findViewById(R.id.acvolt);
//        TextView pf = (TextView) findViewById(R.id.pf);
        TextView units = (TextView) findViewById(R.id.units);
        img = (ImageView) findViewById(R.id.imageView1);

        node = db.getReference("Data");
        voltnode = node.child("GridVolt");
        powernode = node.child("GridPower");
        ampsnode = node.child("GridAmps");
        pfnode = node.child("Gridpf");
        unitsnode = node.child("Gridunits");
        loadnode = node.child("Gridstate");


        DatabaseReference node2 = db.getReference("UserInput");
        DatabaseReference units0node = node2.child("units0");
        DatabaseReference units200node = node2.child("units200");
        DatabaseReference units700node = node2.child("units700");
        DatabaseReference unitsPKnode = node2.child("unitsPK");
        DatabaseReference node3 = db.getReference("Energy");
//        DatabaseReference Cunitsnode = node3.child("Cunits");
        DatabaseReference Lunitsnode = node3.child("Lunits");
//        DatabaseReference Tamountnode = node3.child("Total");

        Lunitsnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Lunits = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });


        units0node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                units0 = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        units200node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                units200 = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        units700node.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                units700 = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        unitsPKnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unitsPKcharges = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

//        pfnode.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue().toString();
//                pf.setText(value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
//            }
//        });
        unitsnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                units.setText(value + " units");
                Cunits = value;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        ampsnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                amps.setText(value + " A");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        powernode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                power.setText(value + " W");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        voltnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                volt.setText(value + " V");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
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
                Toast.makeText(Grid.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (((Boolean) snapshot.getValue(Boolean.class)).booleanValue()) {
                    Grid.this.mainread.setText("INTERNET CONNECTED ");
                } else {
                    Grid.this.mainread.setText("INTERNET CONNECTION FAILED");
                }
            }

            public void onCancelled(DatabaseError error) {
            }
        });

    }

    public void downloadreport(View v) {

        try {
            Tunits = Float.parseFloat(Cunits);
            unit0 = Float.parseFloat(units0);
            unit200 = Float.parseFloat(units200);
            unit700 = Float.parseFloat(units700);
        } catch (NumberFormatException e) {
            Log.e("MyApp", "Invalid Value : " + e.getMessage());
        }
        if (Tunits > 0 && Tunits < 200) {
            amount = Tunits * unit0;
            rate = unit0;
        } else if (Tunits >= 200 && Tunits < 700) {
            amount = Tunits * unit200;
            rate = unit200;
        } else if (Tunits >= 700) {
            amount = Tunits * unit700;
            rate = unit700;
        }
        String fileName = "SmartGrid_Report.pdf";
        PdfDocument pdfDocument = new PdfDocument();
        createPDF(pdfDocument, 600, 300, amount, Tunits, rate);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                File pdfFile;
                OutputStream outputStream;

                ContentResolver resolver = this.getContentResolver();
// Search for existing file
                Uri existingUri = null;
                String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
                String[] selectionArgs = new String[]{fileName};

                try (Cursor cursor = resolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Downloads._ID}, selection, selectionArgs, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                        existingUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error deleting file : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


                // If file exists, delete it
                if (existingUri != null) {
                    resolver.delete(existingUri, null, null);
                }

                // For Android 10+ (Scoped Storage)
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                outputStream = getContentResolver().openOutputStream(uri);
                pdfDocument.writeTo(outputStream);
                outputStream.close();
                Toast.makeText(this, "PDF Report saved to Downloads", Toast.LENGTH_SHORT).show();

                // ✅ Try to open using intent (Android 10+)
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);

//        else {
//                // For Android 9 and below
//                pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
//                outputStream = new FileOutputStream(pdfFile);
//                pdfDocument.writeTo(outputStream);
//                pdfDocument.close();
//                outputStream.close();
//                Toast.makeText(this, "PDF Report saved to Downloads", Toast.LENGTH_SHORT).show();
//
//                openPDF(pdfFile); // ✅ use your custom method
//            }

        } catch(IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

private void openPDF(File file) {
    try {
        Uri uriPdfPath = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

        Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
        pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf");
        pdfOpenIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(pdfOpenIntent);
        finish();
    } catch (ActivityNotFoundException e) {
        Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_LONG).show();
    }
}

public void createPDF(PdfDocument document, int height, int width, float amount, float units, float rate) {
    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
    PdfDocument.Page page = document.startPage(pageInfo);

    // variable for simple date format.
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    String dateTime = sdf.format(calendar.getTime()).toString();

    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss a");
    String currentTime = sdf2.format(calendar.getTime());
    Log.e("MyApp", "DATE" + dateTime);
    Log.e("MyApp", "TIME" + currentTime);

    Canvas canvas = page.getCanvas();
    Paint paint = new Paint();

    paint.setColor(Color.BLUE);
    paint.setTextSize(15);
//        paint.setTextAlign(Paint.Align.CENTER);
    canvas.drawText("SMART GRID ENERGY MONITORING", 30, 50, paint);

    paint.setColor(Color.RED);
    paint.setTextSize(12);
    canvas.drawText("ELECTRIC BILL", 30, 100, paint);

    paint.setTextSize(10);
    paint.setColor(Color.BLACK);

    canvas.drawText("DATE :  ", 30, 130, paint);
    canvas.drawText(dateTime, 200, 130, paint);

    canvas.drawText("TIME :  ", 30, 160, paint);
    canvas.drawText(currentTime, 200, 160, paint);

    canvas.drawText("EMAIL :  ", 30, 190, paint);
    canvas.drawText(email, 80, 190, paint);

    canvas.drawText("CURRENT ENERGY UNITS :", 30, 220, paint);
    canvas.drawText(units + " kWh", 200, 220, paint);

    canvas.drawText("PEAK HRS CHARGES :", 30, 250, paint);
    canvas.drawText("Rs " + unitsPKcharges, 200, 250, paint);

    canvas.drawText("ENERGY UNITS RATE (per kWh) :", 30, 280, paint);
    canvas.drawText("Rs " + rate, 200, 280, paint);

    canvas.drawText("PREVIOUS MONTH UNITS : ", 30, 310, paint);
    canvas.drawText(Lunits + " kWh", 200, 310, paint);

    canvas.drawText("TOTAL AMOUNT DUE : ", 30, 340, paint);
    canvas.drawText("Rs " + amount, 200, 340, paint);


//        canvas.drawText(" ********************************************************", 30, 240, paint);

    document.finishPage(page);  // finish the page
}


@Override
public void onBackPressed() {
    super.onBackPressed();
    finish();
}
}