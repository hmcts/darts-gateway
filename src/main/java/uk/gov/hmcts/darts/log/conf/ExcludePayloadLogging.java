package uk.gov.hmcts.darts.log.conf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcludePayloadLogging {
    private String namespace;

    private String tag;

    private String type;
}
