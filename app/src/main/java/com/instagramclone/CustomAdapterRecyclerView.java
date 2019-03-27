package com.instagramclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomAdapterRecyclerView extends RecyclerView.Adapter<ListFeedViewHolder> {

    private ArrayList<ListFeed> dataSet;
    Context mContext;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public CustomAdapterRecyclerView(ArrayList<ListFeed> dataSet, Context mContext) {
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ListFeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_list_item, viewGroup, false);

        ListFeedViewHolder viewHolder = new ListFeedViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListFeedViewHolder viewHolder, int i) {

        ListFeed listFeed = dataSet.get(i);

        //viewHolder.imageView.setImageResource(R.drawable.loading);

        viewHolder.userName.setText(listFeed.getUsername());

        Object time = listFeed.getTimeStamp();
        if (time == null)
            time = System.currentTimeMillis() + "";
        Date date = new Date();
        date.setTime(Long.parseLong(time.toString()));
        Date currentTime = Calendar.getInstance().getTime();
        Date uploadTime = date;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.getDefault());

        long mills = currentTime.getTime() - uploadTime.getTime();
        int hours =(int) mills/(1000 * 60 * 60);
        int mins =(int) (mills/(1000*60)) % 60;
        int days =(int) (mills / (60*60*24*1000));
        String diff="";
        if (hours<1){
            if (mins==1)
                diff = mins+ " minute ago";
            else
                diff = mins+ " minutes ago";
        }
        else if (hours==1){
            diff = hours+" hour ago";
        }
        else if (hours<=24){
            diff = hours+" hours ago";
        }
        if (days>0){
            if (days>1)
                diff = days+" days ago";
            else
                diff = days+" day ago";
        }



        viewHolder.timestamp.setText(diff);

        StorageReference postReference = storageRef.child(listFeed.getImage());
        final long ONE_MEGABYTE = 1024 * 1024;
        postReference.getBytes(ONE_MEGABYTE*10).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                viewHolder.imageView.setImageBitmap(bitmap);

                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
              /*  Bitmap errorImage = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.download);
                viewHolder.imageView.setImageBitmap(errorImage);*/
                Toast.makeText(mContext, "Download Failed!", Toast.LENGTH_LONG).show();
                // Handle any errors
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
