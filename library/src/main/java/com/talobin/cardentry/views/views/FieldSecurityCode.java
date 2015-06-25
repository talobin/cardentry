package com.talobin.cardentry.views.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.talobin.cardentry.models.models.Card.CardType;
import com.talobin.cardentry.utils.utils.CreditCardUtil;

/**
 * Created by hai on 4/17/15.
 */
public class FieldSecurityCode extends BaseField {

    private CardType mType;
    private String mPreviouslyAcceptedCode;

    private int mRequiredLength;

    public FieldSecurityCode(Context context) {
        super(context);
        init();
    }

    public FieldSecurityCode(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FieldSecurityCode(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        super.init();
    }

    /* TextWatcher Implementation Methods */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void afterTextChanged(Editable s) {
        removeTextChangedListener(this);
        if (isDisabled()) {
            mDelegate.onSecurityCodeValid();
            setValid(true);
        } else {
            if (mType != null) {
                final String number = s.toString();
                final int currentLength = number.length();
                //Case1: length meets requirement
                //-> accepts this code
                if (currentLength == mRequiredLength) {
                    mDelegate.onSecurityCodeValid();
                    mPreviouslyAcceptedCode = number;
                    setValid(true);
                }

                //Case2: length exceed requirement
                // -> revert back to previously-accepted code
                // if there is none, clear the field
                //TODO: undo instead of clear
                else if (currentLength > mRequiredLength) {

                    if (TextUtils.isEmpty(mPreviouslyAcceptedCode)) {
                        setValid(false);
                        setText("");
                    } else {
                        mDelegate.onSecurityCodeValid();
                        setValid(true);
                        setText(mPreviouslyAcceptedCode);
                    }
                }

                //Case3: length is too short
                //-> set Valid flag to false and does nothing else
                else {
                    setValid(false);
                }
            }
            //If type is unknown, does not allow user to enter
            else {
                setText("");
            }
            addTextChangedListener(this);
        }
    }

    public CardType getType() {
        return mType;
    }

    /**
     * Set/Change type of Card
     * Also changes mRequiredLength and
     * re-evaluates if code is valid
     */
    public void setType(CardType type) {
        mType = type;
        mRequiredLength = CreditCardUtil.securityCodeValid(type);
        if (isValid()
            && !TextUtils.isEmpty(mPreviouslyAcceptedCode)
            && mPreviouslyAcceptedCode.length() < mRequiredLength) {
            setValid(false);
        }
    }

    @Override public void disableField() {
        super.disableField();
        mDelegate.onSecurityCodeValid();
    }
}
