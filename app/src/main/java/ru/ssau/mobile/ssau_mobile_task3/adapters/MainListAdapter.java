package ru.ssau.mobile.ssau_mobile_task3.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.ssau.mobile.ssau_mobile_task3.R;
import ru.ssau.mobile.ssau_mobile_task3.db.RecordOperations;
import ru.ssau.mobile.ssau_mobile_task3.model.Category;
import ru.ssau.mobile.ssau_mobile_task3.model.Record;

/**
 * Created by Pavel on 14.12.2016.
 */

public class MainListAdapter extends ArrayAdapter<Category> {

    private ArrayList<Category> data;
    private Record unfinishedRecord;
    private final String TAG = "MainListAdapter";
    Context context;
    private RecordOperations recordOperations;

    public MainListAdapter(Context context, int resource, List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.data = (ArrayList<Category>) objects;
        recordOperations = RecordOperations.getInstance(context);
        unfinishedRecord = recordOperations.getUnfinishedRecord();
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
            viewHolder.lastsFor = (TextView) view.findViewById(R.id.lasts_field);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (unfinishedRecord != null && c.getId()==unfinishedRecord.getCategory().getId()) {
            viewHolder.recordButton.setImageResource(R.drawable.ic_stop);
            viewHolder.lastsFor.setVisibility(View.VISIBLE);
            Calendar cal = Calendar.getInstance();
            long diffMs = cal.getTimeInMillis() - unfinishedRecord.getStart();
            long mins = diffMs / 60000;
            viewHolder.lastsFor.setText(String.format(
                    "%s %d minutes",context.getResources().getString(R.string.lasts), mins));
        }
        else {
            viewHolder.recordButton.setImageResource(R.drawable.ic_start);
            viewHolder.lastsFor.setVisibility(View.GONE);
        }
        viewHolder.activityName.setText(c.getName());
        viewHolder.recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                unfinishedRecord = recordOperations.getUnfinishedRecord();
                String msg = "";
                if (unfinishedRecord != null) {
                    if (c.getId() == unfinishedRecord.getCategory().getId()){
                        publishRecord();
                        unfinishedRecord = null;
                        msg = "Stopped tracking: "+c.getName();
                    } else {
                        publishRecord();
                        msg = "Stopped tracking: "+unfinishedRecord.getCategory().getName()+
                                ", started tracking: "+c.getName();
                        startTracking(c);
                    }
                } else {
                    startTracking(c);
                    msg = "Started tracking: "+c.getName();
                }
                Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                toast.show();
                notifyDataSetChanged();
            }
        });
        return view;
    }

    private void startTracking(Category c) {
        long catId = c.getId();
        long start = Calendar.getInstance().getTimeInMillis();
        long end = 0;
        long mins = 0;
        unfinishedRecord = recordOperations.addRecord(catId, start, end, mins, null, null);
    }

    private void publishRecord() {
        Record rec = new Record();
        rec.copyFrom(unfinishedRecord);
        Calendar c = Calendar.getInstance();
        rec.setEnd(c.getTimeInMillis());
        long diffMs = rec.getEnd() - rec.getStart();
        long mins = diffMs / 60000;
        rec.setMinutes(mins);
        recordOperations.updateRecord(unfinishedRecord, rec);
    }

    public ArrayList<Category> getData() {
        return data;
    }

    public void setData(ArrayList<Category> data) {
        this.data = data;
    }

    private static class ViewHolder {
        TextView activityName, lastsFor;
        ImageButton recordButton;
    }
}
