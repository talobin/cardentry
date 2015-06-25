package com.talobin.cardentry.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.talobin.cardentry.R;
import com.talobin.cardentry.interfaces.CreditCardDelegate;

/**
 * Created by hai on 4/30/15.
 */
public class KeyPadView extends RelativeLayout {
    private CreditCardDelegate mDelegate;
    private TextView m1Key;
    private TextView m2Key;
    private TextView m3Key;
    private TextView m4Key;
    private TextView m5Key;
    private TextView m6Key;
    private TextView m7Key;
    private TextView m8Key;
    private TextView m9Key;
    private TextView m0Key;
    private ImageView mDeleteKey;
    private ImageView mCameraKey;
    private Context mContext;
    private boolean mDoneDrawing = false;

    public KeyPadView(Context context) {
        super(context);
        init(context);
    }

    public KeyPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyPadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_keypad, this, true);
        m1Key = (TextView) view.findViewById(R.id.key_1);
        m2Key = (TextView) view.findViewById(R.id.key_2);
        m3Key = (TextView) view.findViewById(R.id.key_3);
        m4Key = (TextView) view.findViewById(R.id.key_4);
        m5Key = (TextView) view.findViewById(R.id.key_5);
        m6Key = (TextView) view.findViewById(R.id.key_6);
        m7Key = (TextView) view.findViewById(R.id.key_7);
        m8Key = (TextView) view.findViewById(R.id.key_8);
        m9Key = (TextView) view.findViewById(R.id.key_9);
        m0Key = (TextView) view.findViewById(R.id.key_0);
        mDeleteKey = (ImageView) view.findViewById(R.id.key_delete);
        mCameraKey = (ImageView) view.findViewById(R.id.key_scan);
        m1Key.setOnClickListener(onClickListener());
        m2Key.setOnClickListener(onClickListener());
        m3Key.setOnClickListener(onClickListener());
        m4Key.setOnClickListener(onClickListener());
        m5Key.setOnClickListener(onClickListener());
        m6Key.setOnClickListener(onClickListener());
        m7Key.setOnClickListener(onClickListener());
        m8Key.setOnClickListener(onClickListener());
        m9Key.setOnClickListener(onClickListener());
        m0Key.setOnClickListener(onClickListener());
        mDeleteKey.setOnClickListener(onClickListener());
        mCameraKey.setOnClickListener(onClickListener());

        m1Key.setOnTouchListener(onTouchListener());
        m2Key.setOnTouchListener(onTouchListener());
        m3Key.setOnTouchListener(onTouchListener());
        m4Key.setOnTouchListener(onTouchListener());
        m5Key.setOnTouchListener(onTouchListener());
        m6Key.setOnTouchListener(onTouchListener());
        m7Key.setOnTouchListener(onTouchListener());
        m8Key.setOnTouchListener(onTouchListener());
        m9Key.setOnTouchListener(onTouchListener());
        m0Key.setOnTouchListener(onTouchListener());
        mDeleteKey.setOnTouchListener(onTouchListener());
        mCameraKey.setOnTouchListener(onTouchListener());
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        drawViews();
    }

    private OnTouchListener onTouchListener() {
        return new OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        v.setScaleY(1f);
                        v.setScaleX(1f);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.setScaleY(2f);
                        v.setScaleX(2f);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        v.setScaleY(2f);
                        v.setScaleX(2f);
                        break;
                    default:
                        v.setScaleY(1f);
                        v.setScaleX(1f);
                }
                return false;
            }
        };
    }

    private OnClickListener onClickListener() {
        return new OnClickListener() {
            @Override public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.key_1) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_1));
                } else if (id == R.id.key_2) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_2));
                } else if (id == R.id.key_3) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_3));
                } else if (id == R.id.key_4) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_4));
                } else if (id == R.id.key_5) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_5));
                } else if (id == R.id.key_6) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_6));
                } else if (id == R.id.key_7) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_7));
                } else if (id == R.id.key_8) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_8));
                } else if (id == R.id.key_9) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_9));
                } else if (id == R.id.key_0) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0));
                } else if (id == R.id.key_delete) {
                    mDelegate.onKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else if (id == R.id.key_scan) {
                    mDelegate.onCardIOEvent();
                    //final Card[] mCard = new Card[1];
                    //DialogUtil.createFormDialog(mContext, mCard, new Callback() {
                    //    @Override public void onSuccess() {
                    //        Toast.makeText(mContext, mCard[0].getCardNumber(), Toast.LENGTH_SHORT).show();
                    //    }
                    //
                    //    @Override public void onError() {
                    //        Toast.makeText(mContext, "GOOD BYE", Toast.LENGTH_SHORT);
                    //    }
                    //});
                }
            }
        };
    }

    public CreditCardDelegate getDelegate() {
        return mDelegate;
    }

    public void setDelegate(CreditCardDelegate delegate) {
        this.mDelegate = delegate;
    }

    public int getTextHeight() {
        m1Key.measure(0, 0);
        return m1Key.getMeasuredHeight();
    }

    private void drawViews() {

        if (!mDoneDrawing) {
            final float textWidth = m1Key.getPaint().measureText("xx");
            if (textWidth > 0) {
                int currentPadding = mDeleteKey.getPaddingRight();
                final float currentImageWidth =
                    mDeleteKey.getWidth() - currentPadding - currentPadding;
                final float difference = currentImageWidth - textWidth;
                if (difference != 0) {
                    mDoneDrawing = true;
                    currentPadding += difference / 2;
                    mDeleteKey.setPadding(currentPadding, 0, currentPadding, 0);
                    mDeleteKey.requestLayout();
                }
            }
        }
    }
}

