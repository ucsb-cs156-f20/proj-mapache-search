package edu.ucsb.mapache.documents;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class ChannelPurpose {
    private String value;
    private String creator;

    public ChannelPurpose() {
    }

    public ChannelPurpose(String value, String creator) {
        this.value = value;
        this.creator = creator;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ChannelPurpose)) {
            return false;
        }
        ChannelPurpose cp = (ChannelPurpose) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(value, cp.getValue()).append(creator,cp.getCreator());
        return builder.build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, creator);
    }

    @Override
    public String toString() {
        return "{" + " value='" + getValue() + "'" + ", creator='" + getCreator() + "'" + "}";
    }

}

/*
 * ChannelPurpose json
 * 
 * "purpose": { "value": "", "creator": "", "last_set": { "$numberInt": "0" } }
 * 
 */