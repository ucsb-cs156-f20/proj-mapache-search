package edu.ucsb.mapache.documents;

import org.springframework.data.mongodb.core.mapping.Document;

import org.apache.commons.lang3.builder.EqualsBuilder;
import java.util.List;
import java.util.Objects;

@Document(collection = "messages")
public class Message {
    private String type;
    private String subtype;
    private String ts;
    private String user;
    private String text;
    private String channel;
    private SlackUserProfile user_profile;
    private List<MessageReactions> reactions;

    public Message() {
    }

    public Message(String type, String subtype, String ts, String user, String text, String channel,
            SlackUserProfile user_profile, List<MessageReactions> reactions) {
        this.type = type;
        this.subtype = subtype;
        this.ts = ts;
        this.user = user;
        this.text = text;
        this.channel = channel;
        this.user_profile = user_profile;
        this.reactions = reactions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public SlackUserProfile getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(SlackUserProfile user_profile) {
        this.user_profile = user_profile;
    }
    
    public List<MessageReactions> getMessage_reactions() {
        return reactions;
    }
    public void setMessage_reactions(List<MessageReactions> reactions) {
        this.reactions = reactions;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Message message = (Message) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(type, message.type);
        builder.append(subtype, message.subtype);
        builder.append(ts, message.ts);
        builder.append(user, message.user);
        builder.append(text, message.text);
        builder.append(channel, message.channel);
        builder.append(user_profile, message.user_profile);
        builder.append(reactions, message.reactions);
        return builder.build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subtype, ts, user, text, channel, user_profile, reactions);
    }

    @Override
    public String toString() {
        return "Message{" + "type='" + type + '\'' + ", subtype='" + subtype + '\'' + ", ts='" + ts + '\'' + ", user='"
                + user + '\'' + ", text='" + text + '\'' + ", channel='" + channel + '\'' + ", user_profile="
                + user_profile + ", reactions='" + reactions + '}';
    }
}

