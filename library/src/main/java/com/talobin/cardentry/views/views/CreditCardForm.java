package com.talobin.cardentry.views.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.talobin.cardentry.R;
import com.talobin.cardentry.interfaces.interfaces.Callback;
import com.talobin.cardentry.interfaces.interfaces.CreditCardDelegate;
import com.talobin.cardentry.models.models.Card;

/**
 * Created by hai on 4/17/15.
 */
public class CreditCardForm extends RelativeLayout {

    private CreditCardEntry mEntry;
    private KeyPadView mKeyPad;
    private TextView mPositiveButton;
    private CreditCardDelegate mDelegate;

    public CreditCardForm(Context context) {
        super(context);
        init(context);
    }

    public CreditCardForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CreditCardForm(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_form, this, true);
        ImageView frontview = (ImageView) view.findViewById(R.id.ImageView_card_front);
        ImageView backView = (ImageView) view.findViewById(R.id.ImageView_card_back);
        ImageView brandView = (ImageView) view.findViewById(R.id.ImageView_card_brand);
        mEntry = (CreditCardEntry) view.findViewById(R.id.View_card_entry);
        mEntry.setCardFrontImage(frontview);
        mEntry.setCardBackImage(backView);
        mEntry.setBrandCardImage(brandView);
        mKeyPad = (KeyPadView) view.findViewById(R.id.View_key_pad);
        mDelegate = mEntry.getDelegate();
        mKeyPad.setDelegate(mDelegate);
        mPositiveButton = (TextView) view.findViewById(R.id.TextView_positive_button);
        mEntry.setCallBack(new Callback() {
            @Override public void onSuccess() {
                mPositiveButton.setBackgroundResource(R.drawable.touch_feedback);
                mPositiveButton.setTextColor(getResources().getColor(R.color.secondary_text));
                mPositiveButton.setClickable(true);
            }

            @Override public void onError() {
                mPositiveButton.setBackgroundResource(android.R.color.transparent);
                mPositiveButton.setTextColor(getResources().getColor(R.color.lightest_gray));
                mPositiveButton.setClickable(false);
            }
        });
    }

    public boolean isCreditCardValid() {
        return mEntry.isCreditCardValid();
    }

    public Card getCreditCard() {
        return mEntry.getCreditCard();
    }

    public void setPositiveOnClickListener(OnClickListener listener) {
        mPositiveButton.setOnClickListener(listener);
    }

    public void setCardIOListener(CreditCardEntry.CardIOListener cardIOListener) {
        mEntry.setCardIOListener(cardIOListener);
    }

    public void setCardEntry(Card card) {
        mEntry.setCardInput(card);
    }
}
