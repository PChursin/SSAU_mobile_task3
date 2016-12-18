package ru.ssau.mobile.ssau_mobile_task3;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ru.ssau.mobile.ssau_mobile_task3.adapters.PhotoListAdapter;
import ru.ssau.mobile.ssau_mobile_task3.adapters.RVAdapter;
import ru.ssau.mobile.ssau_mobile_task3.db.CategoryOperations;
import ru.ssau.mobile.ssau_mobile_task3.db.PhotoOperations;
import ru.ssau.mobile.ssau_mobile_task3.db.RecordOperations;
import ru.ssau.mobile.ssau_mobile_task3.model.Category;
import ru.ssau.mobile.ssau_mobile_task3.model.Photo;
import ru.ssau.mobile.ssau_mobile_task3.model.Record;
import ru.ssau.mobile.ssau_mobile_task3.model.RecordSendable;

/**
 * Created by Pavel on 17.12.2016.
 */

public class DetailsActivity extends AppCompatActivity {

    Record rec;

    Spinner categorySpinner;
    DatePickerDialog.OnDateSetListener startDateListener, endDateListener;
    TimePickerDialog.OnTimeSetListener startTimeListener, endTimeListener;
    Calendar dateStart, dateEnd;
    EditText summaryField;
    TextView fromDateLabel, fromTimeLabel, toDateLabel, toTimeLabel;
    GridView photoList;
    SimpleDateFormat dateOnly, timeOnly;
    Button submit;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    PhotoListAdapter adapter;
    RVAdapter rvAdapter;

    ArrayList<Photo> photos, pendingDelete;
    private final String TAG = "DetailsActivity";

    CategoryOperations categoryOperations;
    PhotoOperations photoOperations;
    RecordOperations recordOperations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        categoryOperations = CategoryOperations.getInstance(this);
        photoOperations = PhotoOperations.getInstance(this);
        recordOperations = RecordOperations.getInstance(this);

        categorySpinner = (Spinner) findViewById(R.id.edit_spinner);
        fromDateLabel = (TextView) findViewById(R.id.edit_from_date);
        fromTimeLabel = (TextView) findViewById(R.id.edit_from_time);
        toDateLabel = (TextView) findViewById(R.id.edit_to_date);
        toTimeLabel = (TextView) findViewById(R.id.edit_to_time);
        summaryField = (EditText) findViewById(R.id.edit_summary);
        submit = (Button) findViewById(R.id.edit_submit);
        //photoList = (GridView) findViewById(R.id.edit_photos);

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

        photos = new ArrayList<>();

        if (savedInstanceState != null) {
            rec = (Record) savedInstanceState.getSerializable("record");
            pendingDelete = (ArrayList<Photo>) savedInstanceState.getSerializable("pendingDelete");
        }
        else {
            RecordSendable sendable = (RecordSendable) getIntent().getSerializableExtra("record");
            pendingDelete = new ArrayList<>();
            if (sendable != null) {
                ArrayList<Photo> photoArrayList = recordOperations.getAttachedPhotos(sendable.getPhotos());
                rec = new Record(sendable.getId(), sendable.getCategory(), sendable.getStart(),
                        sendable.getEnd(), sendable.getMinutes(), sendable.getSummary(), photoArrayList);
            }
        }

        if (rec != null) {
            categorySpinner.setSelection(catNames.indexOf(rec.getCategory().getName()));
            dateStart.setTimeInMillis(rec.getStart());
            if (rec.getEnd() > 0)
                dateEnd.setTimeInMillis(rec.getEnd());
            if (rec.getSummary() != null)
                summaryField.setText(rec.getSummary());
            if (rec.getPhotos() != null) {
                for (Photo p : rec.getPhotos())
                    photos.add(0, p);
            }
        } else {
            rec = new Record();
            rec.setId(-1);
        }
        photos.add(new Photo(-1, new byte[0]));
        fromDateLabel.setText(dateOnly.format(dateStart.getTime()));
        fromTimeLabel.setText(timeOnly.format(dateStart.getTime()));
        toDateLabel.setText(dateOnly.format(dateEnd.getTime()));
        toTimeLabel.setText(timeOnly.format(dateEnd.getTime()));


        recyclerView = (RecyclerView)findViewById(R.id.edit_photos);
        rvAdapter = new RVAdapter(this, photos);
        rvAdapter.setPendingDelete(pendingDelete);
// Give it a horizontal LinearLayoutManager to make it a horizontal ListView
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(rvAdapter);
        //adapter = new PhotoListAdapter(this, R.layout.photo_list_item, photos);
        //photoList.setAdapter(adapter);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecord();
                if (rec.getId() >= 0)
                    recordOperations.updateRecord(rec, rec);
                else {
                    String photoString = RecordOperations.getPhotoString(rec);
                    recordOperations.addRecord(rec.getCategory().getId(), rec.getStart(), rec.getEnd(),
                            rec.getMinutes(), rec.getSummary(), photoString);
                }
                ArrayList<Photo> toDelete = rvAdapter.getPendingDelete();
                for (Photo p : toDelete)
                    photoOperations.deletePhoto(p);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;

        switch(requestCode) {
            case PhotoListAdapter.GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    showProgressDialog("Converting image");
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                        FileOutputStream fos = getApplicationContext().openFileOutput(
//                                "compress.jpg", Context.MODE_PRIVATE);
                        int k = 40;
                        byte[] compressed;
                        while (true) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            //bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, k, baos);
                            compressed = baos.toByteArray();
                            Log.d(TAG, "onActivityResult: compressed size is " + compressed.length);
                            if (compressed.length <= 1024*1024) {
                                Log.d(TAG, "onActivityResult: compression coef is "+k);
                                break;
                            }
                            k -= 10;
                            if (k < 0) {
                                Log.w(TAG, "onActivityResult: file is too big!");
                                return;
                            }
                        }

////                        fos.close();
//                        FileInputStream fis = getApplicationContext().openFileInput(
//                                "compress.jpg");
//                        bitmap = BitmapFactory.decodeStream(fis);
//                        fis.close();
                        //byte[] blob = Photo.getBitmapAsByteArray(bitmap);
//                        Photo p = photoOperations.addPhoto(blob);
                        Photo p = photoOperations.addPhoto(compressed);
//                        adapter.getData().add(photos.size()-1, p);
//                        adapter.notifyDataSetChanged();
                        rvAdapter.getData().add(photos.size()-1, p);
                        rvAdapter.notifyDataSetChanged();
                    } catch (IOException e) {
                        Log.e(TAG, "onActivityResult: ", e);
                    } finally {
                        hideProgressDialog();
                    }
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        updateRecord();
        outState.putSerializable("pendingDelete", rvAdapter.getPendingDelete());
        outState.putSerializable("record", rec);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void updateRecord() {
        rec.setStart(dateStart.getTimeInMillis());
        rec.setEnd(dateEnd.getTimeInMillis());
        rec.setMinutes((rec.getEnd() - rec.getStart()) / 60000);
        rec.setSummary(summaryField.getText().toString());
        Category cat = categoryOperations.getCategory(categorySpinner.getSelectedItem().toString());
        rec.setCategory(cat);
        ArrayList<Photo> photosCopy = (ArrayList<Photo>) photos.clone();
        photosCopy.remove(photosCopy.size()-1);
        rec.setPhotos(photosCopy);
    }

    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(msg);
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
