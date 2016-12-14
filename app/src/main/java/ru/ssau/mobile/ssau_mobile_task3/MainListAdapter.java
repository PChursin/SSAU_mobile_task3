package ru.ssau.mobile.ssau_mobile_task3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.ssau.mobile.ssau_mobile_task3.model.Category;
import ru.ssau.mobile.ssau_mobile_task3.model.Record;

/**
 * Created by Pavel on 14.12.2016.
 */

public class MainListAdapter extends ArrayAdapter<Category> {

    private ArrayList<Category> data;
    private final String TAG = "MainListAdapter";
    Context context;

    public MainListAdapter(Context context, int resource, List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.data = (ArrayList<Category>) objects;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Category getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        Log.w(TAG, "getItemId!");
        return data.get(i).getId();
    }

    @NonNull
    @Override
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
        final Category c = data.get(i);
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.main_list_item, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.activityName = (TextView) view.findViewById(R.id.activity_name);
            viewHolder.recordButton = (ImageButton) view.findViewById(R.id.start_stop_button);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.activityName.setText(c.getName());
        viewHolder.recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(context, "This is "+c.getName(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        return view;
    }

    public ArrayList<Category> getData() {
        return data;
    }

    public void setData(ArrayList<Category> data) {
        this.data = data;
    }

    private static class ViewHolder {
        TextView activityName;
        ImageButton recordButton;
    }
}
