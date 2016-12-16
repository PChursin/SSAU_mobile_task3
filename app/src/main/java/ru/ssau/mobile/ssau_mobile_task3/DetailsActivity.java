package ru.ssau.mobile.ssau_mobile_task3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ru.ssau.mobile.ssau_mobile_task3.db.CategoryOperations;
import ru.ssau.mobile.ssau_mobile_task3.model.Category;
import ru.ssau.mobile.ssau_mobile_task3.model.Record;

/**
 * Created by Pavel on 17.12.2016.
 */

public class DetailsActivity extends AppCompatActivity {

    Spinner categorySpinner;
    DatePickerDialog.OnDateSetListener startDateListener, endDateListener;
    TimePickerDialog.OnTimeSetListener startTimeListener, endTimeListener;
    Calendar dateStart, dateEnd;
    EditText summaryField;
    TextView fromDateLabel, fromTimeLabel, toDateLabel, toTimeLabel;
    SimpleDateFormat dateOnly, timeOnly;

    CategoryOperations categoryOperations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        categoryOperations = CategoryOperations.getInstance(this);

        categorySpinner = (Spinner) findViewById(R.id.edit_spinner);
        fromDateLabel = (TextView) findViewById(R.id.edit_from_date);
        fromTimeLabel = (TextView) findViewById(R.id.edit_from_time);
        toDateLabel = (TextView) findViewById(R.id.edit_to_date);
        toTimeLabel = (TextView) findViewById(R.id.edit_to_time);
        summaryField = (EditText) findViewById(R.id.edit_summary);

        dateOnly = new SimpleDateFormat("EE, dd MMM yyyy");
        timeOnly = new SimpleDateFormat("HH:mm");

        dateStart = Calendar.getInstance();
        dateEnd = Calendar.getInstance();
        setUpPickers();

        ArrayList<String> catNames = new ArrayList<>();
        for (Category cat : categoryOperations.getAllCategories()) {
            catNames.add(cat.getName());
        }
        categorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, catNames));
        Record rec = (Record) getIntent().getSerializableExtra("record");
        if (rec != null) {
            categorySpinner.setSelection(catNames.indexOf(rec.getCategory().getName()));
            dateStart.setTimeInMillis(rec.getStart());
            if (rec.getEnd() > 0)
                dateEnd.setTimeInMillis(rec.getEnd());
            if (rec.getSummary() != null)
                summaryField.setText(rec.getSummary());
        }
        fromDateLabel.setText(dateOnly.format(dateStart.getTime()));
        fromTimeLabel.setText(timeOnly.format(dateStart.getTime()));
        toDateLabel.setText(dateOnly.format(dateEnd.getTime()));
        toTimeLabel.setText(timeOnly.format(dateEnd.getTime()));
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
            }
        };

        final DatePickerDialog startDatePicker = new DatePickerDialog(DetailsActivity.this, startDateListener,
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
            }
        };

        final DatePickerDialog endDatePicker = new DatePickerDialog(DetailsActivity.this, endDateListener,
                dateEnd.get(Calendar.YEAR), dateEnd.get(Calendar.MONTH), dateEnd.get(Calendar.DAY_OF_MONTH));


        toDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDatePicker.show();
            }
        });

        //START TIME PICKER SETUP
        startTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                dateStart.set(Calendar.HOUR_OF_DAY, i);
                dateStart.set(Calendar.MINUTE, i1);
                fromTimeLabel.setText(timeOnly.format(dateStart.getTime()));
            }
        };

        final TimePickerDialog startTimePicker = new TimePickerDialog(DetailsActivity.this, startTimeListener,
                dateStart.get(Calendar.HOUR_OF_DAY), dateStart.get(Calendar.MINUTE), true);

        fromTimeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePicker.show();
            }
        });

        //START TIME PICKER SETUP
        endTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                dateEnd.set(Calendar.HOUR_OF_DAY, i);
                dateEnd.set(Calendar.MINUTE, i1);
                toTimeLabel.setText(timeOnly.format(dateEnd.getTime()));
            }
        };

        final TimePickerDialog endTimePicker = new TimePickerDialog(DetailsActivity.this, endTimeListener,
                dateEnd.get(Calendar.HOUR_OF_DAY), dateEnd.get(Calendar.MINUTE), true);

        toTimeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimePicker.show();
            }
        });
    }
}
