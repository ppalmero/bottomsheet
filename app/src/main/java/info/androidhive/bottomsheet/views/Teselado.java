package info.androidhive.bottomsheet.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bdet.comun.Punto;
import info.androidhive.bottomsheet.R;
import info.androidhive.bottomsheet.Vaca;
import info.androidhive.bottomsheet.listeners.Eventos;

/**
 * TODO: document your custom view class.
 */
public class Teselado extends View {

    Path path = new Path();
    String accion;
    float x = 50, y = 50;
    float xDown, yDown;
    private boolean dibujarCirculo = false;
    private boolean dibujarVacas;
    private Map<Integer, Vaca> vacas;
    private Map<Integer, Vaca> vacasModificadas = new HashMap<>();
    private Map<Integer, Vaca> oldsVacas = new HashMap<>();
    private ArrayList<Integer> vacasSeleccionadas = new ArrayList<>();
    private ArrayList<Integer> vacasIn = new ArrayList<>();
    private ArrayList<Integer> vacasOut = new ArrayList<>();
    private float ancho, alto;
    private int vacaSelected;
    private ArrayList<Punto> trayectoria;
    private boolean drawTrayectoria = false;
    private boolean drawEvento = false;
    private boolean drawIntervalo = false;

    public Teselado(Context context) {
        super(context);
    }

    public Teselado(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Teselado(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onDraw(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLUE);

        ancho = canvas.getWidth() / 1000f;
        alto  = canvas.getHeight() / 1000f;

        if (accion == "down"){
            //path.moveTo(x, y);
            xDown = x;
            yDown = y;
        }
        if (accion == "move" && (drawEvento || drawIntervalo)){
            //path.lineTo(x, y);
            path.reset();
            if ((x < xDown) && (y < yDown)) {
                path.addRect(x, y, xDown, yDown, Path.Direction.CW);
            } else if (x < xDown) {
                path.addRect(x, yDown, xDown, y, Path.Direction.CW);
            } else if (y < yDown) {
                path.addRect(xDown, y, x, yDown, Path.Direction.CW);
            } else {
                path.addRect(xDown, yDown, x, y, Path.Direction.CW);
            }
            canvas.drawPath(path, paint);
        }

        if (accion == "select") {
            //TODO seleccionar vaca
            Point raton = new Point(Math.round(x / ancho), Math.round(y / alto));
            for (int i = 0; i < vacas.size(); i++) {
                Region r = new Region (getRegion(new Point(vacas.get(i).getX(), vacas.get(i).getY()), 50));
                if (r.contains(raton.x, raton.y)) {
                    vacaSelected = i;
                    Toast toast = Toast.makeText(getContext(), "Vaca elegida: " + i, Toast.LENGTH_LONG);
                    toast.show();
                    // TODO Lanzar evento para obtener la info de la vaca elegida.
                }
            }
        }

        if (dibujarVacas){
            //canvas.drawCircle(30/2, 30/2, 30, paint);
            Drawable d = getResources().getDrawable(R.drawable.vaca_actionbar, null);
            System.out.println("Cantidad de vacas modificadas: " + vacasModificadas.size());
            for (Integer i : vacas.keySet()) {
                if (vacasModificadas.containsKey(i)){
                    d.setBounds((int) (vacasModificadas.get(i).getX() * ancho), (int) (vacasModificadas.get(i).getY() * alto), (int)((vacasModificadas.get(i).getX() + d.getIntrinsicWidth()) * ancho), (int)((vacasModificadas.get(i).getY() + d.getIntrinsicHeight()) * alto));
                    d.draw(canvas);
                    if (drawIntervalo && vacasSeleccionadas.contains(i)){
                        paint.setColor(Color.YELLOW);
                        canvas.drawCircle((vacasModificadas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacasModificadas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, d.getIntrinsicWidth() / 2, paint);
                        paint.setColor(Color.BLUE);
                    } else if (drawEvento) {
                        if (vacasIn.contains(i)){
                            paint.setColor(Color.GREEN);
                            canvas.drawCircle((vacasModificadas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacasModificadas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, d.getIntrinsicWidth() / 2, paint);
                            paint.setColor(Color.BLUE);
                        } else if (vacasOut.contains(i)) {
                            paint.setColor(Color.RED);
                            canvas.drawCircle((vacasModificadas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacasModificadas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, d.getIntrinsicWidth() / 2, paint);
                            paint.setColor(Color.BLUE);
                        }
                    } else if (drawTrayectoria && vacasModificadas.containsKey(vacaSelected)) {
                        paint.setColor(Color.rgb(255, 127, 0));
                        canvas.drawCircle((vacasModificadas.get(vacaSelected).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacasModificadas.get(vacaSelected).getY() + d.getIntrinsicHeight() / 2) * alto, d.getIntrinsicWidth() / 2, paint);
                        int tSize = trayectoria.size() - 1;
                        for (int j = 0; j < tSize; j++){
                            canvas.drawLine((trayectoria.get(j).x + d.getIntrinsicWidth() / 2) * ancho, (trayectoria.get(j).y + d.getIntrinsicHeight() / 2) * alto, (trayectoria.get(j + 1).x + d.getIntrinsicWidth() / 2) * ancho, (trayectoria.get(j + 1).y + d.getIntrinsicHeight() / 2) * alto, paint);
                        }
                        if (tSize > 0) {
                            dibujarFlecha(canvas, (trayectoria.get(tSize - 1).x + d.getIntrinsicWidth() / 2) * ancho, (trayectoria.get(tSize - 1).y + d.getIntrinsicHeight() / 2) * alto, (trayectoria.get(tSize).x + d.getIntrinsicWidth() / 2) * ancho, (trayectoria.get(tSize).y + d.getIntrinsicHeight() / 2) * alto, 6, 6, paint);
                        }

                        paint.setColor(Color.BLUE);
                    }
                    //TODO DIBUJAR FLECHA

                    if (vacas.get(i).getX() != vacasModificadas.get(i).getX() || vacas.get(i).getY() != vacasModificadas.get(i).getY()) {
                        oldsVacas.put(i, vacas.put(i, vacasModificadas.get(i)));
                    }

                    if (oldsVacas.get(i) != null) {//CONDICION AGREGADA PARA ARREGLAR ERROR DE IR AL FINAL Y MOVER ANTERIOR
                        System.out.println("Vaca modificada: " + i + " - (X: " + oldsVacas.get(i).getX() + ", " + oldsVacas.get(i).getY() + ")"
                                + " -> (X: " + vacasModificadas.get(i).getX() + ", " + vacasModificadas.get(i).getY() + ")");
                        //canvas.drawCircle((oldsVacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (oldsVacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, 4, paint);
                        //canvas.drawLine((oldsVacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (oldsVacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, (vacasModificadas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacasModificadas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, paint);
                        dibujarFlecha(canvas, (oldsVacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (oldsVacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, (vacasModificadas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacasModificadas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, 6, 6, paint);
                    }
                } else {
                    d.setBounds((int) (vacas.get(i).getX() * ancho), (int) (vacas.get(i).getY() * alto), (int)((vacas.get(i).getX() + d.getIntrinsicWidth()) * ancho), (int) ((vacas.get(i).getY() + d.getIntrinsicHeight()) * alto));
                    d.draw(canvas);
                    if (drawIntervalo && vacasSeleccionadas.contains(i)){
                        paint.setColor(Color.YELLOW);
                        canvas.drawCircle((vacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, d.getIntrinsicWidth() / 2, paint);
                        paint.setColor(Color.BLUE);
                    } else if (drawEvento) {
                        if (vacasIn.contains(i)){
                            paint.setColor(Color.GREEN);
                            canvas.drawCircle((vacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, d.getIntrinsicWidth() / 2, paint);
                            paint.setColor(Color.BLUE);
                        } else if (vacasOut.contains(i)) {
                            paint.setColor(Color.RED);
                            canvas.drawCircle((vacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, d.getIntrinsicWidth() / 2, paint);
                            paint.setColor(Color.BLUE);
                        }
                    } else if (drawTrayectoria && vacas.containsKey(vacaSelected)) {
                        paint.setColor(Color.rgb(255, 127, 0));
                        canvas.drawCircle((vacas.get(vacaSelected).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacas.get(vacaSelected).getY() + d.getIntrinsicHeight() / 2) * alto, d.getIntrinsicWidth() / 2, paint);
                        int tSize = trayectoria.size() - 2;
                        for (int j = 0; j < tSize; j++){
                            canvas.drawLine((trayectoria.get(j).x + d.getIntrinsicWidth() / 2) * ancho, (trayectoria.get(j).y + d.getIntrinsicHeight() / 2) * alto, (trayectoria.get(j + 1).x + d.getIntrinsicWidth() / 2) * ancho, (trayectoria.get(j + 1).y + d.getIntrinsicHeight() / 2) * alto, paint);
                        }
                        if (tSize > 0) {
                            dibujarFlecha(canvas, (trayectoria.get(tSize - 1).x + d.getIntrinsicWidth() / 2) * ancho, (trayectoria.get(tSize - 1).y + d.getIntrinsicHeight() / 2) * alto, (trayectoria.get(tSize).x + d.getIntrinsicWidth() / 2) * ancho, (trayectoria.get(tSize).y + d.getIntrinsicHeight() / 2) * alto, 6, 6, paint);
                        }

                        paint.setColor(Color.BLUE);
                    }
                }
            }
        }
        return;
    }

    private void dibujarFlecha(Canvas g, float x1, float y1, float x2, float y2, float d, float h, Paint p) {
        /**
         * Draw an arrow line between two points.
         *
         * @param g the graphics component.
         * @param x1 x-position of first point.
         * @param y1 y-position of first point.
         * @param x2 x-position of second point.
         * @param y2 y-position of second point.
         * @param d the width of the arrow.
         * @param h the height of the arrow.
         */
        float dx = x2 - x1, dy = y2 - y1;
        float D = (float)Math.sqrt(dx * dx + dy * dy);
        float xm = D - d, xn = xm, ym = h, yn = -h, x;
        float sin = dy / D, cos = dx / D;

        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        /*float[] xpoints = {x2, (int) xm, (int) xn};
        float[] ypoints = {y2, (int) ym, (int) yn};*/

        g.drawLine(x1, y1, x2, y2, p);

        Path wallpath = new Path();
        wallpath.reset(); // only needed when reusing this path for a new build
        wallpath.moveTo(xm, ym); // used for first point
        wallpath.lineTo(x2, y2);
        wallpath.lineTo(xn, yn); // there is a setLastPoint action but i found it not to work as expected

        g.drawPath(wallpath, p);
    }

    protected Rect getRegion(Point p, int ancho) {
        ancho = ancho / 2;
        return new Rect(p.x - ancho / 2, p.y - ancho / 2, p.x + ancho, p.y + ancho);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent (MotionEvent me){
        x = me.getX();
        y = me.getY();

        if (me.getAction() == MotionEvent.ACTION_DOWN){
            accion = "down";
        }
        if (me.getAction() == MotionEvent.ACTION_MOVE){
            accion = "move";
        }
        if (me.getAction() == MotionEvent.ACTION_UP){
            if (!(drawEvento || drawIntervalo)){
                accion = "select";
            } else if ((x < xDown) && (y < yDown)) {
                stop(x / ancho, y / alto, xDown / ancho, yDown / alto);
            } else if (x < xDown) {
                stop(x / ancho, yDown / alto, xDown / ancho, y / alto);
            } else if (y < yDown) {
                stop(xDown / ancho, y / alto, x / ancho, yDown / alto);
            } else {
                stop(xDown / ancho, yDown / alto, x / ancho, y / alto);
            }

        }

        invalidate();
        return true;
    }

    /*public void draw (boolean algo){
        dibujarCirculo = algo;
        invalidate();
    }*/

    public void drawVacas(boolean dibujar) {
        dibujarVacas = dibujar;
        this.invalidate(); //PARA REDIBUJAR
    }

    public void drawVacasInicio(boolean dibujar) {
        dibujarVacas = dibujar;
        this.vacasModificadas.clear();
        this.invalidate(); //PARA REDIBUJAR
    }

    public void setVacas(Map<Integer, Vaca> vacas) {
        this.vacas = vacas;
    }

    /*public void addVaca(Integer id, Vaca v){
        this.vacas.put(id, v);
    }*/

    public void setVacasModificadas (Map<Integer, Vaca> vacas) {
        this.vacasModificadas = vacas;
    }

    private Eventos mOnStopTrackEventListener;

    public void setOnStopTrackEventListener(Eventos eventListener)
    {
        mOnStopTrackEventListener = eventListener;
    }

    public void stop(float x, float y, float xDown, float yDown)
    {
        if(mOnStopTrackEventListener != null)
        {
            mOnStopTrackEventListener.onStopTrack(x, y, xDown, yDown);
        }

    }

    public void setVacasSeleccionadas(ArrayList<Integer> vacasID) {
        this.drawIntervalo = true;
        this.drawEvento = false;
        this.drawTrayectoria = false;
        this.vacasSeleccionadas = vacasID;
    }

    public void setVacasInOut(ArrayList<Integer> vacasIn, ArrayList<Integer> vacasOut) {
        this.drawIntervalo = false;
        this.drawEvento = true;
        this.drawTrayectoria = false;
        this.vacasIn = vacasIn;
        this.vacasOut = vacasOut;
    }

    public void setTrayectoria(int vacaID, ArrayList<Punto> trayectoria) {
        this.drawIntervalo = false;
        this.drawEvento = false;
        this.drawTrayectoria = true;
        this.vacaSelected = vacaID;
        this.trayectoria = trayectoria;
    }
}
