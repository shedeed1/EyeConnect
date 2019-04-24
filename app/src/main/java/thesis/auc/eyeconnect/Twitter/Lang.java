package thesis.auc.eyeconnect.Twitter;

import java.util.Map;
import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum Lang {
    AR, EN, EN_GB, ID, PL, RU;

    @JsonValue
    public String toValue() {
        switch (this) {
        case AR: return "ar";
        case EN: return "en";
        case EN_GB: return "en-gb";
        case ID: return "id";
        case PL: return "pl";
        case RU: return "ru";
        }
        return null;
    }

    @JsonCreator
    public static Lang forValue(String value) throws IOException {
        if (value.equals("ar")) return AR;
        if (value.equals("en")) return EN;
        if (value.equals("en-gb")) return EN_GB;
        if (value.equals("id")) return ID;
        if (value.equals("pl")) return PL;
        if (value.equals("ru")) return RU;
        throw new IOException("Cannot deserialize Lang");
    }
}
