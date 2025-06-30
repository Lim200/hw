package com.example.internetbanking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class Card {
    private String cardNumber;
    private Long clientId;
    private double balance;

    public Card() {

    }

    public Card(String cardNumber, Long clientId, double balance) {
        this.cardNumber = cardNumber;
        this.clientId = clientId;
        this.balance = balance;
    }



    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
