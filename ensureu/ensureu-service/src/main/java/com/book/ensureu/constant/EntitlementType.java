package com.book.ensureu.constant;

public enum EntitlementType {

    SUBSCRIPTION("Subscription"),TEST_SERIES("Test Series"),USER_PASS("User Pass"),FREE_SUBSCRIPTION("Free Subscription");

    private String name;

    EntitlementType(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
