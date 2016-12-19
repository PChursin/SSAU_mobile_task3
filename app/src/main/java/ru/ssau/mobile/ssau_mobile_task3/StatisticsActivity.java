package ru.ssau.mobile.ssau_mobile_task3;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ru.ssau.mobile.ssau_mobile_task3.db.CategoryOperations;
import ru.ssau.mobile.ssau_mobile_task3.db.RecordOperations;
import ru.ssau.mobile.ssau_mobile_task3.model.Category;

/**
 * Created by Pavel on 18.12.2016.
 */

public class StatisticsActivity extends AppCompatActivity {
    Calendar dateStart, dateEnd;
    TextView fromDateLabel, toDateLabel;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    SimpleDateFormat dateOnly;
    DatePickerDialog.OnDateSetListener startDateListener, endDateListener;
    RecordOperations recordOperations;
    CategoryOperations categoryOperations;

    static ArrayList<String> recentActivities = new ArrayList<>();
    static ArrayList<String> totalTime = new ArrayList<>();
    static HashMap<String, Long> totalTimeMap = new HashMap<>();
    ArrayList<String> categories = new ArrayList<>();

    public static final String TAG = "StatisticsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        recordOperations = RecordOperations.getInstance(this);
        categoryOperations = CategoryOperations.getInstance(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        for (Category cat : categoryOperations.getAllCategories())
            categories.add(cat.getName());
        fromDateLabel = (TextView) findViewById(R.id.stat_from_date);
        toDateLabel = (TextView) findViewById(R.id.stat_to_date);

        if (savedInstanceState != null) {
            dateStart = (Calendar) savedInstanceState.get("dateStart");
            dateEnd = (Calendar) savedInstanceState.get("dateEnd");
        } else {
            dateStart = Calendar.getInstance();
            dateEnd = Calendar.getInstance();
        }

        dateOnly = new SimpleDateFormat("EE, dd MMM yyyy");
        fromDateLabel.setText(dateOnly.format(dateStart.getTime()));
        toDateLabel.setText(dateOnly.format(dateEnd.getTime()));
        updateStats();
        setUpPickers();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private void setUpPickers() {
        //START DATE PICKER SETUP
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateStart.set(Calendar.YEAR, i);
                dateStart.set(Calendar.MONTH, i1);
                dateStart.set(Calendar.DAY_OF_MONTH, i2);
                fromDateLabel.setText(dateOnly.format(dateStart.getTime()));

                updateStats();
            }
        };

        final DatePickerDialog startDatePicker = new DatePickerDialog(StatisticsActivity.this, startDateListener,
                dateStart.get(Calendar.YEAR), dateStart.get(Calendar.MONTH), dateStart.get(Calendar.DAY_OF_MONTH));


        fromDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePicker.show();
            }
        });

        //END DATE PICKER SETUP
        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateEnd.set(Calendar.YEAR, i);
                dateEnd.set(Calendar.MONTH, i1);
                dateEnd.set(Calendar.DAY_OF_MONTH, i2);
                toDateLabel.setText(dateOnly.format(dateEnd.getTime()));

                updateStats();
            }
        };

        final DatePickerDialog endDatePicker = new DatePickerDialog(StatisticsActivity.this, endDateListener,
                dateEnd.get(Calendar.YEAR), dateEnd.get(Calendar.MONTH), dateEnd.get(Calendar.DAY_OF_MONTH));


        toDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDatePicker.show();
            }
        });
    }

    public void updateStats() {
        if (dateStart.getTimeInMillis() < dateEnd.getTimeInMillis()) {
            Log.d(TAG, "updateStats: updating");
            ArrayList lists[] = {totalTime, recentActivities};
            String ext[] = {" minutes", " times"};
            for (int i = 0; i < lists.length; i++) {
                ArrayList<String> l = lists[i];
                l.clear();
                HashMap<String, Long> group;
                if (i == 0) {
                    group = recordOperations.
                            getGroupedRecordsTime(dateStart.getTimeInMillis(), dateEnd.getTimeInMillis());
                    totalTimeMap = group;
                }
                else
                    group = recordOperations.
                            getGroupedRecordsCount(dateStart.getTimeInMillis(), dateEnd.getTimeInMillis());
                Comparator<Map.Entry<String, Long>> comparator = new Comparator<Map.Entry<String, Long>>() {
                    @Override
                    public int compare(Map.Entry<String, Long> a, Map.Entry<String, Long> b) {
                        long aV = a.getValue();
                        long bV = b.getValue();
                        if (aV < bV)
                            return 1;
                        else if (aV == bV)
                            return 0;
                        return -1;
                    }
                };
                ArrayList<Map.Entry<String, Long>> entryArrayList = new ArrayList<>();
                for (Map.Entry<String, Long> entry : group.entrySet()) {
                    entryArrayList.add(entry);
                }
                Collections.sort(entryArrayList, comparator);
                for (Map.Entry<String, Long> entry : entryArrayList) {
                    l.add(entry.getKey() + " - " + entry.getValue() + ext[i]);
                }
            }
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("dateStart", dateStart);
        outState.putSerializable("dateEnd", dateEnd);
        super.onSaveInstanceState(outState);
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        Context context;
        ArrayAdapter<String> adapter;

        public PlaceholderFragment() {
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public static PlaceholderFragment newInstance(int sectionNumber, Context context) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setContext(context);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.list_fragment, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.stat_list);
            int page = getArguments().getInt(ARG_SECTION_NUMBER);
            boolean empty = true;
            // ((StatisticsActivity)context).updateStats();
            if (page == 1) {
                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, totalTime);
                empty = totalTime.isEmpty();
            }
            else {
                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, recentActivities);
                empty = recentActivities.isEmpty();
            }
            listView.setAdapter(adapter);
            if (empty) {
                TextView test = (TextView) rootView.findViewById(R.id.test_text);
                test.setVisibility(View.VISIBLE);
                test.setText("No activities match selected range");
            }
            return rootView;
        }
    }

    public static class PlaceholderPieChartFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        Context context;
        ArrayAdapter<String> adapter;

        public PlaceholderPieChartFragment() {
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public static PlaceholderPieChartFragment newInstance(int sectionNumber, Context context) {
            PlaceholderPieChartFragment fragment = new PlaceholderPieChartFragment();
            fragment.setContext(context);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.graph_fragment, container, false);
            boolean empty = totalTimeMap.isEmpty();
            if (!empty) {
                PieChart chart = (PieChart) rootView.findViewById(R.id.chart);
                ArrayList<String> cats = new ArrayList<>();
                ArrayList<Long> vals = new ArrayList<>();
                for (Map.Entry<String, Long> entry : totalTimeMap.entrySet()) {
                    cats.add(entry.getKey());
                    vals.add(entry.getValue());
                }

                ArrayList<PieEntry> entries = new ArrayList<>();
                for (int i = 0; i < cats.size(); i++) {
                    entries.add(new PieEntry(vals.get(i), cats.get(i)));
                }
                PieDataSet dataSet = new PieDataSet(entries, "Total time");
                dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                chart.setData(new PieData(dataSet));
                chart.setEntryLabelColor(Color.BLACK);
                Description d = new Description();
                d.setText("");
                chart.setDescription(d);
                chart.invalidate();
            } else {
                TextView test = (TextView) rootView.findViewById(R.id.test_text);
                test.setVisibility(View.VISIBLE);
                test.setText("No activities match selected range");
            }
            return rootView;
        }
    }
/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        Context context;
        Fragment currentFragment;



        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderPieChartFragment (defined as a static inner class below).
            if (position < 2)
                currentFragment = PlaceholderFragment.newInstance(position + 1, context);
            else
                currentFragment = PlaceholderPieChartFragment.newInstance(position + 1, context);
            return currentFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Total time spent";
                case 1:
                    return "Most recent activities";
                case 2:
                    return "Time pie chart";
            }
            return null;
        }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
}
