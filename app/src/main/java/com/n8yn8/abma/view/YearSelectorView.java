package com.n8yn8.abma.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.AppDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nate on 3/24/17.
 */

public class YearSelectorView extends LinearLayout {

    Spinner spinner;

    public YearSelectorView(Context context) {
        super(context);
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

        AppDatabase db = AppDatabase.getInstance(getContext().getApplicationContext());
        List<Integer> namesInt = db.yearDao().getAllYearNames();
        List<String> names = new ArrayList<>(namesInt.size());
        for (Integer nameInt : namesInt) {
            names.add(String.valueOf(nameInt));
        }
        final ArrayAdapter<String> adp = new ArrayAdapter<>(getContext(),
                R.layout.item_spinner, names);
        spinner = findViewById(R.id.yearSpinner);
        spinner.setAdapter(adp);
    }

    public String getSelectedYear() {
        return (String) spinner.getSelectedItem();
    }
}
