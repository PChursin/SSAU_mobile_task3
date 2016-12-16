package ru.ssau.mobile.ssau_mobile_task3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ru.ssau.mobile.ssau_mobile_task3.db.RecordOperations;
import ru.ssau.mobile.ssau_mobile_task3.model.Record;

/**
 * Created by Pavel on 16.12.2016.
 */

public class RecentActivity extends AppCompatActivity {
    RecordOperations recordOperations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        recordOperations = RecordOperations.getInstance(this);
        ArrayList<Record> cl = recordOperations.getAllRecords();
        RecentListAdapter adapter = new RecentListAdapter(this, R.layout.recent_list_item, cl);
        ListView listView = (ListView) findViewById(R.id.recent_list);
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecentActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });
    }
}
