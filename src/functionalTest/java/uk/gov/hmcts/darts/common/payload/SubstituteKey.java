package uk.gov.hmcts.darts.common.payload;

public enum SubstituteKey {
    USER_NAME("${USER}"), PASSWORD("${PASSWORD}"), TOKEN("${TOKEN}");

    final String key;

    SubstituteKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
