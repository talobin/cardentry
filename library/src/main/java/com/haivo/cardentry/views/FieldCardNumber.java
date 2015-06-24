package com.haivo.cardentry.views;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import com.haivo.cardentry.interfaces.CreditCardDelegate;
import com.haivo.cardentry.models.Card.CardType;
import com.haivo.cardentry.utils.CreditCardUtil;

/**
 * Created by hai on 4/17/15.
 */
public class FieldCardNumber extends BaseField {

    //<editor-fold desc="Class Properties">
    private CardType mType;
    private String mPreviousNumber;
    private int mPreviousSelection;
    private int mPreviousLength;
    private int mCurrentlyBeingProcessedLength;
    private ACTION mAction;

    //<editor-fold desc="Public Methods">
    public FieldCardNumber(Context context) {
        super(context);
        init();
    }
    //</editor-fold>

    public FieldCardNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldCardNumber(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int end) {
    }

    public void init() {
        super.init();
    }

    /* TextWatcher Implementation Methods */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Log.d("CRAP", "B4 " + s.toString() + "|" + start + "|" + count + "|" + after);
        switch (count) {
            case 0:
                mAction = ACTION.TYPE;
                break;
            case 1:
                mAction = ACTION.DELETE;
                break;
            default:
                mAction = ACTION.FORMAT;
        }
        if (mAction != ACTION.FORMAT) {
            mPreviousSelection = start;
            mPreviousNumber = s.toString();
            mPreviousLength = mPreviousNumber.length();
        }
    }

    public void afterTextChanged(Editable s) {
        removeTextChangedListener(this);
        //Log.d("CRAP", "HERE" + s.toString() + "|" + mPreviousLength + "|" + mPreviousSelection);
        //Log.d("CRAP", "ACTION" + mAction);
        String number = s.toString();
        int currentLength = number.length();
        if (currentLength != mPreviousLength && currentLength != mCurrentlyBeingProcessedLength) {
            switch (mAction) {
                case TYPE:
                    mCurrentlyBeingProcessedLength = currentLength;
                    handleTypingCase(currentLength, number);
                    break;
                case DELETE:
                    mCurrentlyBeingProcessedLength = currentLength;
                    handleDeletingCase(currentLength, number);
                    break;
                case FORMAT:
                    //Do nothing
                    break;
            }
        }
        addTextChangedListener(this);
    }

    public CreditCardDelegate getDelegate() {
        return mDelegate;
    }

    public void setDelegate(CreditCardDelegate delegate) {
        this.mDelegate = delegate;
    }

    public CardType getType() {
        return mType;
    }

    //<editor-fold desc="Private Methods">
    private void handleDeletingCase(int currentLength, String number) {
        final int minimumLengthToCheckForType = CreditCardUtil.CC_LEN_FOR_TYPE;
        if (currentLength >= minimumLengthToCheckForType) {
            mDelegate.onInfoInvalidate();
            //If the has just been changed or not set
            //We should update it
            if (mPreviousSelection < minimumLengthToCheckForType) {
                final CardType type = CreditCardUtil.findCardType(number);
                if (mType != type) {
                    mType = type;
                    mDelegate.onCardTypeChange(mType);
                    //Log.d("CRAP", "CHANGE 2");
                }
            } else {
                //If user deleted separator, delete digit before that as well
                if (cursorIsRightBeforeSeperator(mPreviousNumber, mPreviousSelection)) {
                    number = deleteTheDigitBeforeCursor(number, mPreviousSelection);
                    mPreviousSelection -= 1;
                }

                //Process the raw string
                final String formatted = CreditCardUtil.formatForViewing(number, mType);
                final int formattedLength = formatted.length();
                //Log.d("CRAP", "FORMATTED: " + formatted + "|");
                //Update the view
                if (!number.equalsIgnoreCase(formatted)) {
                    //Update text to the formatted one
                    mCurrentlyBeingProcessedLength = formattedLength;
                    setText(formatted);

                    //Position the cursor
                    if (mPreviousSelection == (mPreviousLength - 1)) {
                        //The cursor was at the end so we keep it there
                        setSelection(formattedLength);
                    }
                    //The cursor was at the middle so we set it back to that position
                    else {
                        setSelection(mPreviousSelection);
                    }
                }
            }
        } else {
            mType = CardType.INVALID;
            mDelegate.onCardTypeChange(mType);
            //Log.d("CRAP", "CHANGE 1" + mPreviousNumber + "|" + number);
        }
    }
    //</editor-fold>

    private String deleteTheDigitBeforeCursor(String number, int cursorPosition) {
        return number.substring(0, cursorPosition - 1) + number.substring(cursorPosition);
    }

    private void handleTypingCase(int currentLength, String number) {
        final int minimumLengthToCheckForType = CreditCardUtil.CC_LEN_FOR_TYPE;
        if (currentLength >= minimumLengthToCheckForType) {
            //If the has just been changed or not set
            //We should update it
            if (mPreviousSelection < minimumLengthToCheckForType
                || mType == null
                || mType == CardType.INVALID) {
                final CardType type = CreditCardUtil.findCardType(number);
                if (mType != type) {
                    mDelegate.onCardTypeChange(type);
                    //Log.d("CRAP", "CHANGE 3");
                }
                mType = type;
            }

            //If type is wrong, we undo the change
            //and signal bad input
            if (mType.equals(CardType.INVALID)) {
                undoLastTypingInput();
                signalBadInput();
                return;
            } else {
                //Process the raw string
                final String formatted = CreditCardUtil.formatForViewing(number, mType);
                final int formattedLength = formatted.length();
                //Log.d("CRAP", "FORMATTED: " + formatted + "|");
                //Update the view
                if (!number.equalsIgnoreCase(formatted)) {
                    //Update text to the formatted one
                    mCurrentlyBeingProcessedLength = formattedLength;
                    setText(formatted);

                    //Position the cursor
                    if (mPreviousSelection == mPreviousLength) {
                        //The cursor was at the end so we keep it there
                        setSelection(formattedLength);
                    }
                    //The cursor was at the middle so we set it back to that position
                    else if (formattedLength < currentLength) {
                        if (cursorIsRightBeforeSeperator(formatted, mPreviousSelection)) {
                            setSelection(mPreviousSelection + 2);
                        } else {
                            setSelection(mPreviousSelection + 1);
                        }
                    } else {
                        setSelection(mPreviousSelection);
                    }
                }
                if (formattedLength >= CreditCardUtil.lengthOfFormattedStringForType(mType)) {
                    if (CreditCardUtil.isValidNumber(formatted)) {
                        signalValidInput();
                    } else {
                        signalBadInput();
                        return;
                    }
                }
            }
        }
    }

    private boolean cursorIsRightBeforeSeperator(String numberString, int cursorPosition) {
        return numberString.substring(cursorPosition, cursorPosition + 1)
                           .equals(CreditCardUtil.CARD_SEPERATOR);
    }

    private void undoLastTypingInput() {
        mCurrentlyBeingProcessedLength = mPreviousLength;
        setText(mPreviousNumber);
        setSelection(mPreviousSelection);
    }

    private void signalBadInput() {
        mDelegate.onBadInput(this);
        setValid(false);
    }

    private void signalValidInput() {
        mDelegate.onCreditCardNumberValid();
        setValid(true);
    }

    private enum ACTION {
        DELETE, TYPE, FORMAT;
    }
    //</editor-fold>
}
