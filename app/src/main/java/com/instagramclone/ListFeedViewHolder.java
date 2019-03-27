package com.instagramclone;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

class ListFeedViewHolder extends RecyclerView.ViewHolder {
    TextView userName;
    ImageView imageView;
    TextView timestamp;
    public ListFeedViewHolder(@NonNull View itemView) {
        super(itemView);

        userName=itemView.findViewById(R.id.textView);
        imageView=itemView.findViewById(R.id.imageView);
        timestamp=itemView.findViewById(R.id.timeTextView);
    }
}
