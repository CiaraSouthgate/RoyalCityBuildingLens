package ca.bcit.royalcitybuildinglens;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.location.Location;
import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Building {
    /** Building ID number */
    @SerializedName("BLDG_ID")
    private int id;

    /** Map reference */
    @SerializedName("MAPREF")
    private int mapRef;

    /** Unit number(s) for this building */
    @SerializedName("UNITNUM")
    private String unitNum;

    /** Street number fort this building */
    @SerializedName("STRNUM")
    private String streetNum;

    /** Name of the street this building is on */
    @SerializedName("STRNAM")
    private String streetName;

    /** This building's name */
    @SerializedName("BLDGNAM")
    private String buildingName;

    /** Year this building was built */
    @SerializedName("BLDGAGE")
    private int yearBuilt;

    /** Name of this building's developer */
    @SerializedName("DEVELOPER")
    private String developer;

    /** Name of this building's architect  */
    @SerializedName("ARCHITECT")
    private String architect;

    /** Year the building moved (0 if the building has never moved) */
    @SerializedName("MOVED")
    private int yearMoved;

    /** Number of floors above ground */
    @SerializedName("NUM_A_GRND")
    private int floorsAbove;

    /** Number of floors below ground */
    @SerializedName("NUM_B_GRND")
    private int floorsBelow;

    /** Area of the site coverage of this building in square metres */
    @SerializedName("SQM_SITCVR")
    private double siteCoverage;

    /** Area of the footprint of this building in square metres */
    @SerializedName("SQM_FTPRNT")
    private double footprint;

    /** Number of residences in this building */
    @SerializedName("NUM_RES")
    private int numResidence;

    /** Area above ground in square metres */
    @SerializedName("SQM_A_GRND")
    private double areaAbove;

    /** Area below ground in square metres*/
    @SerializedName("SQM_B_GRND")
    private double areaBelow;

    /** Array of lat & lon values representing the footprint of this building */
    private JSONArray coordinates;

    /** Single averaged lat & long value representing the location of this building */
    private Location location;

    private double location_lat;
    private double location_long;

    /**
     * Merges two building objects together
     * @param other Building
     */
    public void merge(Building other) {
        if (this.id != other.id)
            throw new IllegalArgumentException("Cannot merge buildings with different ID");
        this.yearBuilt = other.yearBuilt;
        this.developer = other.developer;
        this.architect = other.architect;
        this.buildingName = other.buildingName;
    }

    public List<Pair<String, String>> getFields() {
        Resources res = Resources.getSystem();
//        HashMap<String, String> fields = new HashMap<>();
        List<Pair<String, String>> fields = new ArrayList<>();
        fields.add(Pair.create(res.getString(R.string.built_in), this.getYearBuiltString()));
        if (developer != null && !developer.isEmpty())
            fields.add(Pair.create(res.getString(R.string.developed_by), developer));
        if (architect != null && !architect.isEmpty())
            fields.add(Pair.create(res.getString(R.string.architect), architect));
        fields.add(Pair.create(res.getString(R.string.num_res), Integer.toString(numResidence)));
        fields.add(Pair.create(res.getString(R.string.floors_above), Integer.toString(floorsAbove)));
        fields.add(Pair.create(res.getString(R.string.floors_below), Integer.toString(floorsBelow)));
        fields.add(Pair.create(res.getString(R.string.area_above), Double.toString(areaAbove)));
        fields.add(Pair.create(res.getString(R.string.area_below), Double.toString(areaBelow)));
        fields.add(Pair.create(res.getString(R.string.footprint), Double.toString(footprint)));
        fields.add(Pair.create(res.getString(R.string.site_coverage), Double.toString(siteCoverage)));
        if (yearMoved != 0)
            fields.add(Pair.create(res.getString(R.string.moved_in), Integer.toString(yearMoved)));

        return fields;
    }

    /**
     * Formats a word into title case
     * @param word String
     * @return String
     */
    private String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    /**
     * Formats a String into title case
     * @param line String
     * @return String
     */
    private String toTitleCase(String line) {
        if (line != null) {
            String[] lineWords = line.toLowerCase().split(" ");
            for (int i = 0; i < lineWords.length; i++) {
                String word = capitalize(lineWords[i]);
                lineWords[i] = word;
            }
            return String.join(" ", lineWords);
        } else {
            return null;
        }
    }

    /**
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * @param id int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return int
     */
    public int getMapRef() {
        return mapRef;
    }

    /**
     * @param mapRef int
     */
    public void setMapRef(int mapRef) {
        this.mapRef = mapRef;
    }

    /**
     * @return String
     */
    public String getUnitNum() {
        return unitNum;
    }

    /**
     * @param unitNum String
     */
    public void setUnitNum(String unitNum) {
        this.unitNum = unitNum;
    }

    /**
     * @return String
     */
    public String getStreetNum() {
        return streetNum;
    }

    /**
     * @param streetNum String
     */
    public void setStreetNum(String streetNum) {
        this.streetNum = streetNum;
    }

    /**
     * @return String
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * @param streetName String
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * Formats street name into title case and replaces the abbreviated suffix with its full word
     * @return String
     */
    public String getStreetNameString() {
        if (streetName != null) {
            String[] streetWords = toTitleCase(streetName).split(" ");
            String suffix = streetWords[streetWords.length - 1];
            // ST, PL, CRT, DR, AVE, RD, WAY, CRES, LANE, ROW, BLVD, SQ
            switch(suffix) {
                case "St":
                    suffix = "Street";
                    break;
                case "Pl":
                    suffix = "Place";
                    break;
                case "Crt":
                    suffix = "Court";
                    break;
                case "Dr":
                    suffix = "Drive";
                    break;
                case "Ave":
                    suffix = "Avenue";
                    break;
                case "Rd":
                    suffix = "Road";
                    break;
                case "Blvd":
                    suffix = "Boulevard";
                    break;
                case "Sq":
                    suffix = "Square";
                    break;
                default:
                    // suffix is already formatted
            }

            streetWords[streetWords.length - 1] = suffix;

            return String.join(" ", streetWords);
        } else {
            return null;
        }
    }

    public String getAddress() {
        return streetNum + getStreetNameString();
    }

    /**
     * @return String
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * @param buildingName String
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * Formats building name into title case
     * @return String
     */
    public String getBuildingNameString() {
        if (buildingName == null || buildingName.isEmpty())
            return null;
        return toTitleCase(buildingName);
    }

    /**
     * @return int
     */
    public int getFloorsAbove() {
        return floorsAbove;
    }

    /**
     * @param floorsAbove int
     */
    public void setFloorsAbove(int floorsAbove) {
        this.floorsAbove = floorsAbove;
    }

    /**
     * @return int
     */
    public int getFloorsBelow() {
        return floorsBelow;
    }

    /**
     * @param floorsBelow int
     */
    public void setFloorsBelow(int floorsBelow) {
        this.floorsBelow = floorsBelow;
    }

    /**
     * @return double
     */
    public double getSiteCoverage() {
        return siteCoverage;
    }

    /**
     * @param siteCoverage double
     */
    public void setSiteCoverage(double siteCoverage) {
        this.siteCoverage = siteCoverage;
    }

    /**
     * @return double
     */
    public double getFootprint() {
        return footprint;
    }

    /**
     * @param footprint double
     */
    public void setFootprint(double footprint) {
        this.footprint = footprint;
    }

    /**
     * @return int
     */
    public int getNumResidence() {
        return numResidence;
    }

    /**
     * @param numResidence int
     */
    public void setNumResidence(int numResidence) {
        this.numResidence = numResidence;
    }

    /**
     * @return double
     */
    public double getAreaAbove() {
        return areaAbove;
    }

    /**
     * @param areaAbove double
     */
    public void setAreaAbove(double areaAbove) {
        this.areaAbove = areaAbove;
    }

    /**
     * @return double
     */
    public double getAreaBelow() {
        return areaBelow;
    }

    /**
     * @param areaBelow double
     */
    public void setAreaBelow(double areaBelow) {
        this.areaBelow = areaBelow;
    }

    /**
     * @return int
     */
    public int getYearBuilt() {
        return yearBuilt;
    }

    /**
     * @param yearBuilt int
     */
    public void setYearBuilt(int yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    /**
     * Gets the current age of this building in years
     * @return int
     */
    public int getBuildingAge() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return year - yearBuilt;
    }

    @SuppressLint("DefaultLocale")
    public String getYearBuiltString() {
        return String.format("%d (%d years old)", getYearBuilt(), getBuildingAge());
    }

    /**
     * @return String
     */
    public String getDeveloper() {
        return developer;
    }

    /**
     * @param developer String
     */
    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getDeveloperString() {
        return toTitleCase(developer);
    }

    /**
     * @return String
     */
    public String getArchitect() {
        return architect;
    }

    /**
     * @param architect String
     */
    public void setArchitect(String architect) {
        this.architect = architect;
    }

    /**
     * Formats architect into title case
     * @return String
     */
    public String getArchitectString() {
        return toTitleCase(architect);
    }

    /**
     * @return int
     */
    public int getYearMoved() {
        return yearMoved;
    }

    /**
     * @param yearMoved int
     */
    public void setYearMoved(int yearMoved) {
        this.yearMoved = yearMoved;
    }

    /**
     * @param coordinates JSONArray
     */
    public void setCoordinates(JSONArray coordinates) {
        this.coordinates = coordinates;
        calculateLocation();
    }

    /**
     * @return location
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * @param location location
     */
    public void setLocation(Location location) {
        this.location_lat = location.getLatitude();
        this.location_long = location.getLongitude();
        this.location = location;
    }

    public void restoreLocation() {
        Location location = new Location("");
        location.setLatitude(this.location_lat);
        location.setLongitude(this.location_long);
        this.location = location;
    }

    /**
     * Calcuate an approximate latitude and longitude to represent this building by taking list of
     * coordinates from its footprint and averaging them.
     */
    private void calculateLocation() {
        int coordinatesLength = this.coordinates.length();
        double totalLatitude = 0.0;
        double totalLongitude = 0.0;

        try {
            for (int i = 0; i < coordinatesLength; i++) {
                JSONArray coords = (JSONArray) this.coordinates.get(i);
                totalLatitude += coords.getDouble(1);
                totalLongitude += coords.getDouble(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double avg_lat = totalLatitude / coordinatesLength;
        double avg_long = totalLongitude / coordinatesLength;

        Location location = new Location("");
        location.setLatitude(avg_lat);
        location.setLongitude(avg_long);

        setLocation(location);
    }

    /**
     * @return String
     */
    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", mapRef=" + mapRef +
                ", unitNum='" + unitNum + '\'' +
                ", streetNum='" + streetNum + '\'' +
                ", streetName='" + getStreetNameString() + '\'' +
                ", buildingName='" + getBuildingNameString() + '\'' +
                ", buildingAge=" + getBuildingAge() + " years old" +
                ", numAbove=" + floorsAbove +
                ", numBelow=" + floorsBelow +
                ", sqMeter=" + siteCoverage +
                ", footprint=" + footprint +
                ", numResidence=" + numResidence +
                ", sqmAbove=" + areaAbove +
                ", sqmBelow=" + areaBelow +
                ", yearBuilt=" + yearBuilt +
                ", developer='" + getDeveloperString() + '\'' +
                ", architect='" + getArchitectString() + '\'' +
                ", yearMoved=" + yearMoved +
                ", coordinates=" + coordinates +
                ", location=" + location +
                "}";
    }
}
