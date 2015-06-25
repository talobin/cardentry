package com.talobin.cardentry.models.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.talobin.cardentry.utils.utils.CreditCardUtil;

public class Card implements Parcelable {
    public Card(Parcel source) {
        mCardNumber = source.readString();
        mExpiry = source.readString();
        mSecurityCode = source.readString();
        mZipCode = source.readString();
        mExpDate = source.readString();
        mExpYear = source.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override public int describeContents() {
        return hashCode();
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCardNumber);
        dest.writeString(mExpiry);
        dest.writeString(mSecurityCode);
        dest.writeString(mZipCode);
        dest.writeString(mExpDate);
        dest.writeString(mExpYear);
    }

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
