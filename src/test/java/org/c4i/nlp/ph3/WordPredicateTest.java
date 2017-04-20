package org.c4i.nlp.ph3;

import org.c4i.nlp.ph3.tokenize.WordPredicates;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Arvid
 * @version 11-7-2015 - 20:34
 */
public class WordPredicateTest {

    public static void main(String[] args) {

    }

    @Test
    public void testSensible(){
        String word;
        word = "aap";
        assertTrue(word, WordPredicates.wordOrNameLike().test(word));
        word = "Aap";
        assertTrue(word, WordPredicates.wordOrNameLike().test(word));
        word = "Aap Noot";
        assertTrue(word, WordPredicates.wordOrNameLike().test(word));
        word = "bam-boe";
        assertTrue(word, WordPredicates.wordOrNameLike().test(word));
        word = "bam-boe";
        assertTrue(word, WordPredicates.wordOrNameLike().test(word));
        word = "#!?";
        assertFalse(word, WordPredicates.wordOrNameLike().test(word));
    }


    @Test
    public void testRandomTrue(){
        String word;
        word = "sfasAFAsfasAFS";
        assertTrue(word, WordPredicates.randomString().test(word));
        word = "afsafs52hjk152k152";
        assertTrue(word, WordPredicates.randomString().test(word));
        word = "fasf__-fas_FAsF_AsfaS_FAS_F";
        assertTrue(word, WordPredicates.randomString().test(word));
        word = "plkjmfsdgs";
        assertTrue(word, WordPredicates.randomString().test(word));
    }

    @Test
    public void testRandomFalse(){
        String word;
        word = "aap";
        assertFalse(word, WordPredicates.randomString().test(word));
        word = "directdisplay";
        assertFalse(word, WordPredicates.randomString().test(word));
        word = "Directdisplay";
        assertFalse(word, WordPredicates.randomString().test(word));
        word = "DIRECTDISPLAY";
        assertFalse(word, WordPredicates.randomString().test(word));
        word = "xc100";
        assertFalse(word, WordPredicates.randomString().test(word));
        word = "500XP";
        assertFalse(word, WordPredicates.randomString().test(word));
    }
}
