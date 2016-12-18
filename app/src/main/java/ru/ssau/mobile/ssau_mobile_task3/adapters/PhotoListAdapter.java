package ru.ssau.mobile.ssau_mobile_task3.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import ru.ssau.mobile.ssau_mobile_task3.DetailsActivity;
import ru.ssau.mobile.ssau_mobile_task3.R;
import ru.ssau.mobile.ssau_mobile_task3.db.PhotoOperations;
import ru.ssau.mobile.ssau_mobile_task3.model.Photo;

/**
 * Created by Pavel on 17.12.2016.
 */

public class PhotoListAdapter extends ArrayAdapter<Photo> {

    public static final int GALLERY_REQUEST = 42;
    private ArrayList<Photo> data;
    private final String TAG = "PhotoListAdapter";
    Context context;
    private PhotoOperations photoOperations;

    public PhotoListAdapter(Context context, int resource, List<Photo> objects) {
        super(context, resource, objects);
        this.context = context;
        data = (ArrayList<Photo>) objects;
        photoOperations = PhotoOperations.getInstance(context);
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Photo photo = data.get(position);
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.photo_list_item, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.photo = (ImageButton) view.findViewById(R.id.photo_content);
            viewHolder.cross = (ImageButton) view.findViewById(R.id.photo_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (photo.getImage().length == 0) {
            viewHolder.cross.setVisibility(View.GONE);
            viewHolder.photo.setImageResource(R.drawable.ic_photo);
            viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    ((DetailsActivity)context).startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                }
            });
        } else {
            viewHolder.cross.setVisibility(View.VISIBLE);
            BitmapDrawable bd = new BitmapDrawable(context.getResources(), Photo.getSmallImage(photo.getImage(), context));
            viewHolder.photo.setImageDrawable(bd);

        }
        //view.setMinimumHeight(view.getMeasuredWidth());
        return view;
    }

    public ArrayList<Photo> getData() {
        return data;
    }

    public void setData(ArrayList<Photo> data) {
        this.data = data;
    }

    private static class ViewHolder {
        ImageButton photo, cross;
    }
}
