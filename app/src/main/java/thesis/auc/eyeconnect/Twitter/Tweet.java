package thesis.auc.eyeconnect.Twitter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tweet {
    private String createdAt;
    private double id;
    private String idStr;
    private String text;
    private boolean truncated;
    private LinkEntities entities;
    private String source;
    private Object inReplyToStatusID;
    private Object inReplyToStatusIDStr;
    private Object inReplyToUserID;
    private Object inReplyToUserIDStr;
    private Object inReplyToScreenName;
    private User user;
    private Object geo;
    private Object coordinates;
    private Object place;
    private Object contributors;
    private Tweet retweetedStatus;
    private boolean isQuoteStatus;
    private long retweetCount;
    private long favoriteCount;
    private boolean favorited;
    private boolean retweeted;
    private Boolean possiblySensitive;
    private String lang;

    @JsonProperty("created_at")
    public String getCreatedAt() { return createdAt; }
    @JsonProperty("created_at")
    public void setCreatedAt(String value) { this.createdAt = value; }

    @JsonProperty("id")
    public double getID() { return id; }
    @JsonProperty("id")
    public void setID(double value) { this.id = value; }

    @JsonProperty("id_str")
    public String getIDStr() { return idStr; }
    @JsonProperty("id_str")
    public void setIDStr(String value) { this.idStr = value; }

    @JsonProperty("text")
    public String getText() { return text; }
    @JsonProperty("text")
    public void setText(String value) { this.text = value; }

    @JsonProperty("truncated")
    public boolean getTruncated() { return truncated; }
    @JsonProperty("truncated")
    public void setTruncated(boolean value) { this.truncated = value; }

    @JsonProperty("entities")
    public LinkEntities getEntities() { return entities; }
    @JsonProperty("entities")
    public void setEntities(LinkEntities value) { this.entities = value; }

    @JsonProperty("source")
    public String getSource() { return source; }
    @JsonProperty("source")
    public void setEntities(String value) { this.source = value; }

    @JsonProperty("in_reply_to_status_id")
    public Object getInReplyToStatusID() { return inReplyToStatusID; }
    @JsonProperty("in_reply_to_status_id")
    public void setInReplyToStatusID(Object value) { this.inReplyToStatusID = value; }

    @JsonProperty("in_reply_to_status_id_str")
    public Object getInReplyToStatusIDStr() { return inReplyToStatusIDStr; }
    @JsonProperty("in_reply_to_status_id_str")
    public void setInReplyToStatusIDStr(Object value) { this.inReplyToStatusIDStr = value; }

    @JsonProperty("in_reply_to_user_id")
    public Object getInReplyToUserID() { return inReplyToUserID; }
    @JsonProperty("in_reply_to_user_id")
    public void setInReplyToUserID(Object value) { this.inReplyToUserID = value; }

    @JsonProperty("in_reply_to_user_id_str")
    public Object getInReplyToUserIDStr() { return inReplyToUserIDStr; }
    @JsonProperty("in_reply_to_user_id_str")
    public void setInReplyToUserIDStr(Object value) { this.inReplyToUserIDStr = value; }

    @JsonProperty("in_reply_to_screen_name")
    public Object getInReplyToScreenName() { return inReplyToScreenName; }
    @JsonProperty("in_reply_to_screen_name")
    public void setInReplyToScreenName(Object value) { this.inReplyToScreenName = value; }

    @JsonProperty("user")
    public User getUser() { return user; }
    @JsonProperty("user")
    public void setUser(User value) { this.user = value; }

    @JsonProperty("geo")
    public Object getGeo() { return geo; }
    @JsonProperty("geo")
    public void setGeo(Object value) { this.geo = value; }

    @JsonProperty("coordinates")
    public Object getCoordinates() { return coordinates; }
    @JsonProperty("coordinates")
    public void setCoordinates(Object value) { this.coordinates = value; }

    @JsonProperty("place")
    public Object getPlace() { return place; }
    @JsonProperty("place")
    public void setPlace(Object value) { this.place = value; }

    @JsonProperty("contributors")
    public Object getContributors() { return contributors; }
    @JsonProperty("contributors")
    public void setContributors(Object value) { this.contributors = value; }

    @JsonProperty("retweeted_status")
    public Tweet getRetweetedStatus() { return retweetedStatus; }
    @JsonProperty("retweeted_status")
    public void setRetweetedStatus(Tweet value) { this.retweetedStatus = value; }

    @JsonProperty("is_quote_status")
    public boolean getIsQuoteStatus() { return isQuoteStatus; }
    @JsonProperty("is_quote_status")
    public void setIsQuoteStatus(boolean value) { this.isQuoteStatus = value; }

    @JsonProperty("retweet_count")
    public long getRetweetCount() { return retweetCount; }
    @JsonProperty("retweet_count")
    public void setRetweetCount(long value) { this.retweetCount = value; }

    @JsonProperty("favorite_count")
    public long getFavoriteCount() { return favoriteCount; }
    @JsonProperty("favorite_count")
    public void setFavoriteCount(long value) { this.favoriteCount = value; }

    @JsonProperty("favorited")
    public boolean getFavorited() { return favorited; }
    @JsonProperty("favorited")
    public void setFavorited(boolean value) { this.favorited = value; }

    @JsonProperty("retweeted")
    public boolean getRetweeted() { return retweeted; }
    @JsonProperty("retweeted")
    public void setRetweeted(boolean value) { this.retweeted = value; }

    @JsonProperty("possibly_sensitive")
    public Boolean getPossiblySensitive() { return possiblySensitive; }
    @JsonProperty("possibly_sensitive")
    public void setPossiblySensitive(Boolean value) { this.possiblySensitive = value; }

    @JsonProperty("lang")
    public String getLang() { return lang; }
    @JsonProperty("lang")
    public void setLang(String value) { this.lang = value; }
}
