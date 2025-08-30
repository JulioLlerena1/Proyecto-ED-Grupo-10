package com.example.pro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiSelectSpinner extends androidx.appcompat.widget.AppCompatSpinner
        implements DialogInterface.OnMultiChoiceClickListener {

    String[] _items = null;
    boolean[] mSelection = null;
    ArrayAdapter<String> simple_adapter;

    public MultiSelectSpinner(Context context) {
        super(context);
        simple_adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        simple_adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }
    public void setSelection(int[] indices) {
        if (mSelection == null) return;
        Arrays.fill(mSelection, false);
        for (int index : indices) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            }
        }
        simple_adapter.notifyDataSetChanged();
    }

    public void setItems(List<String> items) {
        _items = items.toArray(new String[0]);
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add("Selecciona...");
        Arrays.fill(mSelection, false);
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(_items, mSelection, this);
        builder.setPositiveButton("OK", (dialog, which) -> simple_adapter.notifyDataSetChanged());
        builder.show();
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;
        } else {
            throw new IllegalArgumentException("Argumento inválido: " + which);
        }
    }

    public int[] getSelectedIndices() {
        List<Integer> selection = new ArrayList<>();
        for (int i = 0; i < mSelection.length; i++) {
            if (mSelection[i]) selection.add(i);
        }
        int[] result = new int[selection.size()];
        for (int i = 0; i < result.length; i++) result[i] = selection.get(i);
        return result;
    }
}
