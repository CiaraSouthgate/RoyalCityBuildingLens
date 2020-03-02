package ca.bcit.royalcitybuildinglens;

import org.json.JSONObject;

public class Building {
    private int id;
    private int mapRef;
    private String unitNum;
    private String streetNum;
    private String streetName;
    private String buildingName;
    private int numAbove;
    private int numBelow;
    private int sqMeter;
    private int footprint;
    private int numResidence;
    private int sqmAbove;
    private int sqmBelow;
    private int buildingAge;
    private String developer;
    private String architect;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMapRef() {
        return mapRef;
    }

    public void setMapRef(int mapRef) {
        this.mapRef = mapRef;
    }

    public String getUnitNum() {
        return unitNum;
    }

    public void setUnitNum(String unitNum) {
        this.unitNum = unitNum;
    }

    public String getStreetNum() {
        return streetNum;
    }

    public void setStreetNum(String streetNum) {
        this.streetNum = streetNum;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public int getNumAbove() {
        return numAbove;
    }

    public void setNumAbove(int numAbove) {
        this.numAbove = numAbove;
    }

    public int getNumBelow() {
        return numBelow;
    }

    public void setNumBelow(int numBelow) {
        this.numBelow = numBelow;
    }

    public int getSqMeter() {
        return sqMeter;
    }

    public void setSqMeter(int sqMeter) {
        this.sqMeter = sqMeter;
    }

    public int getFootprint() {
        return footprint;
    }

    public void setFootprint(int footprint) {
        this.footprint = footprint;
    }

    public int getNumResidence() {
        return numResidence;
    }

    public void setNumResidence(int numResidence) {
        this.numResidence = numResidence;
    }

    public int getSqmAbove() {
        return sqmAbove;
    }

    public void setSqmAbove(int sqmAbove) {
        this.sqmAbove = sqmAbove;
    }

    public int getSqmBelow() {
        return sqmBelow;
    }

    public void setSqmBelow(int sqmBelow) {
        this.sqmBelow = sqmBelow;
    }

    public int getBuildingAge() {
        return buildingAge;
    }

    public void setBuildingAge(int buildingAge) {
        this.buildingAge = buildingAge;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getArchitect() {
        return architect;
    }

    public void setArchitect(String architect) {
        this.architect = architect;
    }

    //    public Building(JSONObject json) {
//        this.id =
//    }
}
