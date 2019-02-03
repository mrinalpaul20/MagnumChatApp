package com.example.magnumchatapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements View.OnClickListener{

    public static final int SENDER_TEXT_TYPE = 1;
    public static final int RECEIVER_TEXT_TYPE = 2;
    public static final int SENDER_IMAGE_TYPE = 3;
    public static final int RECEIVER_IMAGE_TYPE = 4;
    public static final int CENTER_TEXT_TYPE = 5;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    Context context;
    ArrayList<AdapterList> list = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getMessageType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        RecyclerView.ViewHolder vh;

        if (i == SENDER_TEXT_TYPE) {
            View view = layoutInflater.inflate(R.layout.sender_message,viewGroup,false);
            vh = new senderMessageHolder(view);
            ((senderMessageHolder) vh).setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemcClick(View v) {
                    onItemClickListener.onItemcClick(v);
                }
            });
            return vh;
        }

        else if (i == RECEIVER_TEXT_TYPE) {
            View view = layoutInflater.inflate(R.layout.receiver_message,viewGroup,false);
            vh = new receiverMessageHolder(view);
            ((receiverMessageHolder) vh).setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemcClick(View v) {
                    onItemClickListener.onItemcClick(v);
                }
            });
            return vh;
        }

        else if (i == SENDER_IMAGE_TYPE) {
            View view = layoutInflater.inflate(R.layout.sender_image,viewGroup,false);
            vh = new senderImageHolder(view);
            ((senderImageHolder) vh).setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemcClick(View v) {
                    onItemClickListener.onItemcClick(v);
                }
            });
            return vh;
        }

        else if (i == RECEIVER_IMAGE_TYPE) {
            View view = layoutInflater.inflate(R.layout.receiver_image,viewGroup,false);
            vh = new receiverImageHolder(view);
            ((receiverImageHolder) vh).setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemcClick(View v) {
                    onItemClickListener.onItemcClick(v);
                }
            });
            return vh;
        }

        else if (i == CENTER_TEXT_TYPE) {
            View view = layoutInflater.inflate(R.layout.center_message,viewGroup,false);
            vh = new centerMessageHolder(view);
            ((centerMessageHolder) vh).setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemcClick(View v) {
                    onItemClickListener.onItemcClick(v);
                }
            });
            return vh;
        }

        else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof senderMessageHolder) {
            ((senderMessageHolder) viewHolder).bind(list.get(i));
        }
        if (viewHolder instanceof receiverMessageHolder) {
            ((receiverMessageHolder) viewHolder).bind(list.get(i));
        }
        if (viewHolder instanceof senderImageHolder) {
            ((senderImageHolder) viewHolder).bind(list.get(i));
        }
        if (viewHolder instanceof receiverImageHolder) {
            ((receiverImageHolder) viewHolder).bind(list.get(i));
        }
        if (viewHolder instanceof centerMessageHolder) {
            ((centerMessageHolder) viewHolder).bind(list.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(AdapterList adapterList ) {
        list.add(adapterList);
        notifyItemInserted(list.size()-1);
    }

    @Override
    public void onClick(View v) { }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class senderMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView text;
        private OnItemClickListener mListner;
        public senderMessageHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(this);
        }

        public void bind(AdapterList list) {
            text.setText("  "+list.getMessage()+"  ");
        }

        @Override
        public void onClick(View v) {
            if (mListner != null) {
                mListner.onItemcClick(v);
            }
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListner = listener;
        }
    }

    public class receiverMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView text;
        private OnItemClickListener mListner;
        public receiverMessageHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }

        public void bind(AdapterList list) {
            text.setText("  "+list.getMessage()+"  ");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListner != null) {
                mListner.onItemcClick(v);
            }
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListner = listener;
        }
    }

    public class senderImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        private OnItemClickListener mListner;
        public senderImageHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }

        public void bind(AdapterList list) {
            storageReference.child(list.getMessage()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Glide.with(context).load(task.getResult()).into(image);
                    }
                    else {
                        Toast.makeText(context,"ERROR : FAILED TO LOEAD MESSAGE",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mListner != null) {
                mListner.onItemcClick(v);
            }
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListner = listener;
        }
    }

    public class receiverImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        private OnItemClickListener mListner;
        public receiverImageHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        public void bind(AdapterList list) {
            storageReference.child(list.getMessage()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Glide.with(context).load(task.getResult()).into(image);
                    }
                    else {
                        Toast.makeText(context,"ERROR : FAILED TO LOEAD MESSAGE",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mListner != null) {
                mListner.onItemcClick(v);
            }
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListner = listener;
        }
    }

    public class centerMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        private OnItemClickListener mListner;
        public centerMessageHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(this);
        }

        public void bind(AdapterList list) {
            text.setText("  "+list.getMessage()+"  ");
        }

        @Override
        public void onClick(View v) {
            if (mListner != null) {
                mListner.onItemcClick(v);
            }
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListner = listener;
        }
    }

}
