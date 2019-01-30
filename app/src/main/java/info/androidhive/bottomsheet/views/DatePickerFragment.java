package info.androidhive.bottomsheet.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import info.androidhive.bottomsheet.R;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Date et_fechaIni;
    private Date et_fechaFin;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        int vista = getArguments().getInt("vista");

        // Do something with the date chosen by the user
        month++;
        String fecha = ((day < 10)? "0" + day : day) + "/" + ((month < 10)? "0" + month : month) + "/" + year;
        TextView tv = (TextView) getActivity().findViewById(vista);
        tv.setText(fecha);
    }

    public Date getEt_fechaIni() {
        return et_fechaIni;
    }

    public Date getEt_fechaFin() {
        return et_fechaFin;
    }
}