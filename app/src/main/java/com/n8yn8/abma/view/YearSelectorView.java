package com.n8yn8.abma.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.n8yn8.abma.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nate on 3/24/17.
 */

public class YearSelectorView extends LinearLayout {

    Spinner spinner;
    List<String> names = new ArrayList<>();

    public YearSelectorView(Context context, List<String> names) {
        super(context);
        this.names.addAll(names);
        initViews();
    }

    public YearSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public YearSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    @TargetApi(21)
    public YearSelectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews();
    }

    private void initViews() {
        inflate(getContext(), R.layout.dialog_year_selector, this);

        final ArrayAdapter<String> adp = new ArrayAdapter<>(getContext(),
                R.layout.item_spinner, names);
        spinner = findViewById(R.id.yearSpinner);
        spinner.setAdapter(adp);
    }

    public String getSelectedYear() {
        return (String) spinner.getSelectedItem();
    }
}
