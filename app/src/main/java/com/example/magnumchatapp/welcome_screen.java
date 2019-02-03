package com.example.magnumchatapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class welcome_screen extends AppCompatActivity {
RelativeLayout start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setVisibility(View.GONE);
                if (isNetworkAvailable(welcome_screen.this)) {
                    fragmentTransaction.replace(R.id.fragmentContainer,new ChatScreen());
                    fragmentTransaction.commit();
                }

                else {
                    fragmentTransaction.replace(R.id.fragmentContainer,new internet_connect_error());
                    fragmentTransaction.commit();
                }
            }
        });
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
