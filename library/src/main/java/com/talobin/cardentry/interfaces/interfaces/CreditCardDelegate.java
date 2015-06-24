package com.talobin.cardentry.interfaces.interfaces;

import android.view.KeyEvent;
import android.widget.EditText;
import com.talobin.cardentry.models.models.Card;
import com.talobin.cardentry.views.views.BaseField;

/**
 * Created by hai on 4/20/15.
 */
public interface CreditCardDelegate {
    // When the card type is identified
    public void onCardTypeChange(Card.CardType type);

    public void onCreditCardNumberValid();

    public void onExpirationDateValid();

    // Image should flip to back for security code
    public void onSecurityCodeValid();

    public void onZipCodeValid();

    public void onBadInput(EditText field);

    public void focusOnField(BaseField field);

    public void focusOnPreviousField(BaseField field);

    public void onKeyEvent(KeyEvent event);

    public void onCardIOEvent();

    public void onInfoInvalidate();
}
