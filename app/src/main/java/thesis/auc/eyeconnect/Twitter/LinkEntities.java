package thesis.auc.eyeconnect.Twitter;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

public class LinkEntities {
    private Object[] hashtags;
    private Object[] symbols;
    private UserMention[] userMentions;
    private URL[] urls;

    @JsonProperty("hashtags")
    public Object[] getHashtags() { return hashtags; }
    @JsonProperty("hashtags")
    public void setHashtags(Object[] value) { this.hashtags = value; }

    @JsonProperty("symbols")
    public Object[] getSymbols() { return symbols; }
    @JsonProperty("symbols")
    public void setSymbols(Object[] value) { this.symbols = value; }

    @JsonProperty("user_mentions")
    public UserMention[] getUserMentions() { return userMentions; }
    @JsonProperty("user_mentions")
    public void setUserMentions(UserMention[] value) { this.userMentions = value; }

    @JsonProperty("urls")
    public URL[] getUrls() { return urls; }
    @JsonProperty("urls")
    public void setUrls(URL[] value) { this.urls = value; }
}