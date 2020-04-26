package com.aina.dummydatabase;

public class Customer {
    private String custid;
    private String name;
    public Customer(String customerid, String customername){
        this.custid = customerid;
        this.name = customername;
    }

    public String getCustid() {
        return custid;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
