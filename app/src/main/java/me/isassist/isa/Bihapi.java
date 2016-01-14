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
                return url + "city_offices";
            case CASH_MACHINES:
                return url + "cash_machines";
            case DORMITORIES:
                return url + "dormitories";
            case PHARMACIES:
                return url + "pharmacies";
            case HOTELS:
                return url + "hotels";
            case POLICE_OFFICES:
                return url + "police_offices";
            case SPORT_FIELDS:
                return url + "sport_fields";
            case SWIMMING_POOLS:
                return url + "swimming_pools";
            case VETURILO:
                return url + "veturilo";
            case THEATRES:
                return url + "theatres";
            default:
                return null;
        }
    }

    @Override
    public String toString()
    {
        switch (this) {
            case CITY_OFFICES:
                return "City offices";
            case CASH_MACHINES:
                return "ATMs";
            case DORMITORIES:
                return "Dormitories";
            case PHARMACIES:
                return "Pharmacies";
            case HOTELS:
                return "Hotels";
            case POLICE_OFFICES:
                return "Police offices";
            case SPORT_FIELDS:
                return "Sport fields";
            case SWIMMING_POOLS:
                return "Swimming pools";
            case VETURILO:
                return "City bikes";
            case THEATRES:
                return "Theatres";
            default:
                return null;
        }
    }
}
