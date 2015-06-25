package com.talobin.cardentry.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.talobin.cardentry.R;
import com.talobin.cardentry.models.models.Card;
import com.talobin.cardentry.views.views.CreditCardForm;
import com.talobin.cardentry.views.views.FieldExpDate;
import com.talobin.cardentry.views.views.FieldSecurityCode;
import com.talobin.cardentry.views.views.FieldZipCode;

/**
 * Created by hai on 6/23/15.
 */
public class CardEntryActivity extends Activity {
    /**
     * Boolean extra. Optional. Defaults to <code>true</code>. If
     * set to <code>false</code>, expiry information will not be required.
     */
    public static final String EXTRA_REQUIRE_EXPIRY = "com.haivo.cardentry.requireExpiry";

    /**
     * Boolean extra. Optional. Defaults to <code>true</code>. If set, the user will be prompted
     * for the card CVV.
     */
    public static final String EXTRA_REQUIRE_CVV = "com.haivo.cardentry.requireCVV";

    /**
     * Boolean extra. Optional. Defaults to <code>true</code>. If set, the user will be prompted
     * for the card billing postal code.
     */
    public static final String EXTRA_REQUIRE_POSTAL_CODE = "com.haivo.cardentry.requirePostalCode";

    /**
     * Boolean extra. Optional. Defaults to <code>false</code>. If set, the user will be prompted
     * for the card billing postal code.
     */
    public static final String EXTRA_REQUIRE_SCAN_BUTTON = "com.haivo.cardentry.requireScanButton";

    public static final String EXTRA_CARD_INFO = "com.haivo.cardentry.cardInfor";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        final Intent clientData = this.getIntent();
        boolean requireExpiry = clientData.getBooleanExtra(EXTRA_REQUIRE_EXPIRY, true);
        boolean requireCVV = clientData.getBooleanExtra(EXTRA_REQUIRE_CVV, true);
        boolean requirePostalCode = clientData.getBooleanExtra(EXTRA_REQUIRE_POSTAL_CODE, true);
        boolean requireScanButton = clientData.getBooleanExtra(EXTRA_REQUIRE_SCAN_BUTTON, false);
        if (requireScanButton) {
            final ImageView scanButton = (ImageView) findViewById(R.id.key_scan);
            scanButton.setVisibility(View.VISIBLE);
        }
        if (!requirePostalCode) {
            final FieldZipCode zipField = (FieldZipCode) findViewById(R.id.Field_zip_code);
            zipField.disableField();
        }
        if (!requireCVV) {
            final FieldSecurityCode cvvField =
                (FieldSecurityCode) findViewById(R.id.Field_security_code);
            cvvField.disableField();
        }
        if (!requireExpiry) {
            final FieldExpDate expField = (FieldExpDate) findViewById(R.id.Field_exp_date);
            expField.disableField();
        }

        final CreditCardForm form = (CreditCardForm) findViewById(R.id.Form_in_dialog);
        TextView negativeButton = (TextView) findViewById(R.id.TextView_negative_button);
        TextView positiveButton = (TextView) findViewById(R.id.TextView_positive_button);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (form.isCreditCardValid()) {
                    //Toast.makeText(context, "TEST", Toast.LENGTH_SHORT).show();
                    final Card card = form.getCreditCard();
                    final Intent resultData = new Intent();
                    resultData.putExtra(EXTRA_CARD_INFO, card);
                    setResult(Activity.RESULT_OK, resultData);
                    finish();
                }
            }
        });
    }
}
