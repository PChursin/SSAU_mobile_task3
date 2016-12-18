package ru.ssau.mobile.ssau_mobile_task3.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.ssau.mobile.ssau_mobile_task3.DetailsActivity;
import ru.ssau.mobile.ssau_mobile_task3.R;
import ru.ssau.mobile.ssau_mobile_task3.db.RecordOperations;
import ru.ssau.mobile.ssau_mobile_task3.model.Category;
import ru.ssau.mobile.ssau_mobile_task3.model.Record;

/**
 * Created by Pavel on 14.12.2016.
 */

public class RecentListAdapter extends ArrayAdapter<Record> {

    private ArrayList<Record> data;
    private final String TAG = "RecentListAdapter";
    private final RecordOperations recordOperations;
    Context context;
//    private RecordOperations recordOperations;

    public RecentListAdapter(Context context, int resource, List<Record> objects) {
        super(context, resource, objects);
        this.context = context;
        recordOperations = RecordOperations.getInstance(context);
        this.data = (ArrayList<Record>) objects;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Record getItem(int i) {
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
        final Record rec = data.get(i);
        final Category cat = rec.getCategory();
        Log.d(TAG, "getView: "+cat.getName());
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.recent_list_item, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.dateTimeField = (TextView) view.findViewById(R.id.recent_datetime);
            viewHolder.minutesField = (TextView) view.findViewById(R.id.recent_minutes);
            viewHolder.deleteButton = (ImageView) view.findViewById(R.id.recent_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (rec.getEnd() == 0) {
            Calendar cal = Calendar.getInstance();
            long diffMs = cal.getTimeInMillis() - rec.getStart();
            long mins = diffMs / 60000;
            viewHolder.minutesField.setText(String.format(
                    "%s - %d minutes (still lasts)",cat.getName(), mins));
        }
        else {
            viewHolder.minutesField.setText(String.format(
                    "%s - %d minutes",cat.getName(), rec.getMinutes()));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(rec.getStart());
        viewHolder.dateTimeField.setText(sdf.format(cal.getTime()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: starting activity");
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("record", rec.toSendable());
                //todo change parcel structure - photos are too heavy for intent
                context.startActivity(intent);
            }
        });
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordOperations.deleteRecord(rec);
                data.remove(rec);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public ArrayList<Record> getData() {
        return data;
    }

    public void setData(ArrayList<Record> data) {
        this.data = data;
    }

    private static class ViewHolder {
        TextView dateTimeField, minutesField;
        ImageView deleteButton;
    }
}
