package com.instagramclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<ListFeed> {

    private ArrayList<ListFeed> dataSet;
    Context mContext;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public CustomAdapter(@NonNull Context context, ArrayList<ListFeed> data) {
        super(context, R.layout.user_list_item,data);
        this.dataSet = data;
        this.mContext=context;
    }

    private static class ViewHolder {
        TextView userName;
        ImageView imageView;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ListFeed listFeed = getItem(position);

        final ViewHolder viewHolder;
        final View result;

        if (convertView==null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_list_item, parent, false);
            viewHolder.userName= (TextView) convertView.findViewById(R.id.textView);
            viewHolder.imageView=(ImageView) convertView.findViewById(R.id.imageView);
            result=convertView;

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.userName.setText(listFeed.getUsername());

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

        return result;
    }
}
