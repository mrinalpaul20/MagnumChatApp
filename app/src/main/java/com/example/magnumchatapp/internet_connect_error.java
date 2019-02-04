package com.example.magnumchatapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;


public class internet_connect_error extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_internet_connect_error,container,false);
        final ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        ImageButton refreshButton = view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable(view.getContext())) {
                    shimmerFrameLayout.startShimmerAnimation();
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            shimmerFrameLayout.stopShimmerAnimation();
                            ((ChatScreen)getActivity()).showChat();
                        }
                    };
                    handler.postDelayed(r,500);
                }
                else {
                    Toast.makeText(view.getContext(), "ERROR : NO INTERNET \n TRY AGAIN!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}