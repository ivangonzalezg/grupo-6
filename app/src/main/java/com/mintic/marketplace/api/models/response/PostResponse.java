package com.mintic.marketplace.api.models.response;

/**
 * Created by Iván González on 11/08/21.
 */

public class PostResponse {
    private String paymentIntent;
    private String ephemeralKey;
    private String customer;

    public String getPaymentIntent() {
        return paymentIntent;
    }

    public void setPaymentIntent(String paymentIntent) {
        this.paymentIntent = paymentIntent;
    }

    public String getEphemeralKey() {
        return ephemeralKey;
    }

    public void setEphemeralKey(String ephemeralKey) {
        this.ephemeralKey = ephemeralKey;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
