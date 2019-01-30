package info.androidhive.bottomsheet.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, min, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int min) {
        int vista = getArguments().getInt("vista");

        // Do something with the date chosen by the user
        String horario = hour + ":" + min;
        TextView tv = (TextView) getActivity().findViewById(vista);
        tv.setText(horario);
    }
}