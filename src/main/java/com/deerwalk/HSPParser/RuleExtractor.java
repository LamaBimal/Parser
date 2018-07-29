package com.deerwalk.HSPParser;

import com.deerwalk.HSPParser.Segment.Element;
import com.deerwalk.HSPParser.lib.Constants;

import java.io.InputStream;
import java.util.*;

/**
 * Created by btamang on 5/23/18.
 */
public class RuleExtractor {

    private String separator = ";";
    private String commentSymbol = "#";

    private Map<String, List<Element>> segments = new HashMap<String, List<Element>>();

    public static RuleExtractor ruleExtractor = new RuleExtractor();

    private RuleExtractor(){}

    public static RuleExtractor getInstance(){
        return ruleExtractor;
    }
    public void extractLayout(InputStream inputStream) {

        segments.clear();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String row = scanner.nextLine();

            if (!row.startsWith(commentSymbol)) {

                String[] contents = row.split(separator);
                String key = contents[1];
                int position = Integer.parseInt(contents[2]);
                int length = Integer.parseInt(contents[3]);
                Element element = new Element(key, position, length);
                setElement(contents[0], element);
            }
        }
    }

    private void setElement(String name, Element element) {

        if (segments.containsKey(name)) {
            segments.get(name).add(element);
        } else {
            List<Element> elements = new ArrayList<>();
            elements.add(element);
            segments.put(name, elements);
        }
    }

    public Map getSegments() {
        return this.segments;
    }

    public List<Element> getElements(String segment, String layout) {


        /**
         *  For Both layout,
         *  Supplement Record starts with always "S" i.e 1st position.
         *  And 2nd position may be any alphanumeric character.
         * **/
        if (segment.startsWith("S") && !segment.equalsIgnoreCase("SC")) {
            segment = "S*";
        }

        /**
         *  For Dependent layout,
         *  Dependent Coverage Records starts with either C or DC.
         *
         *  Layout is compared because Insured Coverage Records starts with C.
         * **/
        if (segment.trim().equalsIgnoreCase("C") && layout.equalsIgnoreCase(Constants.Layout_DEPENDENT)) {
            segment = "DC";
        }

        /**
         *  For Dependent,
         *
         *
         * ***/

        if (segments.containsKey(segment)) {
            return this.segments.get(segment);
        } else {
            return new ArrayList<>();
        }

    }

    public List<Element> getElements(String segment){
        if (segments.containsKey(segment)) {
            return this.segments.get(segment);
        } else {
            return new ArrayList<>();
        }
    }
}
