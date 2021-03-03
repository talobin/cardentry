package com.talobin.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.talobin.cardentry.models.Card;
import com.talobin.cardentry.ui.CardEntryActivity;

public class SampleActivity extends Activity {

    private static final int MY_ENTRY_REQUEST_CODE = 12345;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        final Button btnShowDialog = (Button) findViewById(R.id.Btn_show_dialog);
        btnShowDialog.setOnClickListener(new View.OnClickListener() {
                                             @Override public void onClick(View v) {

                                                 Intent scanIntent = new Intent(SampleActivity.this,
                                                                                CardEntryActivity.class);

                                                 // customize these values to suit your needs.
                                                 scanIntent.putExtra(CardEntryActivity.EXTRA_REQUIRE_EXPIRY,
                                                                     true); // default: false
                                                 scanIntent.putExtra(CardEntryActivity.EXTRA_REQUIRE_CVV,
                                                                     true); // default: false
                                                 scanIntent.putExtra(CardEntryActivity.EXTRA_REQUIRE_POSTAL_CODE,
                                                                     true); // default: true

                                                 // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
                                                 startActivityForResult(scanIntent,
                                                                        MY_ENTRY_REQUEST_CODE);
                                             }
                                         }

        );
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        }
    }
}
