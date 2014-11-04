package ua.naiksoftware.widget;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * 
 * @author Naik
 */
public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final String[] values;
    private final Context context;
    private final View clickView;

    public CustomSpinnerAdapter(String[] values, Context context, View clickView) {
        this.values = values;
        this.context = context;
        this.clickView = clickView;
        this.clickView.setClickable(false);
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         return clickView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_dropdown_item, null);
        textView.setText(values[position]);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(10, 5, 10, 5);
        return textView;
    }
}
