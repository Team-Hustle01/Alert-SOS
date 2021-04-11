package com.teamhustle.alertsos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private String currentDateTimeString;
    private String latitude, longitude, link;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private RequestQueue mRequestQue;
    private Intent intent;
    private String URL = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {

                            double lat = location.getLatitude();
                            double longi = location.getLongitude();
                            latitude = String.valueOf(lat);
                            longitude = String.valueOf(longi);
                            link = "http://www.google.com/maps/place/"+latitude+","+longitude;
                        }
                    }
                });

        currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());


        if (getIntent().hasExtra("time")) {
            Intent intent = new Intent(MainActivity.this, ReceiveNotificationActivity.class);
            intent.putExtra("time", getIntent().getStringExtra("time"));
            intent.putExtra("link", getIntent().getStringExtra("link"));
            startActivity(intent);
        }

        Button button = findViewById(R.id.btn);
        mRequestQue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                int id = item.getItemId();


                if (id == R.id.nav_dev) {
                    intent = new Intent(MainActivity.this, Developers.class);
                    startActivity(intent);
                }


                if (id == R.id.nav_about) {
                    intent = new Intent(MainActivity.this, About.class);
                    startActivity(intent);
                }


                if (id == R.id.nav_share) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    String subject = "Exclamation";
                    String body = "Install from Play Store now,\n Exclamation,\n https://play.google.com/store/apps/details?id=" + getPackageName();

                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, body);

                    startActivity(Intent.createChooser(intent, "Share this"));


                }

                if (id == R.id.nav_out) {
                    FirebaseAuth.getInstance().signOut();
                    Intent orderIntent = new Intent(MainActivity.this, Login.class);
                    orderIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(orderIntent);
                }


                if (id == R.id.nav_exit) {
                    finishAffinity();
                }


                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }


    private void sendNotification() {

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + "news");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "Emergency Help!");
            notificationObj.put("body", "Please safe me if you're nearby.");

            JSONObject extraData = new JSONObject();
            extraData.put("time", currentDateTimeString);
            extraData.put("link", link);



            json.put("notification",notificationObj);
            json.put("data",extraData);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("MUR", "onResponse: ");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: "+error.networkResponse);
                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAJKiec8E:APA91bFW9uCBmpDYapTkNyoVrXbWRDC3YCp7d9MbYB9ONfpOvUU9vpFGPHKDdvZrrTOOPRkeeNhQ48FKsa9x2_v78e2a5fThWv0i7Yfim3-h4sEaogZ0jdIGNsB2esQ8dAu7kUCqKfzP");
                    return header;
                }
            };
            mRequestQue.add(request);
        }
        catch (JSONException e)

        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setIcon(R.drawable.ic_warning_black_24dp);
        alertDialogBuilder.setTitle("Exit");
        alertDialogBuilder.setMessage("Do you want to exit?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

               finishAffinity();

            }
        });

        alertDialogBuilder.setNeutralButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}