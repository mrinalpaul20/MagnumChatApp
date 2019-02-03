package com.example.magnumchatapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.GONE;

public class ChatScreen extends Fragment {

    RecyclerView recyclerView;
    LinearLayout linearLayout;
    DatabaseReference databaseReference;
    public String message;
    char chat_flag='s';
    protected int message_counter=1;
    ShimmerFrameLayout shimmerFrameLayout;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.chat_screen,container,false);
        context = view.getContext();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final ChatAdapter adapter = new ChatAdapter(context);
        recyclerView.setAdapter(adapter);
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        linearLayout = view.findViewById(R.id.linearLayout);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(context)) {
                    shimmerFrameLayout.startShimmerAnimation();
                    linearLayout.setClickable(false);
                    recyclerView.setVisibility(GONE);
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            shimmerFrameLayout.stopShimmerAnimation();
                            linearLayout.setClickable(true);
                            recyclerView.setVisibility(View.VISIBLE);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.child("chat1").getChildrenCount() < message_counter) {
                                        Toast.makeText(view.getContext(),"Conversation is Ended",Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(context,"Failure"+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                            recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        }
                    };
                    handler.postDelayed(r,500);
                }

                else {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer,new internet_connect_error());
                    fragmentTransaction.commit();
                }
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemcClick(View v) {
                if (isNetworkAvailable(context)) {
                    shimmerFrameLayout.startShimmerAnimation();
                    linearLayout.setClickable(false);
                    recyclerView.setVisibility(GONE);
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            shimmerFrameLayout.stopShimmerAnimation();
                            linearLayout.setClickable(true);
                            recyclerView.setVisibility(View.VISIBLE);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.child("chat1").getChildrenCount() < message_counter) {
                                        Toast.makeText(view.getContext(),"Conversation is Ended",Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(context,"Failure"+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                            recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        }
                    };
                    handler.postDelayed(r,500);
                }

                else {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer,new internet_connect_error());
                    fragmentTransaction.commit();
                }
            }
        });

        return view;
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

    class thread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}