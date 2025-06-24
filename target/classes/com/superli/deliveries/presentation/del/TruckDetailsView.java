package com.superli.deliveries.presentation.del;

/**
 * Holds truck details formatted specifically for presentation purposes.
 */
public class TruckDetailsView {

    private final String licensePlateNum;
    private final String requiredLicenseType;
    private final float maxWeight;

    /**
     * Constructs a TruckDetailsView object.
     *
     * @param licensePlateNum       The license plate number of the truck.
     * @param requiredLicenseType   The license type required to operate this truck.
     * @param maxWeight             The maximum allowed weight of the truck.
     */
    public TruckDetailsView(String licensePlateNum, String requiredLicenseType, float maxWeight) {
        this.licensePlateNum = licensePlateNum;
        this.requiredLicenseType = requiredLicenseType;
        this.maxWeight = maxWeight;
    }

    public String getLicensePlateNum() {
        return licensePlateNum;
    }

    public String getRequiredLicenseType() {
        return requiredLicenseType;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    @Override
    public String toString() {
        return "TruckDetailsView{" +
                "licensePlateNum='" + licensePlateNum + '\'' +
                ", requiredLicenseType='" + requiredLicenseType + '\'' +
                ", maxWeight=" + maxWeight +
                '}';
    }
}
