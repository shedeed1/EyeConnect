package thesis.auc.eyeconnect.Twitter;

import java.util.Map;
import com.fasterxml.jackson.annotation.*;

public class URL {
    private String url;
    private String expandedURL;
    private String displayURL;
    private long[] indices;

    @JsonProperty("url")
    public String getURL() { return url; }
    @JsonProperty("url")
    public void setURL(String value) { this.url = value; }

    @JsonProperty("expanded_url")
    public String getExpandedURL() { return expandedURL; }
    @JsonProperty("expanded_url")
    public void setExpandedURL(String value) { this.expandedURL = value; }

    @JsonProperty("display_url")
    public String getDisplayURL() { return displayURL; }
    @JsonProperty("display_url")
    public void setDisplayURL(String value) { this.displayURL = value; }

    @JsonProperty("indices")
    public long[] getIndices() { return indices; }
    @JsonProperty("indices")
    public void setIndices(long[] value) { this.indices = value; }
}
