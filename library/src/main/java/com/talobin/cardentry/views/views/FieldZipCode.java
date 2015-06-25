package com.talobin.cardentry.views.views;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;

/**
 * Created by hai on 4/17/15.
 */
public class FieldZipCode extends BaseField {

    public FieldZipCode(Context context) {
        super(context);
        init();
    }

    public FieldZipCode(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldZipCode(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        super.init();
        setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
    }

    /* TextWatcher Implementation Methods */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void afterTextChanged(Editable s) {
        String zipCode = s.toString();
        if (zipCode.length() == 5 || isDisabled()) {
            mDelegate.onZipCodeValid();
            setValid(true);
        } else {
            setValid(false);
        }
    }

    @Override public void disableField() {
        super.disableField();
        mDelegate.onZipCodeValid();
    }
}
