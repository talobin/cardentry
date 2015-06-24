package com.haivo.cardentry.models;

import com.haivo.cardentry.utils.CreditCardUtil;
import org.parceler.Parcel;

@Parcel public class Card {
    public enum CardType {
        VISA, MASTERCARD, AMEX, DISCOVER, INVALID;
    }

    String mCardNumber;
    String mExpiry;
    String mSecurityCode;
    String mZipCode;
    String mExpDate;
    String mExpYear;

    public Card(String cardNumber, String expiry, String securityCode, String zipCode) {
        this.setCardNumber(cardNumber);
        this.setExpiry(expiry);
        this.setSecurityCode(securityCode);
        this.setZipCode(zipCode);
        final String[] expiryArray = expiry.split(CreditCardUtil.DATE_SEPERATOR);
        if (expiryArray.length == 2) {
            this.setExpDate(expiryArray[0]);
            this.setExpYear(expiryArray[1]);
        }
    }

    public Card() {
    }

    public void setExpDate(String date) {
        this.mExpDate = date;
    }

    public void setExpYear(String year) {
        this.mExpYear = year;
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.mCardNumber = cardNumber;
    }

    public void setExpiry(String expDate) {
        this.mExpiry = expDate;
    }

    public String getSecurityCode() {
        return mSecurityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.mSecurityCode = securityCode;
    }

    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(String zipCode) {
        this.mZipCode = zipCode;
    }

    public String getExpirationMonth() {
        return mExpDate;
    }

    public String getExpirationYear() {
        return mExpYear;
    }

    public String getExpirationYearSuffix() {
        return mExpYear.substring(mExpYear.length() - 2, mExpYear.length());
    }
}
