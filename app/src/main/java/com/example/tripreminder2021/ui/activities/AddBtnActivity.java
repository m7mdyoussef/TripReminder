package com.example.tripreminder2021.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tripreminder2021.repository.FirebaseDatabaseServices;
import com.example.tripreminder2021.zService.AlarmEventReciever;
import com.example.tripreminder2021.R;
import com.example.tripreminder2021.config.*;
import com.example.tripreminder2021.ui.fragment.DatePickerFragment;
import com.example.tripreminder2021.ui.fragment.TimePickerFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.tripreminder2021.pojo.TripModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBtnActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    public static final String NEW_TRIP_OBJECT = "NEW_TRIP_OBJECT";
    public static final String NEW_TRIP_OBJ_SERIAL = "NEW_TRIP_OBJECT";
    public int hours2 = 2;
    public int minutes2;
    @BindView(R.id.add_trip_btn)
    Button addTripBtn;
    @BindView(R.id.cancel_btn)
    Button cancelBtn;
    @BindView(R.id.repeat_spinner)
    Spinner repeatSpinner;
    @BindView(R.id.trip_way_spinner)
    Spinner tripWaySpinner;
    @BindView(R.id.repeat_spin_linearlayout)
    LinearLayout repeatSpinLinearlayout;
    @BindView(R.id.add_note_btn)
    ImageButton addNoteBtn;
    @BindView(R.id.note_text_field)
    TextInputLayout noteTextField;
    @BindView(R.id.notes_linearLayout)
    LinearLayout notesLinearLayout;
    @BindView(R.id.dateTextField)
    TextInputEditText dateTextField;
    @BindView(R.id.timeTextField)
    TextInputEditText timeTextField;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.trip_name_text_field)
    TextInputLayout tripNameTextField;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.dateEdit_back)
    TextInputEditText dateEdit_back;
    @BindView(R.id.clockEdit_back)
    TextInputEditText clockEdit_back;
    @BindView(R.id.TextInputTime2)
    TextInputLayout TextInputTime2;
    @BindView(R.id.TextInputDate2)
    TextInputLayout TextInputDate2;
    PlacesClient mPlacesClient;
    List<Place.Field> placeField = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
    int hour;
    int minutes;
    int years2;
    int months2;
    int days2;
    int increasedID = 0;
    int SigleRoundposition = 0;
    ArrayAdapter<CharSequence> adapterTripDirectionSpin;
    ArrayAdapter<CharSequence> adapterTripRepeatSpin;
    List<TextInputLayout> mNotesTextInputLayout = new ArrayList<>();
    String selectedStartPlace = "";
    String selectedEndPlace = "";
    List<String> notesList = new ArrayList<>();

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    Calendar mCalendar;
    Calendar myCalendarRound;
    Calendar currentCalendar;
    private FirebaseDatabaseServices firebaseDatabaseServices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_btn);

        ButterKnife.bind(this);
        mCalendar = Calendar.getInstance();
        myCalendarRound = Calendar.getInstance();
        currentCalendar = Calendar.getInstance();
        // hideProgressBar();

        firebaseDatabaseServices=new FirebaseDatabaseServices();

        //Auto Complete Google
        setUpAutoComplete();

        //Spinner init
        spinnerInit();

        mNotesTextInputLayout.add(noteTextField);

    }

    private void setUpAutoComplete() {
        AutocompleteSupportFragment placeStartPointAutoComplete;
        AutocompleteSupportFragment placeDestPointAutoComplete;
        if (!Places.isInitialized()) {
            // @TODO Get Places API key

            Places.initialize(getApplicationContext(), "AIzaSyDhtVSlNM52yj-vH7H7SMEFswg7CtaVCUQ");
//            mPlacesClient=Places.createClient(this);     AIzaSyDhtVSlNM52yj-vH7H7SMEFswg7CtaVCUQ
        }
        //Init Frags
        placeStartPointAutoComplete = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.start_autoComplete_Frag);
        placeStartPointAutoComplete.setPlaceFields(placeField);

        placeDestPointAutoComplete = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.dest_autoComplete_Frag);
        placeDestPointAutoComplete.setPlaceFields(placeField);

        placeStartPointAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Places", "Place: " + place.getAddress() + ", " + place.getId());
                selectedStartPlace = place.getAddress();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Places", "An error occurred: " + status);
            }
        });
        placeDestPointAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Places", "Place: " + place.getAddress() + ", " + place.getId());
                selectedEndPlace = place.getAddress();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Places", "An error occurred: " + status);
            }
        });

    }

    @OnClick({R.id.add_trip_btn, R.id.add_note_btn, R.id.dateTextField,
            R.id.timeTextField, R.id.cancel_btn, R.id.clockEdit_back, R.id.dateEdit_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_trip_btn:

                //@TODO Copy this to another place !
                for (TextInputLayout txtLayout : mNotesTextInputLayout) {
                    Log.i("Notes List", txtLayout.getEditText().getText().toString());
                    notesList.add(txtLayout.getEditText().getText().toString());
                }
                if (tripNameTextField.getEditText().getText().toString().equals("")) {
                    tripNameTextField.setError("Cannot be blank!");
                } else if (dateTextField.getText().toString().equals("")) {
                    dateTextField.setError("Cannot be blank!");
                } else if (timeTextField.getText().toString().equals("")) {
                    timeTextField.setError("Cannot be blank!");
                } else {

                    if (SigleRoundposition == 0) {
                        dateEdit_back.setVisibility(View.GONE);
                        clockEdit_back.setVisibility(View.GONE);

                        TripModel newTrip = new TripModel("1",selectedStartPlace, selectedEndPlace,
                                dateTextField.getText().toString(),
                                timeTextField.getText().toString(),
                                tripNameTextField.getEditText().getText().toString()
                                , "start", notesList, mCalendar.getTime().toString(),
                                Constants.SEARCH_CHILD_UPCOMING_KEY);



                        firebaseDatabaseServices.addTrip(newTrip);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("NEWTRIP", (Serializable) newTrip);
                        startAlarm(newTrip);
                        setResult(Activity.RESULT_OK, resultIntent);
                        Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();


                    }
                    if (SigleRoundposition == 1) {

                        if (myCalendarRound.compareTo(mCalendar) <= 0) {
                            Toast.makeText(AddBtnActivity.this, "cannot return before going", Toast.LENGTH_SHORT).show();
                        } else {

                            TripModel newTrip = new TripModel("1",selectedStartPlace, selectedEndPlace,
                                    dateTextField.getText().toString(),
                                    timeTextField.getText().toString(),
                                    tripNameTextField.getEditText().getText().toString()
                                    , "start", notesList, mCalendar.getTime().toString(),
                                    Constants.SEARCH_CHILD_UPCOMING_KEY);

                            firebaseDatabaseServices.addTrip(newTrip);


                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("NEWTRIP", (Serializable) newTrip);
                            startAlarm(newTrip);
                            setResult(Activity.RESULT_OK, resultIntent);



                            TripModel TripBack = new TripModel("1",selectedStartPlace, selectedEndPlace,
                                    dateTextField.getText().toString(),
                                    timeTextField.getText().toString(),
                                    tripNameTextField.getEditText().getText().toString()+ " Back"
                                    , "start", notesList, mCalendar.getTime().toString(),
                                    Constants.SEARCH_CHILD_UPCOMING_KEY);

                            firebaseDatabaseServices.addTrip(newTrip);

                            Intent resultIntentback = new Intent();
                            resultIntentback.putExtra("TripBack", (Serializable) TripBack);
                            startAlarmBack(TripBack);
                            setResult(Activity.RESULT_OK, resultIntentback);


                            Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }


                    }


                }
                break;
            case R.id.add_note_btn:
                generateNoteLayout(view);
                break;
            case R.id.dateTextField:
                DialogFragment datepicker = new DatePickerFragment();

                datepicker.show(getSupportFragmentManager(), "date");

                break;
            case R.id.timeTextField:
                DialogFragment timepicker = new TimePickerFragment();
                timepicker.show(getSupportFragmentManager(), "time");
                break;

            case R.id.dateEdit_back:
                //////////////////////////////// round picker ///////////////////////////////////////
                final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        years2 = year;
                        months2 = monthOfYear;
                        days2 = dayOfMonth;
                        myCalendarRound.set(Calendar.YEAR, year);
                        myCalendarRound.set(Calendar.MONTH, monthOfYear);
                        myCalendarRound.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = DateFormat.getDateInstance(DateFormat.FULL).format(myCalendarRound.getTime());
                        ; //In which you need put here
                        dateEdit_back.setText(myFormat);
                    }
                };
                new DatePickerDialog(AddBtnActivity.this, date1, currentCalendar
                        .get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH),
                        currentCalendar.get(Calendar.DAY_OF_MONTH)).show();


                break;
            case R.id.clockEdit_back:
                //////////////////////////////// round picker ///////////////////////////////////////
                Calendar mcurrentTime2 = Calendar.getInstance();
                int hour = mcurrentTime2.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime2.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker2;
                mTimePicker2 = new TimePickerDialog(AddBtnActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        hours2 = selectedHour;
                        minutes2 = selectedMinute;
                        String timeSet2 = "";
                        if (hours2 > 12) {
                            hours2 -= 12;
                            timeSet2 = "PM";
                        } else if (hours2 == 0) {
                            hours2 += 12;
                            timeSet2 = "AM";
                        } else if (hours2 == 12) {
                            timeSet2 = "PM";
                        } else {
                            timeSet2 = "AM";
                        }

                        String min2 = "";
                        if (minutes2 < 10)
                            min2 = "0" + minutes2;
                        else
                            min2 = String.valueOf(minutes2);

                        // Append in a StringBuilder
                        String aTime2 = new StringBuilder().append(hours2).append(':')
                                .append(min2).append(" ").append(timeSet2).toString();
                        clockEdit_back.setText(aTime2);
                        myCalendarRound.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendarRound.set(Calendar.MINUTE, selectedMinute - 1);
                        myCalendarRound.set(Calendar.SECOND, 59);
                    }
                }, hour, minute, false);
                mTimePicker2.setTitle("Select Time");
                mTimePicker2.show();


                break;

            case R.id.cancel_btn:
                finish();
                break;
        }
    }

    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, i);
        c.set(Calendar.MONTH, i1);
        c.set(Calendar.DAY_OF_MONTH, i2);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        Log.i("Date Time Picker", currentDateString);
        dateTextField.setText(i2+"-"+(i1 + 1)+"-"+ i);

        mCalendar.set(Calendar.YEAR, i);
        mCalendar.set(Calendar.MONTH, i1); // Month is zero-based
        mCalendar.set(Calendar.DAY_OF_MONTH, i2);


    }


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        hour = i;
        minutes = i1;
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String min = "";
        if (minutes < 10)
            min = "0" + minutes;
        else
            min = String.valueOf(minutes);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hour).append(':')
                .append(min).append(" ").append(timeSet).toString();
        timeTextField.setText(aTime);


        // Set calendat item
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, i);
        mCalendar.set(Calendar.MINUTE, i1);
        mCalendar.set(Calendar.SECOND, 0);

    }
    private void spinnerInit() {
        //Trip Direction Spinner
        adapterTripDirectionSpin = ArrayAdapter.createFromResource(this,
                R.array.trip_direction_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTripDirectionSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        tripWaySpinner.setAdapter(adapterTripDirectionSpin);
        tripWaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                Log.d("Spinner", adapterView.getSelectedItemPosition() + "///////");

                SigleRoundposition = adapterView.getSelectedItemPosition();
                if (SigleRoundposition == 1) {

                    TextInputDate2.setVisibility(View.VISIBLE);
                    TextInputTime2.setVisibility(View.VISIBLE);
                }
                if (SigleRoundposition == 0) {

                    TextInputDate2.setVisibility(View.GONE);
                    TextInputTime2.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                SigleRoundposition = 0;
            }
        });

        //Trip Repeat Spinner
        adapterTripRepeatSpin = ArrayAdapter.createFromResource(this,
                R.array.times_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTripRepeatSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        repeatSpinner.setAdapter(adapterTripRepeatSpin);
        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                Log.i("Spinner", adapterView.getItemAtPosition(pos).toString() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void generateNoteLayout(View view) {
        final LinearLayout currentParent = findViewById(R.id.notes_parent_linear_Layout);

        final View linearLayout = getLayoutInflater().inflate(R.layout.add_notes_sayout_sample, null);

        final TextInputLayout noteTextInput = linearLayout.findViewById(R.id.note_text_field_input);
        mNotesTextInputLayout.add(noteTextInput);

        ImageButton subImgBtn = linearLayout.findViewById(R.id.sub_note_img_btn);
        subImgBtn.setOnClickListener(v -> {
            currentParent.removeView(linearLayout);
            mNotesTextInputLayout.remove(noteTextInput);
        });


        currentParent.addView(linearLayout);
        increasedID++;

    }

    private void startAlarm(TripModel tripModel) {

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        Log.i("time", mCalendar.getTime().toString());
//        long alarmTime = mCalendar.getTimeInMillis();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(tripModel.getDateTime()));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, AlarmEventReciever.class);

        Bundle b = new Bundle();
        b.putParcelable(AddBtnActivity.NEW_TRIP_OBJ_SERIAL, tripModel);
        intent.putExtra(NEW_TRIP_OBJECT, b);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }


    private void startAlarmBack(TripModel tripModel) {
        AlarmManager alarmManager2 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        Log.i("time", mCalendar.getTime().toString());
//        long alarmTime = mCalendar.getTimeInMillis();
        Calendar cal2 = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            cal2.setTime(sdf2.parse( mCalendar.getTime().toString()));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent2 = new Intent(this, AlarmEventReciever.class);
        intent2.putExtra(NEW_TRIP_OBJECT, (Serializable) tripModel);

        Bundle b2 = new Bundle();
        b2.putSerializable(AddBtnActivity.NEW_TRIP_OBJ_SERIAL, tripModel);
        intent2.putExtra(NEW_TRIP_OBJECT, b2);


        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 0, intent2, 0);
        alarmManager2.set(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), pendingIntent2);
    }





}
