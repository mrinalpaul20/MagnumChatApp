package com.example.magnumchatapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.GONE;

public class ChatScreen extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout linearLayout;
    DatabaseReference databaseReference;
    public String message;
    char chat_flag='s';
    protected int message_counter=1;
    ShimmerFrameLayout shimmerFrameLayout;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ChatAdapter adapter = new ChatAdapter(this);
        recyclerView.setAdapter(adapter);
        frameLayout = findViewById(R.id.frameLayout);
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        linearLayout = findViewById(R.id.linearLayout);
        final OnItemClickListener fakeListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
            }
        };
        final OnItemClickListener onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                if (isNetworkAvailable(ChatScreen.this)) {
                    shimmerFrameLayout.startShimmerAnimation();
                    linearLayout.setClickable(false);
                    adapter.setOnItemClickListener(fakeListener);
                    final OnItemClickListener onItemClickListener1 = this;
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            shimmerFrameLayout.stopShimmerAnimation();
                            linearLayout.setClickable(true);
                            adapter.setOnItemClickListener(onItemClickListener1);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.child("chat1").getChildrenCount() < message_counter) {
                                        Toast.makeText(ChatScreen.this,"Conversation is Ended",Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())

                                        if (chat_flag == 's') {
                                            sender(dataSnapshot1,adapter);
                                        }

                                        else {
                                            receiver(dataSnapshot1,adapter);
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(ChatScreen.this,"Failure"+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                        }
                    };
                    handler.postDelayed(r,500);
                }
                else { showFragment(); }
            }
        };

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(ChatScreen.this)) {
                    shimmerFrameLayout.startShimmerAnimation();
                    linearLayout.setClickable(false);
                    adapter.setOnItemClickListener(fakeListener);
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            shimmerFrameLayout.stopShimmerAnimation();
                            linearLayout.setClickable(true);
                            adapter.setOnItemClickListener(onItemClickListener);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.child("chat1").getChildrenCount() < message_counter) {
                                        Toast.makeText(ChatScreen.this,"Conversation is Ended",Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())

                                        if (chat_flag == 's') {
                                            sender(dataSnapshot1,adapter);
                                        }

                                        else {
                                            receiver(dataSnapshot1,adapter);
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(ChatScreen.this,"Failure"+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                            recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        }
                    };
                    handler.postDelayed(r,500);
                }
                else { showFragment(); }
        }
        });
    }

    public void sender(DataSnapshot dataSnapshot, ChatAdapter adapter) {
        message = dataSnapshot.child(String.valueOf(message_counter))
                .getValue(DatabaseMessage.class).getSender();

        if (TextUtils.isEmpty(message)) {
            receiver(dataSnapshot,adapter);
            return;
        }

        if (TextUtils.equals(message,"time")) {
            message_counter++;
            centerMessage(dataSnapshot,adapter);
            return;
        }

        if (TextUtils.equals(message,"image")) {
            String image_path = String.valueOf(message_counter)+".jpg";
            adapter.add(new AdapterList(3,image_path));
            chat_flag = 'r';
            return;
        }

        adapter.add(new AdapterList(1,message));
        chat_flag = 'r';
    }

    public void receiver(DataSnapshot dataSnapshot, ChatAdapter adapter) {
        message = dataSnapshot.child(String.valueOf(message_counter))
                .getValue(DatabaseMessage.class).getReceiver();

        if (TextUtils.isEmpty(message)) {
            message_counter++;
            sender(dataSnapshot,adapter);
            return;
        }

        if (TextUtils.equals(message,"image")) {
            String image_path = String.valueOf(message_counter)+".jpg";
            adapter.add(new AdapterList(4,image_path));
            chat_flag = 's';
            message_counter++;
            return;
        }

        if (TextUtils.equals(message,"time")) {
            message_counter++;
            centerMessage(dataSnapshot,adapter);
            return;
        }

        adapter.add(new AdapterList(2,message));
        chat_flag = 's';
        message_counter++;
    }

    public void centerMessage(DataSnapshot dataSnapshot, ChatAdapter adapter) {
        String sender_message = dataSnapshot.child(String.valueOf(message_counter))
                .getValue(DatabaseMessage.class).getSender();

        String receiver_message = dataSnapshot.child(String.valueOf(message_counter))
                .getValue(DatabaseMessage.class).getReceiver();

        if (!TextUtils.isEmpty(sender_message)) {
            message = sender_message;
        }

        else {
            message = receiver_message;
        }

        adapter.add(new AdapterList(5,message));
        message_counter++;
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void showFragment() {
        frameLayout.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,new internet_connect_error());
        fragmentTransaction.commit();
    }

    public void showChat() {
        frameLayout = findViewById(R.id.frameLayout);
        frameLayout.setVisibility(GONE);
    }
}