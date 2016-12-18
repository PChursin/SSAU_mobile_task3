package ru.ssau.mobile.ssau_mobile_task3.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

import ru.ssau.mobile.ssau_mobile_task3.DetailsActivity;
import ru.ssau.mobile.ssau_mobile_task3.FullscreenImageActivity;
import ru.ssau.mobile.ssau_mobile_task3.R;
import ru.ssau.mobile.ssau_mobile_task3.db.PhotoOperations;
import ru.ssau.mobile.ssau_mobile_task3.model.Photo;

/**
 * Created by Pavel on 17.12.2016.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    public static final int GALLERY_REQUEST = 42;
    private ArrayList<Photo> data, pendingDelete = new ArrayList<>();
    private final String TAG = "RVAdapter";
    Context context;
    private PhotoOperations photoOperations;

    public RVAdapter(Context context, ArrayList<Photo> photos) {
        this.context = context;
        photoOperations = PhotoOperations.getInstance(context);
        data = photos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.photo_list_item, parent, false);

        ViewHolder holder = new ViewHolder(v);
        holder.itemView.setTag(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Photo photo = data.get(position);
        if (photo.getImage().length == 0) {
            holder.cross.setVisibility(View.GONE);
            holder.photo.setImageResource(R.drawable.ic_photo);
            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    ((DetailsActivity)context).startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                }
            });
        } else {
            holder.cross.setVisibility(View.VISIBLE);
            holder.cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //photoOperations.deletePhoto(photo);
                    pendingDelete.add(photo);
                    data.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            });
            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick PHOTO");
                    Intent intent = new Intent(context, FullscreenImageActivity.class);
                    intent.putExtra("image", photo.getImage());
                    context.startActivity(intent);
                }
            });
            Log.d(TAG, "onBindViewHolder: getting bitmap drawable for "+photo.getId());

            BitmapDrawable bd = new BitmapDrawable(context.getResources(), Photo.getSmallImage(photo.getImage(), context));
            holder.photo.setImageDrawable(bd);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<Photo> getData() {
        return data;
    }

    public void setData(ArrayList<Photo> data) {
        this.data = data;
    }

    public ArrayList<Photo> getPendingDelete() {
        return pendingDelete;
    }

    public void setPendingDelete(ArrayList<Photo> pendingDelete) {
        this.pendingDelete = pendingDelete;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton photo, cross;

        public ViewHolder(View itemView) {
            super(itemView);

            photo = (ImageButton) itemView.findViewById(R.id.photo_content);
            cross = (ImageButton) itemView.findViewById(R.id.photo_delete);
        }
    }
}
