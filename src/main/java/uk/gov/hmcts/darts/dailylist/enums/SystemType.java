package uk.gov.hmcts.darts.dailylist.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum SystemType {

    CPP("CPPDL", "CPP"),
    XHIBIT("DL", "XHB");
    String type;
    String modernisedSystemType;

    public static Optional<SystemType> getByType(String type) {
        return Arrays.stream(values()).filter(systemType -> systemType.getType().equalsIgnoreCase(
            type)).findAny();
    }
}
