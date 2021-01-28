package com.example.tripreminder2021;
//

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public  class TimePickerFragment extends DialogFragment {

   @NonNull
   @Override
   public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
       //default current time shown when appearing
       Calendar c =Calendar.getInstance();
       int hour = c.get(Calendar.HOUR_OF_DAY);
       int min = c.get(Calendar.MINUTE);
       return new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener) getActivity(),hour,min, DateFormat.is24HourFormat(getActivity()));

   }

}
