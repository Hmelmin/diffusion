package model;


import java.io.PrintWriter;
import java.util.*;

public class Map {
    private int[][] matrix;
    private HashMap<Integer,City> cities;
    private HashMap<Integer,Country> countries;
    private HashMap<City,LinkedList<City>> citiesNeighbourship;
    private static final int  maxCoord = 9;
    private static final int minCoord = 0;

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
        int count = 0;
        do {
            count++;
            prepareNewDay();
            doNewDay();
            closeDay();
        } while(!checkCitiesDone(count));

    }


    public boolean checkCountriesAccessibility() {
        LinkedList<Integer> accessibleCountries = new LinkedList<>();
        LinkedList<Integer> currentCountryNeighbours = new LinkedList<>();
        Integer currentCountry = getSomeCountryFromMap();

        while (accessibleCountries.size() < countries.size()) {
            accessibleCountries.add(currentCountry);
            findLeftAndRightNeighboursForCountry(currentCountry,accessibleCountries,currentCountryNeighbours);
            findLowerAndUpperNeighboursForCountry(currentCountry,accessibleCountries,currentCountryNeighbours);
            if(currentCountryNeighbours.size() != 0){
                currentCountry = currentCountryNeighbours.get(0);
            }
            else {
                break;
            }
        }
        return accessibleCountries.size()==countries.size();
    }

    public void addCountry(Country country){
        int id = countries.size()+1;
        countries.put(id,country);
        for(int y = country.lowerLeftY;y <= country.upperRightY;y++) {//creating a list of cities for current country
            for (int x = country.lowerLeftX; x <= country.upperRightX; x++) {
                City city = new City(x,y,country);
                cities.put(getHashForCity(city.getCoordX(),city.getCoordY()),city);
                matrix[x][y]=id;
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
        return !((country.upperRightY < minCoord || country.upperRightY > maxCoord) ||
                (country.upperRightX < minCoord || country.upperRightX > maxCoord) ||
                (country.lowerLeftY < minCoord || country.lowerLeftY > maxCoord) ||
                (country.lowerLeftX < minCoord || country.lowerLeftX > maxCoord));
    }

    public void showResults(PrintWriter writer){
        List<Country> countriesToOutput = new LinkedList<>();
        countriesToOutput.addAll(countries.values());
        countriesToOutput.sort(new Comparator<Country>() {
            @Override
            public int compare(Country o1, Country o2) {
                int compareByDay = o1.getFilledDay() - o2.getFilledDay();
                if (compareByDay == 0) {
                    return o1.name.compareTo(o2.name);
                } else return compareByDay;
            }
        });
        for(Country country:countriesToOutput){
            writer.println(country.name+" " +country.getFilledDay());
        }
    }

    private void doNewDay(){
        prepareNewDay();
        doTransactionsWithNeighbours();
        closeDay();
    }

    private void closeDay(){
        for (City city : cities.values()){
            city.fillBalances();
            city.clearIncoming();
        }
    }

    private boolean checkCitiesDone(int day){
        for(Country country : countries.values()){
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
        for(City city : cities.values()){
            int x = city.getCoordX();
            int y = city.getCoordY();
            LinkedList<City> neighbours = new LinkedList<>();
            if(checkLeftNeighbourCity(x,y)){
                neighbours.add(cities.get(getHashForCity(x - 1,y)));
            }
            if(checkRightNeighbourCity(x,y)){
                neighbours.add(cities.get(getHashForCity(x + 1,y)));
            }
            if(checkHigherNeighbourCity(x,y)){
                neighbours.add(cities.get(getHashForCity(x,y + 1)));
            }
            if(checkLowerNeighbourCity(x,y)){
                neighbours.add(cities.get(getHashForCity(x,y - 1)));
            }
            citiesNeighbourship.put(city,neighbours);
        }
    }

    private void setInitialBalances(){
        for(City city : cities.values()){
            city.setInitialBalances(countries.values());
        }
    }

    private Integer getSomeCountryFromMap(){
        for(Integer key : countries.keySet()){
            return key;
        }
        return null;
    }

    private int getHashForCity(int x,int y){
        return 10*x+y;
    }

    private boolean checkLeftNeighbourCity(int x,int y){
        return x != minCoord && matrix[x - 1][y] != 0;
    }

    private boolean checkRightNeighbourCity(int x,int y){
        return x != maxCoord && matrix[x + 1][y] != 0;
    }

    private boolean checkHigherNeighbourCity(int x,int y){
        return y != maxCoord && matrix[x][y + 1] != 0;
    }

    private boolean checkLowerNeighbourCity(int x,int y){
        return y != minCoord && matrix[x][y - 1] != 0;
    }


    private void findLeftAndRightNeighboursForCountry(Integer currentCountryNumber,List<Integer> accessibleCountries,List<Integer> currentCountryNeighbours){
        Country currentCountry = countries.get(currentCountryNumber);
            for (int i=0; i <= currentCountry.upperRightY - currentCountry.lowerLeftY; i++) {
                if(currentCountry.lowerLeftX != minCoord) {
                    int country = matrix[currentCountry.lowerLeftX - 1][currentCountry.lowerLeftY + i];
                    addCountryToNeighboursList(country, accessibleCountries, currentCountryNeighbours);
                }
                if(currentCountry.upperRightX != maxCoord) {
                    int country = matrix[currentCountry.upperRightX + 1][i + currentCountry.lowerLeftY + i];
                    addCountryToNeighboursList(country, accessibleCountries, currentCountryNeighbours);
                }
            }


    }



    private void findLowerAndUpperNeighboursForCountry(Integer currentCountryNumber,List<Integer> accessibleCountries,List<Integer> currentCountryNeighbours){
        Country currentCountry = countries.get(currentCountryNumber);

            for (int i = currentCountry.lowerLeftX; i <= currentCountry.upperRightX- currentCountry.lowerLeftX; i++) {
                if (currentCountry.lowerLeftY != minCoord) {
                    int country = matrix[currentCountry.lowerLeftX + i ][currentCountry.lowerLeftY - 1];
                    addCountryToNeighboursList(country, accessibleCountries, currentCountryNeighbours);
                }
                if (currentCountry.upperRightY != maxCoord) {
                    int country = matrix[currentCountry.lowerLeftX + i ][currentCountry.upperRightY + 1];
                    addCountryToNeighboursList(country, accessibleCountries, currentCountryNeighbours);
                }
            }

    }


    private void addCountryToNeighboursList(int countryNumber,List<Integer> accessibleCountries,List<Integer> currentCountryNeighbours ){
        if(countryNumber != 0) {
            if (!(accessibleCountries.contains(countryNumber) ||
                    currentCountryNeighbours.contains(countryNumber))) {
                currentCountryNeighbours.add(countryNumber);
            }
        }
    }
}
