package com.aina.dummydatabase;

public class FeeEntry {
    private String datetime;
    private String amount;
    private String serialno;
    private String custid;
    public FeeEntry(String dt, String am, String serial,String cust){
        this.datetime = dt;
        this.amount = am;
        this.serialno = serial;
        this.custid = cust;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getCustid() {
        return custid;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }
}
