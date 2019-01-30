package info.androidhive.bottomsheet.views;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import info.androidhive.bottomsheet.R;

public class FechaHoraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha_hora);

        ImageButton ibFI = findViewById(R.id.ibFI);
        ibFI.setOnClickListener(ibFIListener);

        ImageButton ibHI = findViewById(R.id.ibHI);
        ibHI.setOnClickListener(ibHIListener);

        ImageButton ibFF = findViewById(R.id.ibFF);
        ibFF.setOnClickListener(ibFFListener);

        ImageButton ibHF = findViewById(R.id.ibHF);
        ibHF.setOnClickListener(ibHFListener);

        Button btOK = findViewById(R.id.btOK);
        btOK.setOnClickListener(btOKListener);
    }

    private View.OnClickListener ibFIListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle args = new Bundle();
            int vista = R.id.etFI;
            args.putInt("vista",vista);

            newFragment.setArguments(args);
            newFragment.show(getFragmentManager(), "datePicker");
        }
    };

    private View.OnClickListener ibHIListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new TimePickerFragment();
            Bundle args = new Bundle();
            int vista = R.id.etHI;
            args.putInt("vista",vista);
            newFragment.setArguments(args);
            newFragment.show(getFragmentManager(), "timePicker");
        }
    };

    private View.OnClickListener ibFFListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle args = new Bundle();
            int vista = R.id.etFF;
            args.putInt("vista",vista);
            newFragment.setArguments(args);
            newFragment.show(getFragmentManager(), "datePicker");
        }
    };

    private View.OnClickListener ibHFListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new TimePickerFragment();
            Bundle args = new Bundle();
            int vista = R.id.etHF;
            args.putInt("vista",vista);
            newFragment.setArguments(args);
            newFragment.show(getFragmentManager(), "timePicker");
        }
    };

    private View.OnClickListener btOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            TextView tvFI = (TextView) findViewById(R.id.etFI);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateFI = null;
            try {
                dateFI = sdf.parse(tvFI.getText().toString());
            } catch (ParseException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "La fecha debe ser dada en formato dd/mm/yyyy", Toast.LENGTH_LONG);
                toast.show();
            }

            TextView tvHI = (TextView) findViewById(R.id.etHI);
            String strHI = tvHI.getText().toString();

            TextView tvFF = (TextView) findViewById(R.id.etFF);
            Date dateFF = null;
            try {
                dateFF = sdf.parse(tvFF.getText().toString());
            } catch (ParseException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "La fecha debe ser dada en formato dd/mm/yyyy", Toast.LENGTH_LONG);
                toast.show();
            }

            TextView tvHF = (TextView) findViewById(R.id.etHF);
            String strHF = tvHF.getText().toString();

            Intent intent = new Intent();
            intent.putExtra("FI", dateFI.getTime());
            intent.putExtra("HI", Integer.parseInt(strHI.substring(0, strHI.indexOf(":"))));
            intent.putExtra("FF", dateFF.getTime());
            intent.putExtra("HF", Integer.parseInt(strHF.substring(0, strHF.indexOf(":"))));
            setResult(RESULT_OK, intent);
            finish();
        }
    };
}
