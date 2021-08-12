package com.mintic.marketplace.api.models.request;

/**
 * Created by Iván González on 11/08/21.
 */

public class PostRequest {
    private Integer amount;
    private String email;
    private String name;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
