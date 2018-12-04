package model;


import java.io.PrintWriter;
import java.util.*;

public class Map {
    private int[][] matrix;
    private HashMap<Integer,City> cities;
    private HashMap<Integer,Country> countries;
    private HashMap<City,LinkedList<City>> citiesNeighbourship;

    public Map(){
        cities = new HashMap<>();
        countries= new HashMap<>();
        matrix = new int[10][10];
        citiesNeighbourship = new HashMap<>();
    }

    public void prepareToStart(){
        setCitiesNeighbourship();
        setInitialBalances();
    }

    public void doDiffusion(){
        int count=0;
        do {
            count++;
            prepareNewDay();
            doNewDay();
            closeDay();
        } while(!checkCitiesDone(count));

    }


    public boolean checkCountriesAccessibility() {
        LinkedList<Country> accessibleCountries = new LinkedList<>();
        LinkedList<Country> currentCountryNeighbours = new LinkedList<>();
        Country currentCountry = getSomeCountryFromMap();

        while (accessibleCountries.size() < countries.size()) {
            accessibleCountries.add(currentCountry);
            findLeftNeighboursForCountry(currentCountry,accessibleCountries,currentCountryNeighbours);
            findHigherNeighboursForCountry(currentCountry,accessibleCountries,currentCountryNeighbours);
            findRightNeighboursForCountry(currentCountry,accessibleCountries,currentCountryNeighbours);
            findLowerNeighboursForCountry(currentCountry,accessibleCountries,currentCountryNeighbours);
            if(currentCountryNeighbours.size()!=0){
                currentCountry = countries.get(currentCountryNeighbours.get(0).getNumber());
            }
            else {
                break;
            }
        }
        return accessibleCountries.size()==countries.size();
    }

    public void addCountry(Country country){
        country.setNumber(countries.size()+1);//id of country is size of hashmap
        countries.put(country.getNumber(),country);
        for(int y=country.getLowerLeftY();y<=country.getUpperRightY();y++) {//creating a list of cities for current country
            for (int x = country.getLowerLeftX(); x <= country.getUpperRightX(); x++) {
                City city = new City(x,y,country);
                cities.put(getHashForCity(city.getCoordX(),city.getCoordY()),city);
                matrix[x][y]=country.getNumber();
            }
        }
    }

    public boolean validateCoordinates(){
        for (Country country : countries.values()){
            if(!validateCountryCoordinates(country)){
                return false;
            }
        }
        return true;
    }
    private boolean validateCountryCoordinates(Country country){
        if((country.getUpperRightY()<0 || country.getUpperRightY()>9) ||
                (country.getUpperRightX()<0 || country.getUpperRightX()>9) ||
                (country.getLowerLeftY()<0 || country.getLowerLeftY()>9) ||
                (country.getLowerLeftX()<0 || country.getLowerLeftX()>9)){
            return false;
        }
        return true;
    }

    public void showResults(PrintWriter writer){
        List<Country> countriesToOutput = new LinkedList<>();
        countriesToOutput.addAll(countries.values());
        countriesToOutput.sort(new Comparator<Country>() {
            @Override
            public int compare(Country o1, Country o2) {
                int compareByDay = o1.getFilledDay() - o2.getFilledDay();
                if (compareByDay == 0) {
                    return o1.getName().compareTo(o2.getName());
                } else return compareByDay;
            }
        });
        for(Country country:countriesToOutput){
            writer.println(country.getName()+" " +country.getFilledDay());
        }
    }

    private void doNewDay(){
        prepareNewDay();
        doTransactionsWithNeighbours();
        closeDay();
    }

    private void closeDay(){
        for (City city:cities.values()){
            city.fillBalances();
            city.clearIncoming();
        }
    }

    private boolean checkCitiesDone(int day){
        for(Country country:countries.values()){
            if(country.getFilledDay()<0){
                country.setFilledDay(day);
            }
        }
        boolean allCitiesDone = true;
        for(City city : cities.values()){
            if(!city.checkIfDone(countries.values())){
                allCitiesDone = false;
                city.getCountry().setFilledDay(-1);
            }
        }
        return allCitiesDone;
    }

    private void doTransactionsWithNeighbours() {
        for (City city : citiesNeighbourship.keySet()) {
            for (City neighbour : citiesNeighbourship.get(city)) {
                for (Country country : countries.values()) {
                    doTransaction(city, neighbour, country);
                }
            }
        }
    }

    private void prepareNewDay(){
        for (City city:cities.values()){
            city.setAmountsToPay();
        }
    }

    private void doTransaction(City debit, City credit,Country country){
        int amount = debit.withdrawalCoins(country);
        credit.acceptCoins(country,amount);
    }

    private void setCitiesNeighbourship(){
        for(City city:cities.values()){
            int x = city.getCoordX();
            int y = city.getCoordY();
            LinkedList<City> neighbours = new LinkedList<>();
            if(checkLeftNeighbourCity(x,y)){
                neighbours.add(cities.get(getHashForCity(x-1,y)));
            }
            if(checkRightNeighbourCity(x,y)){
                neighbours.add(cities.get(getHashForCity(x+1,y)));
            }
            if(checkHigherNeighbourCity(x,y)){
                neighbours.add(cities.get(getHashForCity(x,y+1)));
            }
            if(checkLowerNeighbourCity(x,y)){
                neighbours.add(cities.get(getHashForCity(x,y-1)));
            }
            citiesNeighbourship.put(city,neighbours);
        }
    }

    private void setInitialBalances(){
        for(City city:cities.values()){
            city.setInitialBalances(countries.values());
        }
    }

    private Country getSomeCountryFromMap(){
        for(Country country:countries.values()){
            return country;
        }
        return null;
    }

    private int getHashForCity(int x,int y){
        return 10*x+y;
    }

    private boolean checkLeftNeighbourCity(int x,int y){
        return x != 0 && matrix[x - 1][y] != 0;
    }

    private boolean checkRightNeighbourCity(int x,int y){
        return x != 9 && matrix[x + 1][y] != 0;
    }

    private boolean checkHigherNeighbourCity(int x,int y){
        return y != 9 && matrix[x][y + 1] != 0;
    }

    private boolean checkLowerNeighbourCity(int x,int y){
        return y != 0 && matrix[x ][y - 1] != 0;
    }


    private void findLeftNeighboursForCountry(Country currentCountry,LinkedList<Country> accessibleCountries,List<Country> currentCountryNeighbours){
        if (currentCountry.getLowerLeftX() != 0) {
            for (int y = currentCountry.getLowerLeftY(); y <= currentCountry.getUpperRightY(); y++) {
                int country = matrix[currentCountry.getLowerLeftX()-1][y];
                if (country != 0) {
                    if (!(accessibleCountries.contains(countries.get(country)) || currentCountryNeighbours.contains(countries.get(country)))) {
                        currentCountryNeighbours.add(countries.get(country));
                    }
                }
            }
        }
    }

    private void findRightNeighboursForCountry(Country currentCountry,LinkedList<Country> accessibleCountries,List<Country> currentCountryNeighbours){
        if (currentCountry.getUpperRightX() != 9) {
            for (int y = currentCountry.getLowerLeftY(); y <= currentCountry.getUpperRightY(); y++) {
                int country = matrix[currentCountry.getUpperRightX()+1][y];
                if (country != 0) {
                    if (!(accessibleCountries.contains(countries.get(country)) || currentCountryNeighbours.contains(countries.get(country)))) {
                        currentCountryNeighbours.add(countries.get(country));
                    }
                }
            }
        }
    }

    private void findLowerNeighboursForCountry(Country currentCountry,LinkedList<Country> accessibleCountries,List<Country> currentCountryNeighbours){
        if (currentCountry.getLowerLeftY() != 0) {
            for (int x = currentCountry.getLowerLeftX(); x <= currentCountry.getUpperRightX(); x++) {
                int country = matrix[x][currentCountry.getLowerLeftY()-1];
                if (country != 0) {
                    if (!(accessibleCountries.contains(countries.get(country)) || currentCountryNeighbours.contains(countries.get(country)))) {
                        currentCountryNeighbours.add(countries.get(country));
                    }
                }
            }
        }
    }

    private void findHigherNeighboursForCountry(Country currentCountry,LinkedList<Country> accessibleCountries,List<Country> currentCountryNeighbours){
        if (currentCountry.getUpperRightY() != 9) {
            for (int x = currentCountry.getLowerLeftX(); x <= currentCountry.getUpperRightX(); x++) {
                int country = matrix[x][currentCountry.getUpperRightY()+1];
                if (country != 0) {
                    if (!(accessibleCountries.contains(countries.get(country)) || currentCountryNeighbours.contains(countries.get(country)))){
                        currentCountryNeighbours.add(countries.get(country));
                    }
                }
            }
        }
    }





}
