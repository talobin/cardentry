package com.haivo.cardentry.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.haivo.cardentry.R;
import com.haivo.cardentry.interfaces.Callback;
import com.haivo.cardentry.models.Card;
import com.haivo.cardentry.views.CreditCardEntry;
import com.haivo.cardentry.views.CreditCardForm;

/**
 * Created by hai on 5/3/15.
 */
public class DialogUtil {

    public static AlertDialog createFormDialog(final Context context,
                                               final Card[] card,
                                               final Callback callback,
                                               final CreditCardEntry.CardIOListener cardIOListener,
                                               final boolean fillDialogFields) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater =
            (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.diaglog_credit_entry_form, null);
        final CreditCardForm form = (CreditCardForm) dialogLayout.findViewById(R.id.Form_in_dialog);
        form.setCardIOListener(cardIOListener);
        if (fillDialogFields) form.setCardEntry(card[0]);
        builder.setView(dialogLayout);
        final AlertDialog dialog = builder.create();
        TextView negativeButton =
            (TextView) dialogLayout.findViewById(R.id.TextView_negative_button);
        TextView positiveButton =
            (TextView) dialogLayout.findViewById(R.id.TextView_positive_button);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.dismiss();
            }
        });
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (form.isCreditCardValid()) {
                    //Toast.makeText(context, "TEST", Toast.LENGTH_SHORT).show();
                    card[0] = form.getCreditCard();
                    callback.onSuccess();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        return dialog;
    }

    public static AlertDialog createFilledFormDialog(final Context context,
                                                     final Card[] card,
                                                     final Callback callback,
                                                     final CreditCardEntry.CardIOListener cardIOListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater =
            (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.diaglog_credit_entry_form, null);
        final CreditCardForm form = (CreditCardForm) dialogLayout.findViewById(R.id.Form_in_dialog);
        form.setCardIOListener(cardIOListener);
        builder.setView(dialogLayout);
        final AlertDialog dialog = builder.create();
        TextView negativeButton =
            (TextView) dialogLayout.findViewById(R.id.TextView_negative_button);
        TextView positiveButton =
            (TextView) dialogLayout.findViewById(R.id.TextView_positive_button);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.dismiss();
            }
        });
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (form.isCreditCardValid()) {
                    //Toast.makeText(context, "TEST", Toast.LENGTH_SHORT).show();
                    card[0] = form.getCreditCard();
                    callback.onSuccess();
                    dialog.dismiss();
                }
            }
        });
        form.setCardEntry(card[0]);
        dialog.show();
        return dialog;
    }
}
