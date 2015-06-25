# CardEntry
Android Credit Card Entry
Inspired by Square and https://github.com/dbachelder/CreditCardEntry

Usage:
- Include in your project : 
          ```dependencies {

        ...

        compile 'com.talobin:card-entry:0.1.2'
          }```
- Start the activity:
       Intent scanIntent = new Intent(YourActivity.this,
                                                                                CardEntryActivity.class);

                                                 // customize these values to suit your needs.
                                                 scanIntent.putExtra(CardEntryActivity.EXTRA_REQUIRE_EXPIRY,
                                                                     true); // default: false
                                                 scanIntent.putExtra(CardEntryActivity.EXTRA_REQUIRE_CVV,
                                                                     true); // default: false
                                                 scanIntent.putExtra(CardEntryActivity.EXTRA_REQUIRE_POSTAL_CODE,
                                                                     false); // default: false

                                                 // MY_ENTRY_REQUEST_CODE is arbitrary and is only used within this activity.
                                                 startActivityForResult(scanIntent,
                                                                        MY_ENTRY_REQUEST_CODE);
- Get the result:
       @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_SCAN_REQUEST_CODE) {
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
        }
       }
