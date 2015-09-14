CardEntry SDK for Android
========================
provides quick and easy way to enter credit/debit cards  in mobile apps.
Android Credit Card Entry
Inspired by Square and https://github.com/dbachelder/CreditCardEntry


### Setup


##### If you use gradle, then add the following dependency from `mavenCentral()`:

```
compile 'com.talobin:card-entry:0.1.4'
```

##### If you use something other than gradle, then:

1. Edit AndroidManifest.xml. We're going to add a few additional items in here:

    ```xml
    <uses-sdk android:minSdkVersion="11" />
    ```

2. Within the `<application>` element, add activity entries:

    ```xml
    <!-- Activities responsible for gathering payment info -->
    <activity android:name="com.talobin.cardentry.ui.CardEntryActivity"
                  android:theme="@android:style/Theme.Holo.Light.Dialog.NoActionBar.MinWidth"/>
    ```
3. That's it, no extra permission is needed

##### Note: Before you build in release mode, make sure to adjust your proguard configuration by adding the following to `proguard.cnf`:

```
-keep class com.talobin.**
-keepclassmembers class com.talobin.** {
    *;
}
```

### Sample code  (See the SampleApp for an example)

First, we'll assume that you're going to launch the Entry from a button,
and that you've set the button's `onClick` handler in the layout XML via `android:onClick="onShowEntryPress"`.
Then, add the method as:

```java
public void onShowEntryPress(View v) {
     Intent scanIntent = new Intent(this, CardEntryActivity.class);
     
     // customize these values to suit your needs.
     scanIntent.putExtra(CardEntryActivity.EXTRA_REQUIRE_EXPIRY,
     true); // default: false
     scanIntent.putExtra(CardEntryActivity.EXTRA_REQUIRE_CVV,
     true); // default: false
     scanIntent.putExtra(CardEntryActivity.EXTRA_REQUIRE_POSTAL_CODE,
     false); // default: true

     // MY_ENTRY_REQUEST_CODE is arbitrary and is only used within this activity.
     startActivityForResult(scanIntent,
     MY_ENTRY_REQUEST_CODE);
}
```

Next, we'll override `onActivityResult()` to get the info that user has entered.
We assume that you have setup serveral `TextView`s to display the result.

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == MY_ENTRY_REQUEST_CODE) {
        if (resultCode == CardEntryActivity.RESULT_OK) {
                Card enterdCard = data.getParcelableExtra(CardEntryActivity.EXTRA_CARD_INFO);
                if (enterdCard != null) {
                    final TextView cardNumber = (TextView) findViewById(R.id.Txt_card_number);
                    cardNumber.setText(getResources().getString(R.string.card_number_result)
                                           + enterdCard.getCardNumber());
                    final TextView cardExp = (TextView) findViewById(R.id.Txt_card_expiry);
                    cardExp.setText(getResources().getString(R.string.expiry_result)
                                        + enterdCard.getExpirationMonth()
                                        + "/"
                                        + enterdCard.getExpirationYear());
                    final TextView cardCVV = (TextView) findViewById(R.id.Txt_card_cvv);
                    cardCVV.setText(getResources().getString(R.string.cvv_result)
                                        + enterdCard.getSecurityCode());
                    final TextView cardZip = (TextView) findViewById(R.id.Txt_card_zip);
                    cardZip.setText(getResources().getString(R.string.zip_result)
                                        + enterdCard.getZipCode());
                }
            }
    // else handle other activity results
}
```

