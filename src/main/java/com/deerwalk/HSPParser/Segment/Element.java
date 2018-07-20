package com.deerwalk.HSPParser.Segment;

/**
 * Created by btamang on 5/23/18.
 */
public class Element {
    public String name;
    public int length;
    public int position;

    public Element(String name, int pos, int len) {
        this.name = name;
        this.length = len;
        this.position = pos;
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return this.position;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
