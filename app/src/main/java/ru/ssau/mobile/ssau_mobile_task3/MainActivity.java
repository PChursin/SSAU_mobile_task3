package ru.ssau.mobile.ssau_mobile_task3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import ru.ssau.mobile.ssau_mobile_task3.adapters.MainListAdapter;
import ru.ssau.mobile.ssau_mobile_task3.db.CategoryOperations;
import ru.ssau.mobile.ssau_mobile_task3.db.DBHelper;
import ru.ssau.mobile.ssau_mobile_task3.model.Category;

public class MainActivity extends AppCompatActivity {

    CategoryOperations catOps;
    DBHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TEST

        catOps = CategoryOperations.getInstance(this);
        dbhelper = DBHelper.getInstance(this);
        Button clear = (Button) findViewById(R.id.clear_photo_button);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhelper.clearPhotos();
            }
        });

        /*Category c = new Category();
        c.setId(0);
        c.setName("Работа");
        Category c1 = new Category();
        c1.setId(1);
        c1.setName("Отдых");
        /*Photo p = new Photo();
        p.setId(0);
        ArrayList<Photo> pl = new ArrayList<Photo>();
        pl.add(p);
        Record r = new Record();
        r.setId(0); r.setCategory(c); r.setPhotos(pl); r.setStart(123); r.setEnd(321); r.setMinutes(5); r.setSummary("Lol");*/
        ArrayList<Category> cl = catOps.getAllCategories();
//        ArrayList<Category> cl = new ArrayList<>();
//        cl.add(c);
//        cl.add(c1);

        MainListAdapter adapter = new MainListAdapter(this, R.layout.main_list_item, cl);
        ListView listView = (ListView) findViewById(R.id.main_list);
        listView.setAdapter(adapter);

        //END
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_recent) {
            Intent intent = new Intent(MainActivity.this, RecentActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_stats) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
