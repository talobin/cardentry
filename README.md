# CardEntry
Android Credit Card Entry
Inspired by Square and https://github.com/dbachelder/CreditCardEntry

Usage:
- Include in your project : 
     dependencies {

        ...

        compile 'com.talobin:card-entry:0.1.2'
    }
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
