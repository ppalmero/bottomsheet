package info.androidhive.bottomsheet;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.androidhive.bottomsheet.enums.Consultas;
import info.androidhive.bottomsheet.listeners.Eventos;
import info.androidhive.bottomsheet.views.FechaHoraActivity;
import info.androidhive.bottomsheet.views.Teselado;
import info.androidhive.bottomsheet.ws.callWS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CONSULTA_INTERVALO = 1111;
    private static final int CONSULTA_EVENTO = 2222;

    @BindView(R.id.expandir)
    Button btnExpandir;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    /*** circle view ***/

    CircleProgressView mCircleView;
    Switch mSwitchSpin;
    Switch mSwitchShowUnit;
    SeekBar mSeekBar;
    SeekBar mSeekBarSpinnerLength;
    Boolean mShowUnit = true;
    Spinner mSpinner;
    private int tiempoActual;

    /***fin circle view ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //obtenerDatos();
        //inicializarEstructura();

        // Capture our button from layout
        ImageButton btnInicio = findViewById(R.id.btnInicio);
        // Register the onClick listener with the implementation above
        btnInicio.setOnClickListener(btnInicioListener);

        ImageButton btnAnterior = findViewById(R.id.btnAnterior);
        btnAnterior.setOnClickListener(btnAnteriorListener);

        ImageButton btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(btnSiguienteListener);

        ImageButton btnFin = findViewById(R.id.btnFin);
        btnFin.setOnClickListener(btnFinListener);

        ImageButton btnAxP = findViewById(R.id.ibAnimalesPorParcela);
        btnAxP.setOnClickListener(btnAxPListener);

        ImageButton btnES = findViewById(R.id.ibEntradaYSalidaDeAnimales);
        btnES.setOnClickListener(btnESListener);

        cargarCirculos();

        /*** LECTURA DE POSICIÓN INICIAL - USO DE WEB SERVICE Y JSON ***/
        callWS cws = new callWS();
        try {
            tiempoActual = -1;
            JSONObject reader = new JSONObject(cws.requestWS(Consultas.INICIO, null, getApplicationContext()));
            //cargarPosicionInicial(reader);
            //String sys  = reader.getString("Tiempo");
            JSONObject puntos = reader.getJSONObject("Puntos");
            Map<Integer, Vaca> vacas = new HashMap<>();
            for (int i = 0; i < puntos.length(); i++) {
                JSONObject vaca = puntos.getJSONObject("Punto" + i);
                vacas.put(i, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
            }
            sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
            sheetBehavior.setHideable(false);
            sheetBehavior.setSkipCollapsed(false);
            Teselado t = (Teselado)findViewById(R.id.teseladoView);//((LinearLayout)((CardView)layoutBottomSheet.getChildAt(1)).getChildAt(0)).getChildAt(0);
            t.setVacas(vacas);
            t.drawVacas(true);
        } catch (JSONException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        //btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                /*Context context = getApplicationContext();
                CharSequence text = "Hello toast!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
            }
        });
    }

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener btnInicioListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Inicio", Toast.LENGTH_LONG);
            toast.show();
            callWS cws = new callWS();
            try {
                tiempoActual = -1;
                JSONObject reader = new JSONObject(cws.requestWSs(Consultas.INICIO, null, getApplicationContext()));
                JSONObject puntos = reader.getJSONObject("Puntos");
                Map<Integer, Vaca> vacas = new HashMap<>();
                for (int i = 0; i < puntos.length(); i++) {
                    JSONObject vaca = puntos.getJSONObject("Punto" + i);
                    vacas.put(i, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
                }
                Teselado t = (Teselado)findViewById(R.id.teseladoView);
                t.setVacas(vacas);
                t.drawVacasInicio(true);
            } catch (JSONException e) {
                toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    private View.OnClickListener btnAnteriorListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Anterior", Toast.LENGTH_LONG);
            toast.show();
            callWS cws = new callWS();
            try {
                Map<String, Integer> parametrosWS = new HashMap<>();
                parametrosWS.put("tiempo", tiempoActual);

                JSONObject reader = new JSONObject(cws.requestWSs(Consultas.ANTERIOR, parametrosWS, getApplicationContext()));
                JSONObject puntos = reader.getJSONObject("Puntos");
                modificarVista(puntos);
                tiempoActual--;
            } catch (JSONException e) {
                toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    private View.OnClickListener btnSiguienteListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Siguiente", Toast.LENGTH_SHORT);
            toast.show();
            callWS cws = new callWS();
            try {
                Map<String, Integer> parametrosWS = new HashMap();
                parametrosWS.put("tiempo", tiempoActual + 1);

                JSONObject reader = new JSONObject(cws.requestWSs(Consultas.SIGUIENTE, parametrosWS, getApplicationContext()));
                JSONObject puntos = reader.getJSONObject("Puntos");
                modificarVista(puntos);
                tiempoActual++;
            } catch (JSONException e) {
                toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    private void modificarVista(JSONObject puntos) throws JSONException{
        Map<Integer, Vaca> vacas = new HashMap<>();
        for (int i = 0; i < puntos.length(); i++) {
            Integer oId = Integer.parseInt(((String)puntos.names().get(i)).substring(5));
            JSONObject vaca = puntos.getJSONObject("Punto" + oId);
            vacas.put(oId, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
        }
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setHideable(false);
        sheetBehavior.setSkipCollapsed(false);
        Teselado t = (Teselado) findViewById(R.id.teseladoView);
        t.setVacasModificadas(vacas);
        t.drawVacas(true);
    }

    private View.OnClickListener btnFinListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Fin", Toast.LENGTH_LONG);
            toast.show();
            callWS cws = new callWS();
            try {
                //TODO quitar hardcode, obtenerlo por WS
                tiempoActual = 1272;
                JSONObject reader = new JSONObject(cws.requestWSs(Consultas.FIN, null, getApplicationContext()));
                JSONObject puntos = reader.getJSONObject("Puntos");
                Map<Integer, Vaca> vacas = new HashMap<>();
                for (int i = 0; i < puntos.length(); i++) {
                    JSONObject vaca = puntos.getJSONObject("Punto" + i);
                    vacas.put(i, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
                }
                Teselado t = (Teselado)findViewById(R.id.teseladoView);
                t.setVacas(vacas);
                t.drawVacasInicio(true);
            } catch (JSONException e) {
                toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    private View.OnClickListener btnAxPListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, FechaHoraActivity.class);
            intent.putExtra("tipo", "intervalo");
            startActivityForResult(intent, CONSULTA_INTERVALO);
        }
    };

    private View.OnClickListener btnESListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, FechaHoraActivity.class);
            intent.putExtra("tipo", "evento");
            startActivityForResult(intent, CONSULTA_EVENTO);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONSULTA_INTERVALO && resultCode == Activity.RESULT_OK){
            final long fi = data.getExtras().getLong("FI");
            final int hi = data.getExtras().getInt("HI");
            final long ff = data.getExtras().getLong("FF");
            final int hf = data.getExtras().getInt("HF");
            Toast toast = Toast.makeText(getApplicationContext(), "Indicar parcela", Toast.LENGTH_LONG);
            toast.show();
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            Teselado t = (Teselado)findViewById(R.id.teseladoView);
            t.setOnStopTrackEventListener(new Eventos() {
                @Override
                public void onStopTrack(Float xDown, Float yDown, Float x, Float y) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Parcela: " + xDown + yDown + x + y, Toast.LENGTH_LONG);
                    toast.show();
                    callWS cws = new callWS();
                    Map<String, Float> parametrosArea = new HashMap<>();
                    parametrosArea.put("xmin", xDown.intValue() / 1000f);
                    parametrosArea.put("ymin", yDown.intValue() / 1000f);
                    parametrosArea.put("xmax", x.intValue() / 1000f);
                    parametrosArea.put("ymax", y.intValue() / 1000f);
                    Map<String, Long> parametroFechas = new HashMap<>();
                    parametroFechas.put("ti", fi);
                    parametroFechas.put("tf", ff);
                    Map<String, Integer> parametrosHoras = new HashMap<>();
                    parametrosHoras.put("hi", hi);
                    parametrosHoras.put("hf", hf);

                    try {
                        JSONObject reader = new JSONObject(cws.requestWSsConsultas(Consultas.INTERVALO, parametrosArea, parametroFechas, parametrosHoras, getApplicationContext()));
                        JSONObject puntos = reader.getJSONObject("Entradas");
                        ArrayList<Integer> vacasID = new ArrayList<>();
                        for (int i = 0; i < puntos.length(); i++) {
                            vacasID.add( puntos.getInt("ID" + i));
                        }
                        Teselado t = (Teselado)findViewById(R.id.teseladoView);
                        t.setVacasSeleccionadas(vacasID);
                        t.drawVacas(true);
                    } catch (JSONException e) {
                        toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        } else if(requestCode == CONSULTA_EVENTO && resultCode == Activity.RESULT_OK){
            final long fi = data.getExtras().getLong("FI");
            final int hi = data.getExtras().getInt("HI");
            Toast toast = Toast.makeText(getApplicationContext(), "Indicar parcela", Toast.LENGTH_LONG);
            toast.show();
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            Teselado t = findViewById(R.id.teseladoView);
            t.setOnStopTrackEventListener(new Eventos() {
                @Override
                public void onStopTrack(Float xDown, Float yDown, Float x, Float y) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Parcela: " + xDown + yDown + x + y, Toast.LENGTH_LONG);
                    toast.show();
                    callWS cws = new callWS();
                    Map<String, Float> parametrosArea = new HashMap<>();
                    parametrosArea.put("xmin", xDown.intValue() / 1000f);
                    parametrosArea.put("ymin", yDown.intValue() / 1000f);
                    parametrosArea.put("xmax", x.intValue() / 1000f);
                    parametrosArea.put("ymax", y.intValue() / 1000f);
                    Map<String, Long> parametroFechas = new HashMap<>();
                    parametroFechas.put("ti", fi);
                    Map<String, Integer> parametrosHoras = new HashMap<>();
                    parametrosHoras.put("hi", hi);

                    try {
                        JSONObject reader = new JSONObject(cws.requestWSsConsultas(Consultas.EVENTO, parametrosArea, parametroFechas, parametrosHoras, getApplicationContext()));

                        JSONObject puntosIn = reader.getJSONObject("Entradas");
                        ArrayList<Integer> vacasIDIn = new ArrayList<>();
                        for (int i = 0; i < puntosIn.length(); i++) {
                            vacasIDIn.add( puntosIn.getInt("ID" + i));
                        }

                        JSONObject puntosOut = reader.getJSONObject("Salidas");
                        ArrayList<Integer> vacasIDOut = new ArrayList<>();
                        for (int i = 0; i < puntosOut.length(); i++) {
                            vacasIDOut.add( puntosOut.getInt("ID" + i));
                        }
                        Teselado t = (Teselado)findViewById(R.id.teseladoView);
                        t.setVacasInOut(vacasIDIn, vacasIDOut);
                        t.drawVacas(true);
                    } catch (JSONException e) {
                        toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        }
    }

    private void cargarCirculos() {

        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d(TAG, "Progress Changed: " + value);
            }
        });

        //value setting
//        mCircleView.setMaxValue(100);
//        mCircleView.setValue(0);
//        mCircleView.setValueAnimated(24);

        //growing/rotating counter-clockwise
//        mCircleView.setDirection(Direction.CCW)

//        //show unit
//        mCircleView.setUnit("%");
//        mCircleView.setUnitVisible(mShowUnit);
//
//        //text sizes
//        mCircleView.setTextSize(50); // text size set, auto text size off
//        mCircleView.setUnitSize(40); // if i set the text size i also have to set the unit size
//        mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
//        //if you want the calculated text sizes to be bigger/smaller you can do so via
//        mCircleView.setUnitScale(0.9f);
//        mCircleView.setTextScale(0.9f);
//
////        //custom typeface
////        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ANDROID_ROBOT.ttf");
////        mCircleView.setTextTypeface(font);
////        mCircleView.setUnitTextTypeface(font);
//
//
//        //color
//        //you can use a gradient
//        mCircleView.setBarColor(getResources().getColor(R.color.primary), getResources().getColor(R.color.accent));
//
//        //colors of text and unit can be set via
//        mCircleView.setTextColor(Color.RED);
//        mCircleView.setTextColor(Color.BLUE);
//        //or to use the same color as in the gradient
//        mCircleView.setTextColorAuto(true); //previous set values are ignored
//
//        //text mode
//        mCircleView.setText("Text"); //shows the given text in the circle view
//        mCircleView.setTextMode(TextMode.TEXT); // Set text mode to text to show text
//
//        //in the following text modes, the text is ignored
//        mCircleView.setTextMode(TextMode.VALUE); // Shows the current value
//        mCircleView.setTextMode(TextMode.PERCENT); // Shows current percent of the current value from the max value

        //spinning
//        mCircleView.spin(); // start spinning
//        mCircleView.stopSpinning(); // stops spinning. Spinner gets shorter until it disappears.
//        mCircleView.setValueAnimated(24); // stops spinning. Spinner spins until on top. Then fills to set value.


        //animation callbacks

        //this example shows how to show a loading text if it is in spinning mode, and the current percent value otherwise.
        mCircleView.setShowTextWhileSpinning(true); // Show/hide text in spinning mode
        mCircleView.setText("Cargando...");
        mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                mCircleView.setUnitVisible(mShowUnit);
                                break;
                            case SPINNING:
                                mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );


        // region setup other ui elements
        //Setup Switch
        /*mSwitchSpin = (Switch) findViewById(R.id.switch1);
        mSwitchSpin.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mCircleView.spin();
                        } else {
                            mCircleView.stopSpinning();
                        }
                    }
                }

        );

        mSwitchShowUnit = (Switch) findViewById(R.id.switch2);
        mSwitchShowUnit.setChecked(mShowUnit);
        mSwitchShowUnit.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mCircleView.setUnitVisible(isChecked);
                        mShowUnit = isChecked;
                    }
                }

        );*/

        //Setup SeekBar
        /*mSeekBar = (SeekBar) findViewById(R.id.seekBar);

        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mCircleView.setValueAnimated(seekBar.getProgress(), 1500);
                        mSwitchSpin.setChecked(false);
                    }
                }
        );
/*
        mSeekBarSpinnerLength = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarSpinnerLength.setMax(360);
        mSeekBarSpinnerLength.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mCircleView.setSpinningBarLength(seekBar.getProgress());
                    }
                });

        mSpinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<>();
        list.add("Left Top");
        list.add("Left Bottom");
        list.add("Right Top");
        list.add("Right Bottom");
        list.add("Top");
        list.add("Bottom");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        mSpinner.setAdapter(dataAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mCircleView.setUnitPosition(UnitPosition.LEFT_TOP);
                        break;
                    case 1:
                        mCircleView.setUnitPosition(UnitPosition.LEFT_BOTTOM);
                        break;
                    case 2:
                        mCircleView.setUnitPosition(UnitPosition.RIGHT_TOP);
                        break;
                    case 3:
                        mCircleView.setUnitPosition(UnitPosition.RIGHT_BOTTOM);
                        break;
                    case 4:
                        mCircleView.setUnitPosition(UnitPosition.TOP);
                        break;
                    case 5:
                        mCircleView.setUnitPosition(UnitPosition.BOTTOM);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner.setSelection(2);*/
    }

    /**
     * manually opening / closing bottom sheet on button click
     *
    @OnClick(R.id.btn_bottom_sheet)
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            btnBottomSheet.setText("Expand sheet");
        }
    }*/

    @OnClick(R.id.expandir)
    public void expandirBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            //btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            //btnBottomSheet.setText("Expand sheet");
        }
    }

    /**
     * showing bottom sheet dialog
     *
    @OnClick(R.id.btn_bottom_sheet_dialog)
    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }


    /**
     * showing bottom sheet dialog fragment
     * same layout is used in both dialog and dialog fragment
     *
    @OnClick(R.id.btn_bottom_sheet_dialog_fragment)
    public void showBottomSheetDialogFragment() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }*/
}
