package no.ntnu.mikaelr.delta.util;

import no.ntnu.mikaelr.delta.R;

import java.util.HashMap;

public class BadgeIdConverter {

    private static BadgeIdConverter instance = null;
    private static HashMap<String,Integer> iconResourceIdMap = new HashMap<String, Integer>();

    protected BadgeIdConverter() {}

    public static BadgeIdConverter getInstance() {
        if (instance == null) {
            createIconResourceMap();
            instance = new BadgeIdConverter();
        }
        return instance;
    }

    public int convertBadgeNameToResourceId(String badgeName){
        Integer resourceId = iconResourceIdMap.get(badgeName+".png");
        if(resourceId == null)
            return R.drawable.ic_achievement;
        return resourceId;
    }

    private static void createIconResourceMap() {
        iconResourceIdMap = new HashMap<String, Integer>();
        iconResourceIdMap.put("ic_ach_deltaker_v1.png", R.drawable.ic_ach_deltaker_v1);
        iconResourceIdMap.put("ic_ach_deltaker_v2.png", R.drawable.ic_ach_deltaker_v2);
        iconResourceIdMap.put("ic_ach_kommentarer_v1.png", R.drawable.ic_ach_kommentarer_v1);
        iconResourceIdMap.put("ic_ach_profilbilde.png", R.drawable.ic_ach_profilbilde);
        iconResourceIdMap.put("ic_ach_tommelopp.png", R.drawable.ic_ach_tommelopp);
    }

}
