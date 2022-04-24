package com.example.expensetrackerapp.Model;

public class Cat2 {

    private String type;
    private String id;
    private int amount;

    Cat2()
    {

    }
    public Cat2(int amount, String type, String id)
    {
        this.amount=amount;
        this.type=type;
        this.id=id;
    }
    public int getAmount() {
        return amount;
    }
    public String getType(){return  type;}
    public String getId() {
        return id;
    }
}
