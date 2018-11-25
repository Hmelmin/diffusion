package model;

import java.util.Collection;
import java.util.HashMap;


public class City {
    private int coordX;
    private int coordY;
    private Country country;
    /*for each type of coins we create a separate "account"*/
    private HashMap<Country,Integer> currentBalance;//accounts to store current amount of coins of each type
    private HashMap<Country,Integer> incomingBalance;//accounts to receive new coins of each type during the day
    private HashMap<Country,Integer> amountToPay;//amount of coins of each type to pay during the day


    public City(int coordX, int coordY, Country country) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.country = country;
        currentBalance = new HashMap<>();
        incomingBalance = new HashMap<>();
        amountToPay = new HashMap<>();
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public Country getCountry() {
        return country;
    }

    public void acceptCoins(Country country,int amount){
        incomingBalance.put(country,incomingBalance.get(country)+amount);
    }
    public int withdrawalCoins(Country country){
        currentBalance.put(country,currentBalance.get(country)-amountToPay.get(country));
        return amountToPay.get(country);
    }

    public void setAmountsToPay(){
        for(Country  key:currentBalance.keySet()){
            amountToPay.put(key,currentBalance.get(key)/1000);
        }
    }

    public void fillBalances(){
        for(Country  key:currentBalance.keySet()){
            currentBalance.put(key,currentBalance.get(key)+incomingBalance.get(key));
        }
    }

    public void clearIncoming(){
        for(Country  key:incomingBalance.keySet()){
            incomingBalance.put(key,0);
        }
    }

    public void setInitialBalances(Collection<Country> countries){
        for(Country country:countries){
            if(this.country.getNumber()==country.getNumber()){
                currentBalance.put(country,1000000);
            }
            else{
                currentBalance.put(country,0);
            }
            amountToPay.put(country,0);
            incomingBalance.put(country,0);
        }
    }

    public boolean checkIfDone(Collection<Country> countries){
        for(Country country:countries){
            if(currentBalance.get(country)==0){
                return false;
            }
        }
        return true;
    }


}
