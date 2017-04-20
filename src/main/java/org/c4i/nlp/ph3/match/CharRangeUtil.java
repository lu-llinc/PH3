package org.c4i.nlp.ph3.match;

import org.apache.commons.lang3.CharSet;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * http://jrgraphix.net/r/Unicode/
 * @author Arvid Halma
 * @version 20-04-2017 - 2:11
 */
public class CharRangeUtil {

    public static Map<String, char[]> RANGES = new TreeMap<>();

    static {
        // custom subranges from "Basic Latin"
        RANGES.put("Basic Latin Uppercase", new char[]{'A', 'Z'});
        RANGES.put("Basic Latin Lowercase", new char[]{'a', 'z'});
        RANGES.put("Basic Latin Accents", new char[]{'\u00c0', '\u00ff'});
        RANGES.put("Digits", new char[]{'0', '9'});


        // official unicode
        RANGES.put("Basic Latin", new char[]{'\u0002', '\u007F'});
        RANGES.put("Latin-1 Supplement", new char[]{'\u00A0', '\u00FF'});
        RANGES.put("Latin Extended-A", new char[]{'\u0010', '\u017F'});
        RANGES.put("Latin Extended-B", new char[]{'\u0018', '\u024F'});
        RANGES.put("IPA Extensions", new char[]{'\u0025', '\u02AF'});
        RANGES.put("Spacing Modifier Letters", new char[]{'\u002B', '\u02FF'});
        RANGES.put("Combining Diacritical Marks", new char[]{'\u0030', '\u036F'});
        RANGES.put("Greek and Coptic", new char[]{'\u0037', '\u03FF'});
        RANGES.put("Cyrillic", new char[]{'\u0040', '\u04FF'});
        RANGES.put("Cyrillic Supplementary", new char[]{'\u0050', '\u052F'});
        RANGES.put("Armenian", new char[]{'\u0053', '\u058F'});
        RANGES.put("Hebrew", new char[]{'\u0059', '\u05FF'});
        RANGES.put("Arabic", new char[]{'\u0060', '\u06FF'});
        RANGES.put("Syriac", new char[]{'\u0070', '\u074F'});
        RANGES.put("Thaana", new char[]{'\u0078', '\u07BF'});
        RANGES.put("Devanagari", new char[]{'\u0090', '\u097F'});
        RANGES.put("Bengali", new char[]{'\u0098', '\u09FF'});
        RANGES.put("Gurmukhi", new char[]{'\u00A0', '\u0A7F'});
        RANGES.put("Gujarati", new char[]{'\u00A8', '\u0AFF'});
        RANGES.put("Oriya", new char[]{'\u00B0', '\u0B7F'});
        RANGES.put("Tamil", new char[]{'\u00B8', '\u0BFF'});
        RANGES.put("Telugu", new char[]{'\u00C0', '\u0C7F'});
        RANGES.put("Kannada", new char[]{'\u00C8', '\u0CFF'});
        RANGES.put("Malayalam", new char[]{'\u00D0', '\u0D7F'});
        RANGES.put("Sinhala", new char[]{'\u00D8', '\u0DFF'});
        RANGES.put("Thai", new char[]{'\u00E0', '\u0E7F'});
        RANGES.put("Lao", new char[]{'\u00E8', '\u0EFF'});
        RANGES.put("Tibetan", new char[]{'\u00F0', '\u0FFF'});
        RANGES.put("Myanmar", new char[]{'\u0100', '\u109F'});
        RANGES.put("Georgian", new char[]{'\u010A', '\u10FF'});
        RANGES.put("Hangul Jamo", new char[]{'\u0110', '\u11FF'});
        RANGES.put("Ethiopic", new char[]{'\u0120', '\u137F'});
        RANGES.put("Cherokee", new char[]{'\u013A', '\u13FF'});
        RANGES.put("Unified Canadian Aboriginal Syllabics", new char[]{'\u0140', '\u167F'});
        RANGES.put("Ogham", new char[]{'\u0168', '\u169F'});
        RANGES.put("Runic", new char[]{'\u016A', '\u16FF'});
        RANGES.put("Tagalog", new char[]{'\u0170', '\u171F'});
        RANGES.put("Hanunoo", new char[]{'\u0172', '\u173F'});
        RANGES.put("Buhid", new char[]{'\u0174', '\u175F'});
        RANGES.put("Tagbanwa", new char[]{'\u0176', '\u177F'});
        RANGES.put("Khmer", new char[]{'\u0178', '\u17FF'});
        RANGES.put("Mongolian", new char[]{'\u0180', '\u18AF'});
        RANGES.put("Limbu", new char[]{'\u0190', '\u194F'});
        RANGES.put("Tai Le", new char[]{'\u0195', '\u197F'});
        RANGES.put("Khmer Symbols", new char[]{'\u019E', '\u19FF'});
        RANGES.put("Phonetic Extensions", new char[]{'\u01D0', '\u1D7F'});
        RANGES.put("Latin Extended Additional", new char[]{'\u01E0', '\u1EFF'});
        RANGES.put("Greek Extended", new char[]{'\u01F0', '\u1FFF'});
        RANGES.put("General Punctuation", new char[]{'\u0200', '\u206F'});
        RANGES.put("Superscripts and Subscripts", new char[]{'\u0207', '\u209F'});
        RANGES.put("Currency Symbols", new char[]{'\u020A', '\u20CF'});
        RANGES.put("Combining Diacritical Marks for Symbols", new char[]{'\u020D', '\u20FF'});
        RANGES.put("Letterlike Symbols", new char[]{'\u0210', '\u214F'});
        RANGES.put("Number Forms", new char[]{'\u0215', '\u218F'});
        RANGES.put("Arrows", new char[]{'\u0219', '\u21FF'});
        RANGES.put("Mathematical Operators", new char[]{'\u0220', '\u22FF'});
        RANGES.put("Miscellaneous Technical", new char[]{'\u0230', '\u23FF'});
        RANGES.put("Control Pictures", new char[]{'\u0240', '\u243F'});
        RANGES.put("Optical Character Recognition", new char[]{'\u0244', '\u245F'});
        RANGES.put("Enclosed Alphanumerics", new char[]{'\u0246', '\u24FF'});
        RANGES.put("Box Drawing", new char[]{'\u0250', '\u257F'});
        RANGES.put("Block Elements", new char[]{'\u0258', '\u259F'});
        RANGES.put("Geometric Shapes", new char[]{'\u025A', '\u25FF'});
        RANGES.put("Miscellaneous Symbols", new char[]{'\u0260', '\u26FF'});
        RANGES.put("Dingbats", new char[]{'\u0270', '\u27BF'});
        RANGES.put("Miscellaneous Mathematical Symbols-A", new char[]{'\u027C', '\u27EF'});
        RANGES.put("Supplemental Arrows-A", new char[]{'\u027F', '\u27FF'});
        RANGES.put("Braille Patterns", new char[]{'\u0280', '\u28FF'});
        RANGES.put("Supplemental Arrows-B", new char[]{'\u0290', '\u297F'});
        RANGES.put("Miscellaneous Mathematical Symbols-B", new char[]{'\u0298', '\u29FF'});
        RANGES.put("Supplemental Mathematical Operators", new char[]{'\u02A0', '\u2AFF'});
        RANGES.put("Miscellaneous Symbols and Arrows", new char[]{'\u02B0', '\u2BFF'});
        RANGES.put("CJK Radicals Supplement", new char[]{'\u02E8', '\u2EFF'});
        RANGES.put("Kangxi Radicals", new char[]{'\u02F0', '\u2FDF'});
        RANGES.put("Ideographic Description Characters", new char[]{'\u02FF', '\u2FFF'});
        RANGES.put("CJK Symbols and Punctuation", new char[]{'\u0300', '\u303F'});
        RANGES.put("Hiragana", new char[]{'\u0304', '\u309F'});
        RANGES.put("Katakana", new char[]{'\u030A', '\u30FF'});
        RANGES.put("Bopomofo", new char[]{'\u0310', '\u312F'});
        RANGES.put("Hangul Compatibility Jamo", new char[]{'\u0313', '\u318F'});
        RANGES.put("Kanbun", new char[]{'\u0319', '\u319F'});
        RANGES.put("Bopomofo Extended", new char[]{'\u031A', '\u31BF'});
        RANGES.put("Katakana Phonetic Extensions", new char[]{'\u031F', '\u31FF'});
        RANGES.put("Enclosed CJK Letters and Months", new char[]{'\u0320', '\u32FF'});
        RANGES.put("CJK Compatibility", new char[]{'\u0330', '\u33FF'});
        RANGES.put("CJK Unified Ideographs Extension A", new char[]{'\u0340', '\u4DBF'});
        RANGES.put("Yijing Hexagram Symbols", new char[]{'\u04DC', '\u4DFF'});
        RANGES.put("CJK Unified Ideographs", new char[]{'\u04E0', '\u9FFF'});
        RANGES.put("Yi Syllables", new char[]{'\u0A00', '\uA48F'});
        RANGES.put("Yi Radicals", new char[]{'\u0A49', '\uA4CF'});
        RANGES.put("Hangul Syllables", new char[]{'\u0AC0', '\uD7AF'});
        RANGES.put("High Surrogates", new char[]{'\u0D80', '\uDB7F'});
        RANGES.put("High Private Use Surrogates", new char[]{'\u0DB8', '\uDBFF'});
        RANGES.put("Low Surrogates", new char[]{'\u0DC0', '\uDFFF'});
        RANGES.put("Private Use Area", new char[]{'\u0E00', '\uF8FF'});
        RANGES.put("CJK Compatibility Ideographs", new char[]{'\u0F90', '\uFAFF'});
        RANGES.put("Alphabetic Presentation Forms", new char[]{'\u0FB0', '\uFB4F'});
        RANGES.put("Arabic Presentation Forms-A", new char[]{'\u0FB5', '\uFDFF'});
        RANGES.put("Variation Selectors", new char[]{'\u0FE0', '\uFE0F'});
        RANGES.put("Combining Half Marks", new char[]{'\u0FE2', '\uFE2F'});
        RANGES.put("CJK Compatibility Forms", new char[]{'\u0FE3', '\uFE4F'});
        RANGES.put("Small Form Variants", new char[]{'\u0FE5', '\uFE6F'});
        RANGES.put("Arabic Presentation Forms-B", new char[]{'\u0FE7', '\uFEFF'});
        RANGES.put("Halfwidth and Fullwidth Forms", new char[]{'\u0FF0', '\uFFEF'});
        RANGES.put("Specials", new char[]{'\u0FFF', '\uFFFF'});

        // out of range?
        //RANGES.put("Linear B Syllabary", new char[]{'\u1000', '\u1007F'});
        //RANGES.put("Linear B Ideograms", new char[]{'\u1008', '\u100FF'});
        //RANGES.put("Aegean Numbers", new char[]{'\u1010', '\u1013F'});
        //RANGES.put("Old Italic", new char[]{'\u1030', '\u1032F'});
        //RANGES.put("Gothic", new char[]{'\u1033', '\u1034F'});
        //RANGES.put("Ugaritic", new char[]{'\u1038', '\u1039F'});
        //RANGES.put("Deseret", new char[]{'\u1040', '\u1044F'});
        //RANGES.put("Shavian", new char[]{'\u1045', '\u1047F'});
        //RANGES.put("Osmanya", new char[]{'\u1048', '\u104AF'});
        //RANGES.put("Cypriot Syllabary", new char[]{'\u1080', '\u1083F'});
        //RANGES.put("Byzantine Musical Symbols", new char[]{'\u1D00', '\u1D0FF'});
        //RANGES.put("Musical Symbols", new char[]{'\u1D10', '\u1D1FF'});
        //RANGES.put("Tai Xuan Jing Symbols", new char[]{'\u1D30', '\u1D35F'});
        //RANGES.put("Mathematical Alphanumeric Symbols", new char[]{'\u1D40', '\u1D7FF'});
        //RANGES.put("CJK Unified Ideographs Extension B", new char[]{'\u2000', '\u2A6DF'});
        //RANGES.put("CJK Compatibility Ideographs Supplement", new char[]{'\u2F80', '\u2FA1F'});
        //RANGES.put("Tags", new char[]{'\uE000', '\uE007F'});
    }

    public static char[][] LATIN = {
            RANGES.get("Basic Latin Uppercase"),
            RANGES.get("Basic Latin Lowercase"),
            RANGES.get("Basic Latin Accents"),
            RANGES.get("Digits"),
    };
}
