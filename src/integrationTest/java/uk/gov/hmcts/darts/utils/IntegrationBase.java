package uk.gov.hmcts.darts.utils;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@WebServiceServerTest
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@ComponentScan("uk.gov.hmcts.darts")
@ActiveProfiles("int-test")
@AutoConfigureWireMock(port = 8090)
public class IntegrationBase {

    protected EventApiStub theEventApi = new EventApiStub();
    protected DailyListApiStub dailyListApiStub = new DailyListApiStub();
    protected GetCourtLogsApiStub courtLogsApi = new GetCourtLogsApiStub();

    protected PostCourtLogsApiStub postCourtLogsApi = new PostCourtLogsApiStub();

    @BeforeEach
    void clearStubs() {
        WireMock.reset();
    }
}
