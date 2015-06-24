package com.haivo.cardentry.views;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import com.haivo.cardentry.utils.CreditCardUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FieldExpDate extends BaseField implements View.OnFocusChangeListener {

    final int IGNORE_SELECTION_SET_VALUE = -1;
    String mPreviousString;
    Date mCurrentDate;
    int mPreviousLength;
    int mPreviousSelection;

    public FieldExpDate(Context context) {
        super(context);
        init();
    }

    public FieldExpDate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldExpDate(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        super.init();
        mCurrentDate = new Date();
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        setOnFocusChangeListener(this);
    }

    /* TextWatcher Implementation Methods */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Log.d("CRAP", "B4 :" + s.toString() + "|" + start + "|" + count + "|" + after);
        final String currentString = s.toString();
        if (currentString.equals(mPreviousString)) {
            mPreviousSelection = start;
        } else {
            mPreviousString = currentString;
            mPreviousLength = mPreviousString.length();
        }
    }

    public void afterTextChanged(Editable s) {
        //Log.d("CRAP", "AFTER :" + s.toString());
        this.removeTextChangedListener(this);
        final String currentString = s.toString();
        if (!currentString.equals(mPreviousString)) {
            final int currentLength = currentString.length();
            final int seperatorPosition = currentString.indexOf(CreditCardUtil.DATE_SEPERATOR);
            formatExpirationDate(currentString, currentLength, seperatorPosition);
        }
        this.addTextChangedListener(this);
    }

    public void formatExpirationDate(String currentText, int currentLength, int seperatorPosition) {
        switch (currentLength) {
            case 1:
                if (seperatorPosition >= 0) {
                    signalBadInput();
                    undoLastInput();
                } else {
                    final int digit = Integer.parseInt(currentText);
                    if (digit < 2) {
                        //Does nothing
                    } else {
                        setDisplayText("0" + currentText + CreditCardUtil.DATE_SEPERATOR,
                                       currentLength + 2);
                    }
                }
                break;
            case 2:
                if (mPreviousLength > currentLength) {
                    setDisplayText(currentText.substring(0, 1), 1);
                } else if (seperatorPosition >= 0) {
                    signalBadInput();
                    undoLastInput();
                } else {
                    int month = Integer.parseInt(currentText);
                    if (month > 12 || month < 1) {
                        // Invalid digit
                        signalBadInput();
                        undoLastInput();
                    } else {
                        setDisplayText(currentText + CreditCardUtil.DATE_SEPERATOR,
                                       currentLength + 1);
                    }
                }
                break;
            case 3:
                if (seperatorPosition >= 0) {
                    return;
                } else {
                    setDisplayText(currentText.substring(0, 2)
                                       + CreditCardUtil.DATE_SEPERATOR
                                       + currentText.substring(2, 3), currentLength + 1);
                }
                break;
            case 4:
                switch (seperatorPosition) {
                    case -1:
                        signalBadInput();
                        undoLastInput();
                        break;
                    case 0:
                        signalBadInput();
                        undoLastInput();
                        break;
                    case 1:
                        final String processedString = "01" + currentText.substring(1, 4);

                        if (isDataValid(processedString)) {
                            return;
                        } else {
                            signalBadInput();
                            undoLastInput();
                        }
                        break;
                    case 2:
                        return;
                    default:
                        signalBadInput();
                        undoLastInput();
                }
                break;

            case 5:
                switch (seperatorPosition) {
                    case 1:
                        final String trimString = currentText.substring(0, currentLength - 1);
                        final String processedString = "0" + trimString;
                        if (isDataValid(processedString)) {
                            setDisplayText(processedString, currentLength - 1);
                            setValid(true);
                            mDelegate.onExpirationDateValid();
                        } else {
                            signalBadInput();
                            undoLastInput();
                        }
                        break;
                    case 2:
                        if (isDataValid(currentText)) {
                            mDelegate.onExpirationDateValid();
                            setValid(true);
                        } else {
                            signalBadInput();
                            undoLastInput();
                        }
                        break;
                    default:
                        signalBadInput();
                        undoLastInput();
                }
                break;
            default:
                if (currentLength > 5) {
                    if (isDataValid(mPreviousString)) {
                        mDelegate.onExpirationDateValid();
                        setValid(true);
                    } else {
                        signalBadInput();
                    }
                    undoLastInput();
                } else {
                    return;
                }
        }
    }

    private void undoLastInput() {
        setText(mPreviousString);
        setSelection(mPreviousSelection);
    }

    private void setDisplayText(String text, int selection) {
        mPreviousString = text;
        mPreviousLength = mPreviousString.length();
        setText(mPreviousString);
        if (selection != IGNORE_SELECTION_SET_VALUE) {
            setSelection(selection);
        }
    }

    private void signalBadInput() {
        mDelegate.onBadInput(this);
        setValid(false);
    }

    private boolean isDataValid(String string) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
        simpleDateFormat.setLenient(false);
        Date expiry = null;
        try {
            expiry = simpleDateFormat.parse(string);
            if (expiry.before(mCurrentDate)) {
                // Invalid exp date
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    @Override public void disableField() {
        super.disableField();
        mDelegate.onExpirationDateValid();
    }

    /**
     * Check if the data is valid before user wants to leave
     */
    @Override public void onFocusChange(View v, boolean hasFocus) {
        //Log.d("CRAP", "FOCUS CHANGE" + hasFocus);
        if (!hasFocus) {
            final String currentString = getText().toString();
            final int currentLength = currentString.length();
            switch (currentLength) {
                case 0:
                    return;
                case 6:
                    return;
                case 5:

                    if (!isDataValid(currentString)) {
                        signalBadInput();
                    }
                    break;
                case 4:
                    final int separatorPosition =
                        currentString.indexOf(CreditCardUtil.DATE_SEPERATOR);
                    switch (separatorPosition) {
                        case -1:
                            String processedString = currentString.substring(0, 2)
                                + CreditCardUtil.DATE_SEPERATOR
                                + currentString.substring(2, 4);
                            if (isDataValid(processedString)) {
                                setText(processedString);
                            } else {
                                signalBadInput();
                            }
                            break;
                        case 1:
                            processedString = "0" + currentString;
                            if (isDataValid(processedString)) {
                                setText(processedString);
                            } else {
                                signalBadInput();
                            }
                            break;
                        default:
                            signalBadInput();
                            break;
                    }
                    break;
                default:
                    signalBadInput();
            }
        } else {
            //Log.d("CRAP", "FOCUS BECAUSE HAS FOCUS");
            getDelegate().focusOnField(this);
        }
    }
}
