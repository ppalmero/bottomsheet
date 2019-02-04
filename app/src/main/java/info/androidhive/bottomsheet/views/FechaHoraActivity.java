package info.androidhive.bottomsheet.views;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.androidhive.bottomsheet.R;

public class FechaHoraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha_hora);

        Bundle extras = getIntent().getExtras();
        String s = extras.getString("tipo");

        ImageButton ibFI = findViewById(R.id.ibFI);
        ibFI.setOnClickListener(ibFIListener);

        ImageButton ibHI = findViewById(R.id.ibHI);
        ibHI.setOnClickListener(ibHIListener);


        if (s.equals("evento")) {
            TextView tvV = findViewById(R.id.tvVacas);
            tvV.setVisibility(View.INVISIBLE);
            Spinner spV = findViewById(R.id.spVacas);
            spV.setVisibility(View.INVISIBLE);

            TextView tvFF = findViewById(R.id.tvFF);
            tvFF.setVisibility(View.INVISIBLE);
            LinearLayout llFF = findViewById(R.id.llFF);
            llFF.setVisibility(View.INVISIBLE);

            TextView tvHF = findViewById(R.id.tvHF);
            tvHF.setVisibility(View.INVISIBLE);
            LinearLayout llHF = findViewById(R.id.llHF);
            llHF.setVisibility(View.INVISIBLE);
        } else {
            ImageButton ibFF = findViewById(R.id.ibFF);
            ImageButton ibHF = findViewById(R.id.ibHF);
            ibFF.setOnClickListener(ibFFListener);
            ibHF.setOnClickListener(ibHFListener);
            if (s.equals("intervalo")) {
                TextView tvV = findViewById(R.id.tvVacas);
                tvV.setVisibility(View.INVISIBLE);
                Spinner spV = findViewById(R.id.spVacas);
                spV.setVisibility(View.INVISIBLE);
            } else {
                ArrayList<String> list = new ArrayList<>();
                Spinner spV = findViewById(R.id.spVacas);
                for (int i = 0; i < 200; i++) {
                    list.add("Vaca " + i);
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spV.setAdapter(dataAdapter);
            }
        }

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
            Intent intent = new Intent();

            TextView tvV = findViewById(R.id.tvVacas);
            if (tvV.isShown()) {
                Spinner spV = findViewById(R.id.spVacas);
                intent.putExtra("Vaca", spV.getSelectedItemPosition());//TODO ver si se corresponde el ID de vaca con el item seleccionado
            }
            TextView tvFI = findViewById(R.id.etFI);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateFI = null;
            try {
                dateFI = sdf.parse(tvFI.getText().toString());
            } catch (ParseException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "La fecha debe ser dada en formato dd/mm/yyyy", Toast.LENGTH_LONG);
                toast.show();
            }

            TextView tvHI = findViewById(R.id.etHI);
            String strHI = tvHI.getText().toString();

            intent.putExtra("FI", dateFI.getTime());
            intent.putExtra("HI", Integer.parseInt(strHI.substring(0, strHI.indexOf(":"))));
            TextView tvFF = findViewById(R.id.etFF);
            if (tvFF.isShown()) {
                Date dateFF = null;
                try {
                    dateFF = sdf.parse(tvFF.getText().toString());
                } catch (ParseException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "La fecha debe ser dada en formato dd/mm/yyyy", Toast.LENGTH_LONG);
                    toast.show();
                }
                TextView tvHF = findViewById(R.id.etHF);
                String strHF = tvHF.getText().toString();
                intent.putExtra("FF", dateFF.getTime());
                intent.putExtra("HF", Integer.parseInt(strHF.substring(0, strHF.indexOf(":"))));
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    };
}
