package thesis.auc.eyeconnect.Twitter;

import java.util.Map;
import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum TranslatorType {
    NONE, REGULAR;

    @JsonValue
    public String toValue() {
        switch (this) {
        case NONE: return "none";
        case REGULAR: return "regular";
        }
        return null;
    }

    @JsonCreator
    public static TranslatorType forValue(String value) throws IOException {
        if (value.equals("none")) return NONE;
        if (value.equals("regular")) return REGULAR;
        throw new IOException("Cannot deserialize TranslatorType");
    }
}
