package uk.gov.hmcts.darts.utils;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;

@ImportAutoConfiguration({FeignAutoConfiguration.class})
@ComponentScan("uk.gov.hmcts.darts")
@ActiveProfiles("int-test")
@AutoConfigureWireMock(port = 8090)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationBase {

    protected EventApiStub theEventApi = new EventApiStub();
    protected DailyListApiStub dailyListApiStub = new DailyListApiStub();
    protected GetCourtLogsApiStub courtLogsApi = new GetCourtLogsApiStub();
    protected PostCourtLogsApiStub postCourtLogsApi = new PostCourtLogsApiStub();
    protected GetCasesApiStub getCasesApiStub = new GetCasesApiStub();

    @Value("${server.port}")
    private int serverPort;

    @BeforeEach
    void clearStubs() {
        WireMock.reset();
    }

    public URL getGatewayUri() throws MalformedURLException {
        String url = System.getenv("TEST_URL");
        if (System.getenv("TEST_URL") == null) {
            return new URL("http://localhost:" + serverPort + "/service/darts");
        } else {
            return new URL(url);
        }
    }
}
