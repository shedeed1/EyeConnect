package thesis.auc.eyeconnect.Twitter;

import java.util.Map;
import com.fasterxml.jackson.annotation.*;

public class Description {
    private URL[] urls;

    @JsonProperty("urls")
    public URL[] getUrls() { return urls; }
    @JsonProperty("urls")
    public void setUrls(URL[] value) { this.urls = value; }
}
