package com.talobin.cardentry.utils;

import android.annotation.SuppressLint;
import com.talobin.cardentry.R;
import com.talobin.cardentry.models.Card.CardType;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hai on 4/17/15.
 */
@SuppressLint("SimpleDateFormat") public class CreditCardUtil {

    // See: http://www.regular-expressions.info/creditcard.html
    public static final String REGX_VISA = "^4[0-9]{15}?";// VISA 16
    public static final String REGX_MC = "^5[1-5][0-9]{14}$"; // MC 16
    public static final String REGX_AMEX = "^3[47][0-9]{13}$";// AMEX 15
    public static final String REGX_DISCOVER = "^6(?:011|5[0-9]{2})[0-9]{12}$";// Discover
    // 16
    public static final String REGX_DINERS_CLUB = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$";// DinersClub
    // 14
    // //
    // 38812345678901

    public static final int CC_LEN_FOR_TYPE = 4; // number of characters to
    // determine length

    public static final String REGX_AMEX_REG_TYPE = "^3[47][0-9]{2}$";// AMEX 15
    public static final String REGX_DINERS_CLUB_TYPE = "^3(?:0[0-5]|[68][0-9])[0-9]$";// DinersClub
    public static final String REGX_VISA_TYPE = "^4[0-9]{3}?";// VISA 16
    public static final String REGX_MC_TYPE = "^5[1-5][0-9]{2}$";// MC 16
    public static final String REGX_DISCOVER_TYPE = "^6(?:011|5[0-9]{2})$";// Discover
    // 16

    public static final String DATE_SEPERATOR = "/";
    public static final String CARD_SEPERATOR = " ";
    public static final String EXAMPLE_OVERVIEW_STRING = "55555    55/55    5555      55555";

    public static String cleanNumber(String number) {
        return number.replaceAll("\\s", "");
    }

    public static CardType findCardType(String number) {

        if (number.length() < CC_LEN_FOR_TYPE) {
            return CardType.INVALID;
        }

        String reg = null;

        for (CardType type : CardType.values()) {
            switch (type) {
                case AMEX:
                    reg = REGX_AMEX_REG_TYPE;
                    break;
                case DISCOVER:
                    reg = REGX_DISCOVER_TYPE;
                    break;
                case MASTERCARD:
                    reg = REGX_MC_TYPE;
                    break;
                case VISA:
                    reg = REGX_VISA_TYPE;
                    break;
                default:
                    break;
            }

            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(number.substring(0, CC_LEN_FOR_TYPE));

            if (matcher.matches()) {
                return type;
            }
        }

        return CardType.INVALID;
    }

    public static boolean isValidNumber(String number) {
        boolean result = false;

        String cleaned = cleanNumber(number);

        String reg = null;

        switch (findCardType(cleaned)) {
            case AMEX:
                reg = REGX_AMEX;
                break;
            case DISCOVER:
                reg = REGX_DISCOVER;
                break;
            case INVALID:
                return result;
            case MASTERCARD:
                reg = REGX_MC;
                break;
            case VISA:
                reg = REGX_VISA;
                break;
            default:
                return result;
        }

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(cleaned);

        return matcher.matches() && validateCardNumber(cleaned);
    }

    public static boolean validateCardNumber(String cardNumber) throws NumberFormatException {
        int sum = 0, digit, addend = 0;
        boolean doubled = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            digit = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (doubled) {
                addend = digit * 2;
                if (addend > 9) {
                    addend -= 9;
                }
            } else {
                addend = digit;
            }
            sum += addend;
            doubled = !doubled;
        }
        return (sum % 10) == 0;
    }

    public static String formatForViewing(String enteredNumber) {
        return formatForViewing(enteredNumber, findCardType(enteredNumber));
    }

    public static String formatForViewing(String enteredNumber, CardType type) {
        String cleaned = cleanNumber(enteredNumber);
        int len = cleaned.length();

        if (len <= CC_LEN_FOR_TYPE) return cleaned;

        // NSRange r2; r2.location = NSNotFound;
        // NSRange r3; r3.location = NSNotFound;
        // NSRange r4; r4.location = NSNotFound;
        // NSMutableArray *gaps = [NSMutableArray arrayWithObjects:@"", @"",
        // @"", nil];

        ArrayList<String> gaps = new ArrayList<String>();

        int segmentLengths[] = { 0, 0, 0 };

        switch (type) {
            case VISA:
            case MASTERCARD:
            case DISCOVER: // { 4-4-4-4}
                gaps.add(" ");
                segmentLengths[0] = 4;
                gaps.add(" ");
                segmentLengths[1] = 4;
                gaps.add(" ");
                segmentLengths[2] = 4;
                break;
            case AMEX: // {4-6-5}
                gaps.add(" ");
                segmentLengths[0] = 6;
                gaps.add(" ");
                segmentLengths[1] = 5;
                gaps.add("");
                segmentLengths[2] = 0;
                break;
            default:
                return enteredNumber;
        }

        int end = CC_LEN_FOR_TYPE;
        int start = 0;
        String segment1 = cleaned.substring(0, end);
        start = end;
        end = segmentLengths[0] + end > len ? len : segmentLengths[0] + end;
        String segment2 = cleaned.substring(start, end);
        start = end;
        end = segmentLengths[1] + end > len ? len : segmentLengths[1] + end;
        String segment3 = cleaned.substring(start, end);
        start = end;
        end = segmentLengths[2] + end > len ? len : segmentLengths[2] + end;
        String segment4 = cleaned.substring(start, end);

        String ret = String.format("%s%s%s%s%s%s%s",
                                   segment1,
                                   gaps.get(0),
                                   segment2,
                                   gaps.get(1),
                                   segment3,
                                   gaps.get(2),
                                   segment4);

        return ret.trim();
    }

    public static int lengthOfStringForType(CardType type) {
        int idx = 0;

        switch (type) {
            case VISA:
            case MASTERCARD:
            case DISCOVER: // { 4-4-4-4}
                idx = 16;
                break;
            case AMEX: // {4-6-5}
                idx = 15;
                break;
            default:
                idx = 0;
        }

        return idx;
    }

    public static int lengthOfFormattedStringForType(CardType type) {
        int idx = 0;

        switch (type) {
            case VISA:
            case MASTERCARD:
            case DISCOVER: // { 4-4-4-4}
                idx = 16 + 3;
                break;
            case AMEX: // {4-6-5}
                idx = 15 + 2;
                break;
            default:
                idx = 0;
        }

        return idx;
    }

    public static int lengthOfFormattedStringTilLastGroupForType(CardType type) {
        int idx = 0;

        switch (type) {
            case VISA:
            case MASTERCARD:
            case DISCOVER: // { 4-4-4-4}
                idx = 16 + 3 - 4;
                break;
            case AMEX: // {4-6-5}
                idx = 15 + 2 - 5;
                break;
            default:
                idx = 0;
        }
        return idx;
    }

    public static int securityCodeValid(CardType type) {
        switch (type) {
            case AMEX:
                return 4;
            case DISCOVER:
            case INVALID:
            case MASTERCARD:
            case VISA:
            default:
                return 3;
        }
    }

    public static int cardImageForCardType(CardType type, boolean back) {
        switch (type) {
            case AMEX:
                if (back) {
                    return R.drawable.cc_back;
                } else {
                    return R.drawable.amex;
                }
            case DISCOVER:
                if (back) {
                    return R.drawable.cc_back;
                } else {
                    return R.drawable.discover;
                }
            case MASTERCARD:
                if (back) {
                    return R.drawable.cc_back;
                } else {
                    return R.drawable.master_card;
                }
            case VISA:
                if (back) {
                    return R.drawable.cc_back;
                } else {
                    return R.drawable.visa;
                }
            default:
                if (back) {
                    return R.drawable.cc_back;
                } else {
                    return R.drawable.unknown_cc;
                }
        }
    }
}
