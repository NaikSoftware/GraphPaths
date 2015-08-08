package ua.naiksoftware.graphpaths;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import ua.naiksoftware.graphpaths.module.Cell;
import ua.naiksoftware.graphpaths.module.Deikstra;
import ua.naiksoftware.graphpaths.module.JDD;
import ua.naiksoftware.graphpaths.module.SalesmanProblem;
import ua.naiksoftware.graphpaths.module.Shimbell;
import ua.naiksoftware.view.HorizontalNumsLine;
import ua.naiksoftware.view.SyncHorizontalScroll;
import ua.naiksoftware.view.VerticalNumsLine;
import ua.naiksoftware.widget.CustomSpinnerAdapter;

/**
 * Screen for input 2D matrix
 *
 * @author Naik
 */
public class MainActivity extends Activity {

    private static final String tag = "MainActivity";
    public static TableLayout table;//таблица для ввода матрицы
    private VerticalNumsLine verNumsLine;
    private HorizontalNumsLine horNumsLine;
    private SyncHorizontalScroll syncHorizontalScroll;
    private TextView sizeTextView;
    private static final int DEFAULT_MATRIX_SIZE = 3;
    public static final int MAX_MATRIX_SIZE = 30;
    public static final int CELL_SIZE;//pixel
    private int size = DEFAULT_MATRIX_SIZE;

    static {
        TextPaint p = new TextPaint();
        p.setTextSize(30f);
        Rect r = new Rect();
        p.getTextBounds("88888", 0, 5, r);
        CELL_SIZE = r.width();
    }

    private enum Mode {

        DEIKSTRA, SHIMBELL, SALESMAN_PROBLEM, JDD
    }

    private AlertDialog deikstraDialog, canvasDialog;
    private CanvasView canvasView;
    private View canvasLayout, deikstraDialogLayout;
    private Spinner fromSpinner, toSpinner;
    private Spinner methodsSpinner;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Initialize components
        canvasLayout = LayoutInflater.from(this).inflate(R.layout.canvas, null);
        canvasView = (CanvasView) canvasLayout.findViewById(R.id.canvasView);
        deikstraDialogLayout = LayoutInflater.from(this).inflate(R.layout.deikstra_dialog, null);
        fromSpinner = (Spinner) deikstraDialogLayout.findViewById(R.id.spinner_from);
        toSpinner = (Spinner) deikstraDialogLayout.findViewById(R.id.spinner_to);
        sizeTextView = (TextView) findViewById(R.id.sizeTextView);
        sizeTextView.setText(String.valueOf(size));
        table = (TableLayout) findViewById(R.id.matrix_table);
        verNumsLine = (VerticalNumsLine) findViewById(R.id.vert_nums);
        horNumsLine = (HorizontalNumsLine) findViewById(R.id.horiz_nums);
        syncHorizontalScroll = (SyncHorizontalScroll) findViewById(R.id.syncHorizontalScroll);
        syncHorizontalScroll.addSync(horNumsLine);

        constructTable();
        table.post(new Runnable() {
            public void run() {
                int[] loc1 = new int[2], loc2 = new int[2];
                ((TableRow) table.getChildAt(0)).getChildAt(0).getLocationOnScreen(loc1);
                ((TableRow) table.getChildAt(0)).getChildAt(1).getLocationOnScreen(loc2);
                horNumsLine.setInterval(Math.abs(loc2[0] - loc1[0]));
                ((TableRow) table.getChildAt(0)).getChildAt(0).getLocationOnScreen(loc1);
                ((TableRow) table.getChildAt(1)).getChildAt(0).getLocationOnScreen(loc2);
                verNumsLine.setInterval(Math.abs(loc2[1] - loc1[1]));
            }
        });
        horNumsLine.setNums(size);
        verNumsLine.setNums(size);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(canvasLayout);
        builder.setNeutralButton("Thanks", null);
        canvasDialog = builder.create();

        methodsSpinner = (Spinner) findViewById(R.id.methods_spinner);
    }

    /**
     * Called from xml
     */
    public void clickCalc(View v) {
        switch (methodsSpinner.getSelectedItemPosition()) {
            case 0:
                calcDeikstra();
                break;
            case 1:
                calcShimbell();
                break;
            case 2:
                calcBrunchesAndLimits();
                break;
            case 3:
                calcJDD();
                break;
        }
    }

    private void calcJDD() {
        JDD jdd = new JDD(canvasView);
        jdd.resolve(getMatrix(Mode.JDD), 0, 0);
        canvasDialog.show();
    }

    private void calcDeikstra() {
        LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView fromTV = new TextView(this), toTV = new TextView(this);
        fromTV.setLayoutParams(params);
        toTV.setLayoutParams(params);
        fromTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        toTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        fromTV.setText("From");
        toTV.setText("To");
        String[] data = new String[size];
        for (int i = 1; i <= size; data[i - 1] = String.valueOf(i), i++) ;
        CustomSpinnerAdapter fromAdapter = new CustomSpinnerAdapter(data, this, fromTV);
        fromSpinner.setAdapter(fromAdapter);
        fromSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> av, View view, int pos, long id) {
                fromTV.setText("From " + (pos + 1));
            }

            public void onNothingSelected(AdapterView<?> av) {
            }
        });
        CustomSpinnerAdapter toAdapter = new CustomSpinnerAdapter(data, this, toTV);
        toSpinner.setAdapter(toAdapter);
        toSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> av, View view, int pos, long id) {
                toTV.setText("To " + (pos + 1));
            }

            public void onNothingSelected(AdapterView<?> av) {
            }
        });
        if (deikstraDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(deikstraDialogLayout);
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface di, int i) {
                    int from = fromSpinner.getSelectedItemPosition() + 1;
                    int to = toSpinner.getSelectedItemPosition() + 1;
                    if (from == to) {
                        Toast.makeText(MainActivity.this, "What? Path from " + from + " to " + to + "???", Toast.LENGTH_LONG).show();
                    } else {
                        Deikstra deikstra = new Deikstra(canvasView);
                        deikstra.resolve(getMatrix(Mode.DEIKSTRA), from, to);
                        canvasDialog.show();
                    }
                }
            });
            deikstraDialog = builder.create();
        }
        deikstraDialog.show();
    }

    private void calcShimbell() {
        Shimbell shimbell = new Shimbell(canvasView);
        shimbell.resolve(getMatrix(Mode.SHIMBELL), 0, 0);
        canvasDialog.show();
    }

    private void calcBrunchesAndLimits() {
        SalesmanProblem salesmanProblem = new SalesmanProblem(canvasView);
        salesmanProblem.resolve(getMatrix(Mode.SALESMAN_PROBLEM), size, size);
        canvasDialog.show();
    }

    private Cell[][] getMatrix(Mode mode) {
        Cell[][] m = new Cell[size][size];
        TableRow row;
        String val;
        for (int i = 0; i < size; i++) {
            row = (TableRow) table.getChildAt(i);
            for (int j = 0; j < size; j++) {
                m[i][j] = new Cell();
                if (i == j) {
                    switch (mode) {
                        case DEIKSTRA:
                        case SALESMAN_PROBLEM:
                        case JDD:
                            m[i][i].setInt(Integer.MAX_VALUE);
                            break;
                        case SHIMBELL:
                            m[i][i].setInt(0);
                            break;
                    }
                } else {
                    val = ((EditText) row.getChildAt(j)).getText().toString();
                    if (val.isEmpty()) {
                        m[i][j].setInt(Integer.MAX_VALUE);
                    } else {
                        m[i][j].setInt(Integer.parseInt(val));
                    }
                }
            }
        }
        return m;
    }

    public void plusSize(View v) {
        if (size == MAX_MATRIX_SIZE) {
            return;
        }
        plusCol();
        size++;
        plusRow();
        verNumsLine.setNums(size);
        horNumsLine.setNums(size);
        sizeTextView.setText(String.valueOf(size));
    }

    public void minusSize(View v) {
        if (size == 2) {
            return;
        }
        minusRow();
        size--;
        minusCol();
        verNumsLine.setNums(size);
        horNumsLine.setNums(size);
        sizeTextView.setText(String.valueOf(size));
    }

    public void minusRow() {
        table.removeViewAt(size - 1);
    }

    public void plusRow() {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(size));
        for (int j = 0; j < size; j++) {
            EditText editText = new EditText(this);
            if (j == (size - 1)) {
                editText.setText(Cell.INFINITY);
                editText.setEnabled(false);
            } else {
                editText.setHint(Cell.INFINITY);
            }
            editText.setSingleLine();
            editText.setWidth(CELL_SIZE);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            tableRow.addView(editText, j);
        }
        table.addView(tableRow, size - 1);
    }

    public void minusCol() {
        for (int j = 0; j < size; j++) {
            TableRow row = (TableRow) table.getChildAt(j);
            row.removeViewAt(size);
        }
    }

    public void plusCol() {
        for (int j = 0; j < size; j++) {
            EditText editText = new EditText(this);
            editText.setHint(Cell.INFINITY);
            editText.setSingleLine();
            editText.setWidth(CELL_SIZE);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            TableRow row = (TableRow) table.getChildAt(j);
            row.addView(editText, size);
        }
    }

    private void constructTable() {
        TableRow.LayoutParams paramsRow = new TableRow.LayoutParams(size);
        for (int i = 0; i < size; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(paramsRow);
            for (int j = 0; j < size; j++) {
                EditText editText = new EditText(this);
                if (j == i) {
                    editText.setText(Cell.INFINITY);
                    editText.setEnabled(false);
                } else {
                    editText.setHint(Cell.INFINITY);
                }
                editText.setSingleLine();
                editText.setWidth(CELL_SIZE);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                tableRow.addView(editText, j);
            }
            table.addView(tableRow, i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.about_context_item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage("Програма для обчислення графів.\n(c) NaikSoftware 2014-2015");
            builder.setNeutralButton("Ok", null);
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
