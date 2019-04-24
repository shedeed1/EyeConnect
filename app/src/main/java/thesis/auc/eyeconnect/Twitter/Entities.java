package thesis.auc.eyeconnect.Twitter;

import java.util.Map;
import com.fasterxml.jackson.annotation.*;

public class Entities {
    private Description url;
    private Description description;

    @JsonProperty("url")
    public Description getURL() { return url; }
    @JsonProperty("url")
    public void setURL(Description value) { this.url = value; }

    @JsonProperty("description")
    public Description getDescription() { return description; }
    @JsonProperty("description")
    public void setDescription(Description value) { this.description = value; }
}
