package no.ntnu.mikaelr.delta.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhraseGenerator {

    private final String HERLIG = "Herlig";
    private final String SUPERT = "Supert";
    private final String AWESOME = "Awesome";
    private final String ALLRIGHT = "Allright";

    private List<String> encouragements = new ArrayList<String>();

    public PhraseGenerator() {
        encouragements.add(HERLIG);
        encouragements.add(SUPERT);
        encouragements.add(AWESOME);
        encouragements.add(ALLRIGHT);
    }

    public String encouragement() {
        Random random = new Random();
        int i = random.nextInt(encouragements.size()-1);
        return encouragements.get(i);
    }

}
