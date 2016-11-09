package org.c4i.nlp.ph3;

import org.c4i.nlp.ph3.normalize.PorterStemmer;
import org.c4i.nlp.ph3.normalize.StringNormalizer;
import org.c4i.nlp.ph3.normalize.StringNormalizers;
import org.junit.Test;

/**
 * Demonstrate normalizers by example
 * @author Arvid
 * @version 14-10-2015 - 21:02
 */
public class NormalizerTest {


    @Test
    public void demoDefault(){
        System.out.println("\nDemo default normalizer");
        String[] words = {"hyper-space", "f*cking", "arVId", "Caf\u00e9"};
        StringNormalizer normalizer = StringNormalizers.DEFAULT;
        for (String word : words) {
            System.out.printf("%s \t--> %s\n", word, normalizer.normalize(word));
        }
    }

    @Test
    public void demoCompose(){
        System.out.println("\nDemo composition of normalizers");
        String[] words = {"hyper-space", "f*cking", "arVId", "Caf\u00e9"};
        StringNormalizer lower = StringNormalizers.LOWER_CASE;
        StringNormalizer noAccents = StringNormalizers.NO_ACCENTS;
        for (String word : words) {
            System.out.printf("%s \t--> %s --> %s --> %s\n", word, lower.normalize(word), noAccents.normalize(word), lower.compose(noAccents).apply(word));
        }
    }

    @Test
    public void demoPorter(){
        System.out.println("\nDemo Porter stemmer");
        String[] words = {"caresses", "flies", "dies", "mules", "denied",
                "died", "agreed", "owned", "humbled", "sized",
                "meeting", "stating", "siezing", "itemization",
                "sensational", "traditional", "reference", "colonizer",
                "plotted"};

        PorterStemmer stemmer = new PorterStemmer();

        for (String word : words) {
            System.out.printf("%s \t--> %s\n", word, stemmer.normalize(word));
        }
    }
}
