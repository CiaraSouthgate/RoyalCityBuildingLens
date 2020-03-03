package ca.bcit.royalcitybuildinglens;

import com.google.gson.annotations.SerializedName;

public class Building {
    @SerializedName("BLDG_ID")
    private int id;
    @SerializedName("MAPREF")
    private int mapRef;
    @SerializedName("UNITNUM")
    private String unitNum;
    @SerializedName("STRNUM")
    private String streetNum;
    @SerializedName("STRNAM")
    private String streetName;
    @SerializedName("BLDGNAM")
    private String buildingName;
    @SerializedName("NUM_A_GRND")
    private int numAbove;
    @SerializedName("NUM_B_GRND")
    private int numBelow;
    @SerializedName("SQM_SITCVR")
    private double sqMeter;
    @SerializedName("SQM_FTPRNT")
    private double footprint;
    @SerializedName("NUM_RES")
    private int numResidence;
    @SerializedName("SQM_A_GRND")
    private double sqmAbove;
    @SerializedName("SQM_B_GRND")
    private double sqmBelow;
    @SerializedName("BLDGAGE")
    private int yearBuilt;
    @SerializedName("DEVELOPER")
    private String developer;
    @SerializedName("ARCHITECT")
    private String architect;
    @SerializedName("MOVED")
    private int yearMoved;

    public void merge(Building other) {
        if (this.id != other.id)
            throw new IllegalArgumentException("Cannot merge buildings with different ID");
        this.yearBuilt = other.yearBuilt;
        this.developer = other.developer;
        this.architect = other.architect;
    }

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

    public double getSqMeter() {
        return sqMeter;
    }

    public void setSqMeter(double sqMeter) {
        this.sqMeter = sqMeter;
    }

    public double getFootprint() {
        return footprint;
    }

    public void setFootprint(double footprint) {
        this.footprint = footprint;
    }

    public int getNumResidence() {
        return numResidence;
    }

    public void setNumResidence(int numResidence) {
        this.numResidence = numResidence;
    }

    public double getSqmAbove() {
        return sqmAbove;
    }

    public void setSqmAbove(double sqmAbove) {
        this.sqmAbove = sqmAbove;
    }

    public double getSqmBelow() {
        return sqmBelow;
    }

    public void setSqmBelow(double sqmBelow) {
        this.sqmBelow = sqmBelow;
    }

    public int getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(int yearBuilt) {
        this.yearBuilt = yearBuilt;
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

    public int getYearMoved() {
        return yearMoved;
    }

    public void setYearMoved(int yearMoved) {
        this.yearMoved = yearMoved;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", mapRef=" + mapRef +
                ", unitNum='" + unitNum + '\'' +
                ", streetNum='" + streetNum + '\'' +
                ", streetName='" + streetName + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", numAbove=" + numAbove +
                ", numBelow=" + numBelow +
                ", sqMeter=" + sqMeter +
                ", footprint=" + footprint +
                ", numResidence=" + numResidence +
                ", sqmAbove=" + sqmAbove +
                ", sqmBelow=" + sqmBelow +
                ", yearBuilt=" + yearBuilt +
                ", developer='" + developer + '\'' +
                ", architect='" + architect + '\'' +
                ", yearMoved=" + yearMoved +
                '}';
    }
}
