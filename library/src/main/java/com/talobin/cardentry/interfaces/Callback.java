package com.talobin.cardentry.interfaces;

/**
 * Created by hai on 5/4/15.
 */
public interface Callback {
    void onSuccess();

    void onError();

    public static class EmptyCallback implements Callback {

        @Override public void onSuccess() {
        }

        @Override public void onError() {
        }
    }
}
