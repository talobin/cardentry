package com.talobin.cardentry.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.talobin.cardentry.R;
import com.talobin.cardentry.interfaces.Callback;
import com.talobin.cardentry.interfaces.CreditCardDelegate;
import com.talobin.cardentry.models.Card;
import com.talobin.cardentry.models.Card.CardType;
import com.talobin.cardentry.utils.CreditCardUtil;
import com.talobin.cardentry.utils.RotateAnimation;

/**
 * Created by hai on 4/17/15.
 */
public class CreditCardEntry extends RelativeLayout implements OnTouchListener,
    OnGestureListener,
    CreditCardDelegate,
    ViewTreeObserver.OnGlobalLayoutListener {

    public interface CardIOListener {
        public void onCardioClicked();
    }

    //<editor-fold desc="Class Properties">
    public static final int CARD_IO_REQUEST_CODE = 50;

    private static final int ANIM_DURATION = 150;
    private Context mContext;
    private float mTranslateDistance;
    private boolean mIsShaking;
    private Callback mDoneCallback;
    private float mCardImageHalfWidth;
    private float mCardImageHalfHeight;
    private boolean mShouldAnimate;
    private CardType mCardType;
    private CardIOListener mCardIOListener;

    private LinearLayout mExtraFieldsContainer;
    private ImageView mFrontCardImage;
    private ImageView mBackCardImage;
    private ImageView mBrandCardImage;
    private ImageView mCurrentVisibleView;
    private FieldCardNumber mFieldCardNumber;
    private FieldExpDate mFieldExpDate;
    private FieldSecurityCode mFieldSecurityCode;
    private FieldZipCode mFieldZipCode;
    private TextView mTextFourDigits;
    private BaseField mFocusedField;
    //</editor-fold>

    //<editor-fold desc="Layout Methods">

    @Override public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    @Override public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override public void onShowPress(MotionEvent e) {
    }

    @Override public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override public void onGlobalLayout() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
            this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        final float entryWidth = this.getWidth() - this.getPaddingRight() - this.getPaddingLeft();
        final float currentSize = mFieldCardNumber.getTextSize();
        final float textWidth =
            mFieldCardNumber.getPaint().measureText(CreditCardUtil.EXAMPLE_OVERVIEW_STRING);
        final float ratio = entryWidth / textWidth;
        final float newSize = currentSize * ratio;
        mFieldCardNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
        mTextFourDigits.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
        mFieldZipCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
        mFieldExpDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
        mFieldSecurityCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
        final int padding = (int) mFieldCardNumber.getPaint().measureText(" ");
        mTextFourDigits.setPadding(padding, 0, padding, 0);
        mFieldZipCode.setPadding(padding * 2, 0, padding, 0);
        mFieldExpDate.setPadding(padding, 0, padding, 0);
        mFieldSecurityCode.setPadding(padding, 0, padding, 0);
        mFieldCardNumber.setPadding(padding, 0, padding, 0);
        mCardImageHalfWidth = mFrontCardImage.getWidth() / 2;
        mCardImageHalfHeight = mFrontCardImage.getHeight() / 2;
    }
    //</editor-fold>

    //<editor-fold desc="Delegate Methods">

    /**
     * Takes actions when CardType changes:
     * - change card images
     * - show flipping animation
     */
    @Override public void onCardTypeChange(CardType type) {
        //Log.d("CRAP", "CARD TYPE CHANGE" + type + mCardType);
        if (mCardType != type) {
            mCardType = type;
            if (mCurrentVisibleView == mBrandCardImage) {
                if (mCardType == CardType.INVALID) {
                    showFrontCardImage();
                } else {
                    changeCardImagesToNewType(type);
                }
            } else {
                changeCardImagesToNewType(type);
                showBrandCardImage();
            }
            mFieldSecurityCode.setType(type);
        }
    }

    @Override public void onCreditCardNumberValid() {
        //Log.d("CRAP", "CARD VALID, FOCUS");
        focusOnField(mFieldExpDate);
        if (isCreditCardValid()) {
            mDoneCallback.onSuccess();
        }
    }

    @Override public void onExpirationDateValid() {
        focusOnField(mFieldSecurityCode);
        if (isCreditCardValid()) {
            mDoneCallback.onSuccess();
        }
    }

    @Override public void onSecurityCodeValid() {
        focusOnField(mFieldZipCode);
        if (isCreditCardValid()) {
            mDoneCallback.onSuccess();
        }
    }

    @Override public void onZipCodeValid() {
        if (isCreditCardValid()) {
            mDoneCallback.onSuccess();
        }
    }

    @Override public void onBadInput(final EditText field) {
        onInfoInvalidate();
        if (!mIsShaking) {
            //TODO: add viberate to all manifest
            Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
            AnimatorSet shakingAnimatorSet = new AnimatorSet();
            shakingAnimatorSet.playTogether(ObjectAnimator.ofFloat(field,
                                                                   "translationX",
                                                                   0,
                                                                   10,
                                                                   -10,
                                                                   5,
                                                                   -5,
                                                                   0));
            shakingAnimatorSet.setDuration(200);
            shakingAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {
                    mIsShaking = true;
                }

                @Override public void onAnimationEnd(Animator animation) {
                    mIsShaking = false;
                }

                @Override public void onAnimationCancel(Animator animation) {

                }

                @Override public void onAnimationRepeat(Animator animation) {

                }
            });
            shakingAnimatorSet.start();
        }
    }

    @Override public void focusOnField(BaseField field) {
        if (!field.hasFocus()) {
            field.requestFocus();
            field.setSelection(field.getText().length());
        }

        if (field.getClass().equals(FieldCardNumber.class) || mFocusedField == null) {
            if (mFocusedField != null) {
                mShouldAnimate = true;
            }
            focusOnCreditCardField();
        } else if (mFocusedField.getClass().equals(FieldCardNumber.class)) {
            mShouldAnimate = true;
            focusOnExtraField(field);
        } else {
            mShouldAnimate = false;
            focusOnExtraField(field);
        }
    }

    @Override public void focusOnPreviousField(BaseField field) {
        if (field.getClass().equals(FieldExpDate.class)) {
            focusOnField(mFieldCardNumber);
        } else if (field.getClass().equals(FieldSecurityCode.class)) {
            //Log.d("CRAP", "FOCUS ON PREVIOUS");
            focusOnField(mFieldExpDate);
        } else if (field.getClass().equals(FieldZipCode.class)) {
            focusOnField(mFieldSecurityCode);
        }
    }

    @Override public void onKeyEvent(KeyEvent event) {
        mFocusedField.dispatchKeyEvent(event);
    }

    @Override public void onCardIOEvent() {
        mCardIOListener.onCardioClicked();
        //mFocusedField.getText().clear();
        //focusOnPreviousField(mFocusedField);
    }

    @Override public void onInfoInvalidate() {
        mDoneCallback.onError();
    }
    //</editor-fold>

    //<editor-fold desc="Public Methods">
    public CreditCardEntry(Context context) {
        super(context);
        init(context);
    }

    public CreditCardEntry(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CreditCardEntry(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setCardFrontImage(ImageView image) {
        //Log.d("CRAP", "SET FRONT IMAGE");

        mFrontCardImage = image;
        if (mCurrentVisibleView == null) {
            mCurrentVisibleView = mFrontCardImage;
        }
    }

    public void setCardInput(Card card) {
        mFieldCardNumber.setText(card.getCardNumber());
        mFieldExpDate.setText(card.getExpirationMonth() + "/" + card.getExpirationYearSuffix());
        mFieldZipCode.setText(card.getZipCode());
        focusOnField(mFieldSecurityCode);
    }

    public void setCardIOListener(CardIOListener cardIOListener) {
        mCardIOListener = cardIOListener;
    }

    public void setCardBackImage(ImageView backCardImage) {
        //Log.d("CRAP", "SET BACK IMAGE");
        mBackCardImage = backCardImage;
    }

    public void setBrandCardImage(ImageView brandCardImage) {
        //Log.d("CRAP", "SET BRAND IMAGE");
        mBrandCardImage = brandCardImage;
    }

    public boolean isCreditCardValid() {
        return mFieldCardNumber.isValid()
            && mFieldExpDate.isValid()
            && mFieldSecurityCode.isValid()
            && mFieldZipCode.isValid();
    }

    public Card getCreditCard() {
        if (isCreditCardValid()) {
            return new Card(mFieldCardNumber.getText().toString(),
                            mFieldExpDate.getText().toString(),
                            mFieldSecurityCode.getText().toString(),
                            mFieldZipCode.getText().toString());
        } else {
            return null;
        }
    }

    public CreditCardDelegate getDelegate() {
        return mFieldCardNumber.getDelegate();
    }

    public void setCallBack(Callback callBack) {
        mDoneCallback = callBack;
    }
    //</editor-fold>

    //<editor-fold desc="Private Methods">
    private void calculateTranslateDistance(Boolean shouldUpdateLastDigits) {
        final String number = mFieldCardNumber.getText().toString();
        if (shouldUpdateLastDigits) {
            //Get last digits
            final int length = number.length();
            final int numberOfLastDigit;
            if (mFieldCardNumber.getType() == CardType.AMEX) {
                numberOfLastDigit = 5;
            } else {
                numberOfLastDigit = 4;
            }
            //Calculate translate distance
            if (!TextUtils.isEmpty(number) && length > numberOfLastDigit) {
                final String digits = number.substring(length - numberOfLastDigit);
                mTextFourDigits.setText(digits);
                final float numberLength = mFieldCardNumber.getPaint().measureText(number);
                final float fourdigitLength = mTextFourDigits.getPaint().measureText(digits);
                mTranslateDistance = 0 - numberLength + fourdigitLength;
            }
        } else {
            final String digits = mTextFourDigits.getText().toString();
            final float numberLength = mFieldCardNumber.getPaint().measureText(number);
            final float fourdigitLength = mTextFourDigits.getPaint().measureText(digits);
            mTranslateDistance = 0 - numberLength + fourdigitLength;
        }
    }

    private void changeCardImagesToNewType(CardType newType) {
        //Log.d("CRAP", "CHANGE IMAGES");
        mBrandCardImage.setImageResource(CreditCardUtil.cardImageForCardType(newType, false));
        mBackCardImage.setImageResource(CreditCardUtil.cardImageForCardType(newType, true));
    }

    private void init(Context context) {
        this.mContext = context;
        ViewTreeObserver vto = this.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(this);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int width;

        if (android.os.Build.VERSION.SDK_INT < 13) {
            width = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        }

        LayoutParams params =
            new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        this.setOnTouchListener(this);
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_entry, this, true);

        mFieldCardNumber = (FieldCardNumber) view.findViewById(R.id.Field_card_number);
        mFieldCardNumber.setDelegate(this);
        mFieldCardNumber.setWidth((int) (width));

        mTextFourDigits = (TextView) view.findViewById(R.id.TextView_four_digits);
        // when the user taps the last 4 digits of the card, they probably want to edit it
        mTextFourDigits.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                focusOnField(mFieldCardNumber);
            }
        });

        mFieldExpDate = (FieldExpDate) view.findViewById(R.id.Field_exp_date);
        mFieldExpDate.setDelegate(this);

        mFieldSecurityCode = (FieldSecurityCode) view.findViewById(R.id.Field_security_code);
        mFieldSecurityCode.setDelegate(this);

        mFieldZipCode = (FieldZipCode) view.findViewById(R.id.Field_zip_code);
        mFieldZipCode.setDelegate(this);

        mExtraFieldsContainer = (LinearLayout) view.findViewById(R.id.Layout_entry_root);

        mFieldCardNumber.requestFocus();
        mCurrentVisibleView = mFrontCardImage;
    }

    private void focusOnExtraField(final BaseField field) {
        if (mFocusedField != field) {
            mFocusedField = field;
            if (mShouldAnimate) {
                calculateTranslateDistance(true);
                AnimatorSet translatingAnimatorSet = new AnimatorSet();
                translatingAnimatorSet.playTogether(ObjectAnimator.ofFloat(mFieldCardNumber,
                                                                           "translationX",
                                                                           0,
                                                                           mTranslateDistance));
                translatingAnimatorSet.setDuration(400);
                translatingAnimatorSet.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {

                    }

                    @Override public void onAnimationEnd(Animator animation) {
                        if (mFieldCardNumber.getVisibility() == View.VISIBLE) {
                            mFieldCardNumber.setVisibility(GONE);
                            mExtraFieldsContainer.setVisibility(VISIBLE);
                        }
                        showAppropriateFace(field);
                    }

                    @Override public void onAnimationCancel(Animator animation) {

                    }

                    @Override public void onAnimationRepeat(Animator animation) {

                    }
                });
                translatingAnimatorSet.start();
            } else {
                showAppropriateFace(field);
            }
        }
    }

    private void showAppropriateFace(BaseField focusedField) {
        //Log.d("CRAP", "SHOW APPRORIATE");
        if (focusedField.getClass().equals(FieldSecurityCode.class)) {
            showBackCardImage();
        } else {
            showBrandCardImage();
        }
    }

    private void showBrandCardImage() {
        //Log.d("CRAP", "BRAND");
        if (mCardType == null || mCardType == CardType.INVALID) {
            showFrontCardImage();
        } else if (mBrandCardImage.getVisibility() != VISIBLE) {
            //FlipAnimator animator = new FlipAnimator(mCurrentVisibleView,
            //                                         mBrandCardImage,
            //                                         mCardImageHalfWidth,
            //                                         mCardImageHalfHeight);
            //mFrontCardImage.startAnimation(animator);
            flipToThisFace(mBrandCardImage);
        }
        //mCurrentVisibleView = mBrandCardImage;
        //mBackCardImage.setVisibility(INVISIBLE);
        //mFrontCardImage.setVisibility(INVISIBLE);

    }

    private void flipToThisFace(ImageView whichFace) {
        //if (whichFace == mBackCardImage) {
        //    Log.d("CRAP", "FLIP TO BACK");
        //} else if (whichFace == mFrontCardImage) {
        //    Log.d("CRAP", "FLIP TO FRONT");
        //} else {
        //    Log.d("CRAP", "FLIP TO BRAND");
        //}

        animateHidingCurrentFace(whichFace);
    }

    private void animateHidingCurrentFace(ImageView whichFaceToShow) {
        if (mCardImageHalfHeight * mCardImageHalfWidth == 0) {
            calculateImageDimension();
        }
        final int orientation = 1;

        final RotateAnimation rotation = new RotateAnimation(0,
                                                             90 * orientation,
                                                             mCardImageHalfWidth,
                                                             mCardImageHalfHeight,
                                                             false);
        rotation.setDuration(ANIM_DURATION);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new animateShowingNextFace(whichFaceToShow, true));
        mCurrentVisibleView.startAnimation(rotation);
    }

    private void calculateImageDimension() {
        mCardImageHalfWidth = mCurrentVisibleView.getWidth() / 2.0f;
        mCardImageHalfHeight = mCurrentVisibleView.getHeight() / 2.0f;
    }

    /**
     * Callback after a card rotation.
     */
    private final class animateShowingNextFace implements Animation.AnimationListener {

        private final ImageView mFaceToShow;
        private final int mOrientation;

        public animateShowingNextFace(ImageView faceToShow, boolean rotateLeft) {
            mFaceToShow = faceToShow;
            mOrientation = rotateLeft ? 1 : -1;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            final RotateAnimation rotation;
            if (mFaceToShow == mBackCardImage) {
                mFaceToShow.setVisibility(VISIBLE);
                mBrandCardImage.setVisibility(GONE);
                mFrontCardImage.setVisibility(GONE);
            } else if (mFaceToShow == mFrontCardImage) {
                mFaceToShow.setVisibility(VISIBLE);
                mBrandCardImage.setVisibility(GONE);
                mBackCardImage.setVisibility(GONE);
            } else {
                mFaceToShow.setVisibility(VISIBLE);
                mFrontCardImage.setVisibility(GONE);
                mBackCardImage.setVisibility(GONE);
            }
            rotation = new RotateAnimation(90 * mOrientation,
                                           0,
                                           mCardImageHalfWidth,
                                           mCardImageHalfHeight,
                                           false);
            rotation.setDuration(ANIM_DURATION);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            mFaceToShow.startAnimation(rotation);
            mCurrentVisibleView = mFaceToShow;
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private void showBackCardImage() {
        //Log.d("CRAP", "BACK");
        if (mBackCardImage.getVisibility() != VISIBLE) {
            //FlipAnimator animator = new FlipAnimator(mCurrentVisibleView,
            //                                         mBackCardImage,
            //                                         mCardImageHalfWidth,
            //                                         mCardImageHalfHeight);
            //mFrontCardImage.startAnimation(animator);
            flipToThisFace(mBackCardImage);
        }
        mCurrentVisibleView = mBackCardImage;
        //mFrontCardImage.setVisibility(INVISIBLE);
        //mBrandCardImage.setVisibility(INVISIBLE);
    }

    private void showFrontCardImage() {
        //Log.d("CRAP", "FRONT");
        if (mFrontCardImage != null && mFrontCardImage.getVisibility() != VISIBLE) {
            //FlipAnimator animator = new FlipAnimator(mCurrentVisibleView,
            //                                         mFrontCardImage,
            //                                         mCardImageHalfWidth,
            //                                         mCardImageHalfHeight);
            //mFrontCardImage.startAnimation(animator);
            flipToThisFace(mFrontCardImage);
        }
        mCurrentVisibleView = mFrontCardImage;
        //if (mBackCardImage != null && mBrandCardImage != null) {
        //    mBackCardImage.setVisibility(INVISIBLE);
        //    mBrandCardImage.setVisibility(INVISIBLE);
        //}
    }

    private void focusOnCreditCardField() {
        if (mFocusedField != mFieldCardNumber) {
            mFocusedField = mFieldCardNumber;
            if (mShouldAnimate) {
                calculateTranslateDistance(false);
                AnimatorSet shakingAnimatorSet = new AnimatorSet();
                shakingAnimatorSet.playTogether(ObjectAnimator.ofFloat(mFieldCardNumber,
                                                                       "translationX",
                                                                       mTranslateDistance,
                                                                       0));
                shakingAnimatorSet.setDuration(400);
                shakingAnimatorSet.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {
                        if (mFieldCardNumber.getVisibility() == View.GONE) {
                            mFieldCardNumber.setVisibility(VISIBLE);
                            mExtraFieldsContainer.setVisibility(GONE);
                        }
                    }

                    @Override public void onAnimationEnd(Animator animation) {
                        //Log.d("CRAP", "END ANIMATION");
                        showBrandCardImage();
                    }

                    @Override public void onAnimationCancel(Animator animation) {

                    }

                    @Override public void onAnimationRepeat(Animator animation) {

                    }
                });
                shakingAnimatorSet.start();
            } else {
                showBrandCardImage();
            }
        }
    }

    //</editor-fold>
}
