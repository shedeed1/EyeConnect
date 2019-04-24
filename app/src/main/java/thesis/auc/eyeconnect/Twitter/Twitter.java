package thesis.auc.eyeconnect.Twitter;

import java.util.Map;
import com.fasterxml.jackson.annotation.*;

public class Twitter {
    private User[] users;
    private long nextCursor;
    private String nextCursorStr;
    private long previousCursor;
    private String previousCursorStr;
    private Object totalCount;

    @JsonProperty("users")
    public User[] getUsers() { return users; }
    @JsonProperty("users")
    public void setUsers(User[] value) { this.users = value; }

    @JsonProperty("nex" +
            "t_cursor")
    public long getNextCursor() { return nextCursor; }
    @JsonProperty("next_cursor")
    public void setNextCursor(long value) { this.nextCursor = value; }

    @JsonProperty("next_cursor_str")
    public String getNextCursorStr() { return nextCursorStr; }
    @JsonProperty("next_cursor_str")
    public void setNextCursorStr(String value) { this.nextCursorStr = value; }

    @JsonProperty("previous_cursor")
    public long getPreviousCursor() { return previousCursor; }
    @JsonProperty("previous_cursor")
    public void setPreviousCursor(long value) { this.previousCursor = value; }

    @JsonProperty("previous_cursor_str")
    public String getPreviousCursorStr() { return previousCursorStr; }
    @JsonProperty("previous_cursor_str")
    public void setPreviousCursorStr(String value) { this.previousCursorStr = value; }

    @JsonProperty("total_count")
    public Object getTotalCount() { return totalCount; }
    @JsonProperty("total_count")
    public void setTotalCount(Object value) { this.totalCount = value; }
}
