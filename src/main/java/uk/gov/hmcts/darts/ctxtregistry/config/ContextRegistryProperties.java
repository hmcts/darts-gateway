package uk.gov.hmcts.darts.ctxtregistry.config;

import lombok.Getter;
import lombok.Setter;


public interface ContextRegistryProperties {
    String getTokenGenerate();

    boolean isMapTokenToSession();
}
