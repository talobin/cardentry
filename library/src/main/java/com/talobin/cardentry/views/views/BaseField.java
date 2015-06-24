package com.talobin.cardentry.views.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import com.talobin.cardentry.interfaces.interfaces.CreditCardDelegate;

/**
 * Created by hai on 4/17/15.
 */
public abstract class BaseField extends EditText
    implements TextWatcher, OnKeyListener, View.OnFocusChangeListener {

    protected CreditCardDelegate mDelegate;

    protected Context mContext;

    private boolean mValid = false;
    private boolean mIsDisabled = false;

    public BaseField(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public BaseField(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public BaseField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public void init() {
        setInputType(InputType.TYPE_NULL);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            setRawInputType(InputType.TYPE_CLASS_TEXT);
            setTextIsSelectable(true);
        }
        addTextChangedListener(this);
        setOnKeyListener(this);
        setOnFocusChangeListener(this);
    }

    /**
     * Switch to previous field if text gets cleared out
     * Also centers the text, but at the sametime
     * cursor is always to the left
     */
    public void onTextChanged(CharSequence s, int start, int before, int end) {
        //mDelegate.onInfoInvalidate();
        if (start == 0 && before == 1 && s.length() == 0) {
            if (mDelegate != null) {
                mDelegate.focusOnPreviousField(this);
            }
            setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        } else {
            setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
            if (before > 0) {
                mDelegate.onInfoInvalidate();
            }
        }
    }

    //@Override public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    //    outAttrs.actionLabel = null;
    //    outAttrs.inputType = InputType.TYPE_NULL;
    //    outAttrs.imeOptions = EditorInfo.IME_ACTION_NONE;
    //    if (android.os.Build.VERSION.SDK_INT >= 11) {
    //        setRawInputType(InputType.TYPE_CLASS_TEXT);
    //        setTextIsSelectable(true);
    //    }
    //    //return new BackInputConnection(super.onCreateInputConnection(outAttrs), false);
    //}

    @Override public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            //Log.d("CRAP", "FOCUS CHANGE IN BASE" + hasFocus);
            if (!isDisabled()) {
                getDelegate().focusOnField(this);
            }
        }
    }

    public void backInput() {
        if (this.getText().toString().length() == 0) {
            if (mDelegate != null) {
                mDelegate.focusOnPreviousField(this);
            }
        }
    }

    @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) return false;
        if (keyCode == KeyEvent.KEYCODE_ALT_LEFT
            || keyCode == KeyEvent.KEYCODE_ALT_RIGHT
            || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
            || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
            return false;
        }

        if (keyCode == KeyEvent.KEYCODE_DEL && this.getText().toString().length() == 0) {
            if (mDelegate != null) {
                mDelegate.focusOnPreviousField(this);
            }
        }
        return false;
    }

    public CreditCardDelegate getDelegate() {
        return mDelegate;
    }

    public void setIsDisabled(boolean mIsDisabled) {
        this.mIsDisabled = mIsDisabled;
    }

    public void setDelegate(CreditCardDelegate delegate) {
        this.mDelegate = delegate;
    }

    public boolean isDisabled() {
        return mIsDisabled;
    }

    public boolean isValid() {
        return mValid;
    }

    public void setValid(boolean valid) {
        this.mValid = valid;
    }

    public void disableField() {
        setIsDisabled(true);
        setValid(true);
        setVisibility(GONE);
    }

    @SuppressLint("InlinedApi") private class BackInputConnection extends InputConnectionWrapper {

        public BackInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                BaseField.this.backInput();
                // Un-comment if you wish to cancel the backspace:
                // return false;
            }
            return super.sendKeyEvent(event);
        }

        // From Android 4.1 this is called when the DEL key is pressed on the
        // soft keyboard (and
        // sendKeyEvent() is not called). We convert this to a "normal" key
        // event.
        @SuppressLint("InlinedApi") @Override public boolean deleteSurroundingText(int beforeLength,
                                                                                   int afterLength) {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;

            if (currentapiVersion < 11) {
                return super.deleteSurroundingText(beforeLength, afterLength);
            } else {

                long eventTime = SystemClock.uptimeMillis();
                sendKeyEvent(new KeyEvent(eventTime,
                                          eventTime,
                                          KeyEvent.ACTION_DOWN,
                                          KeyEvent.KEYCODE_DEL,
                                          0,
                                          0,
                                          KeyCharacterMap.VIRTUAL_KEYBOARD,
                                          0,
                                          KeyEvent.FLAG_SOFT_KEYBOARD
                                              | KeyEvent.FLAG_KEEP_TOUCH_MODE
                                              | KeyEvent.FLAG_EDITOR_ACTION));
                sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(),
                                          eventTime,
                                          KeyEvent.ACTION_UP,
                                          KeyEvent.KEYCODE_DEL,
                                          0,
                                          0,
                                          KeyCharacterMap.VIRTUAL_KEYBOARD,
                                          0,
                                          KeyEvent.FLAG_SOFT_KEYBOARD
                                              | KeyEvent.FLAG_KEEP_TOUCH_MODE
                                              | KeyEvent.FLAG_EDITOR_ACTION));
                return true;
            }
        }
    }
}
