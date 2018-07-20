package com.deerwalk.HSPParser.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by btamang on 5/23/18.
 */
public class Segment {

    public static ArrayList<String> loopRecordType = new ArrayList<String>();

    static {
        /**
         * Insured Coverage Type
         * **/
        loopRecordType.add("C");
        /**
         * Dependent coverage Type
         * **/
        loopRecordType.add("DC");
    }

    public String segmentName;
    public List<Element> elements;

    public Segment() {
        this.elements = new ArrayList<Element>();
    }

    public Segment(String segment) {
        this.segmentName = segment;
        this.elements = new ArrayList<Element>();
    }

    public void addElement(Element element) {
        this.elements.add(element);
    }

    public void addElements(List<Element> collection) {
        this.elements.addAll(collection);
    }

    public List<Element> getElements() {
        return elements;
    }

    public String getSegmentName() {
        return this.segmentName;
    }
}
