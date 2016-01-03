package me.isassist.isa;

/**
 * Created by Patryk on 2016-01-03.
 */
public enum Bihapi {
    CITY_OFFICES,
    CASH_MACHINES,
    DORMITORIES,
    PHARMACIES,
    HOTELS,
    POLICE_OFFICES,
    SPORT_FIELDS,
    SWIMMING_POOLS,
    VETURILO,
    THEATRES;

    public String getURL(){
        String url = "https://api.bihapi.pl/wfs/warszawa/";

        switch(this) {
            case CITY_OFFICES:
                return url + "cityOffices";
            case CASH_MACHINES:
                return url + "cashMachines";
            case DORMITORIES:
                return url + "dormitories";
            case PHARMACIES:
                return url + "pharmacies";
            case HOTELS:
                return url + "hotels";
            case POLICE_OFFICES:
                return url + "policeOffices";
            case SPORT_FIELDS:
                return url + "sportFields";
            case SWIMMING_POOLS:
                return url + "swimmingPools";
            case VETURILO:
                return url + "veturilo";
            case THEATRES:
                return url + "theatres";
            default:
                return null;
        }
    }
}
