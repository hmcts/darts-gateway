package uk.gov.hmcts.darts.utils;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.common.client.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMappingOperation;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;

@ImportAutoConfiguration({FeignAutoConfiguration.class})
@ComponentScan("uk.gov.hmcts.darts")
@ActiveProfiles("int-test")
@AutoConfigureWireMock(port = 8090)
@SpringBootTest(classes = RedisConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationBase {

    protected EventApiStub theEventApi = new EventApiStub();
    protected DailyListApiStub dailyListApiStub = new DailyListApiStub();
    protected GetCourtLogsApiStub courtLogsApi = new GetCourtLogsApiStub();
    protected PostCourtLogsApiStub postCourtLogsApi = new PostCourtLogsApiStub();
    protected GetCasesApiStub getCasesApiStub = new GetCasesApiStub();
    protected AuthenticationAssertion authenticationStub = new AuthenticationAssertion();

    protected static final String DEFAULT_USERNAME = "some-user";

    protected static final String DEFAULT_PASSWORD = "password";

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private Map<String, ContextRegistryClient> contextClients;

    @Autowired
    protected RedisTemplate<String, Object> template;

    @Autowired
    protected XmlParser parser;

    private static String localhost;

    static {
        try {
            localhost = InetAddress.getByName("localhost").getHostAddress();
        } catch (UnknownHostException he) {
            localhost = "unknown";
        }
    }

    @BeforeEach
    void clearStubs() {
        WireMock.reset();
        template.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @AfterAll
    static void afterAll() {
        RedisConfiguration.REDISSERVER.stop();
    }

    public URL getGatewayUri() throws MalformedURLException {
        String url = System.getenv("TEST_URL");
        if (System.getenv("TEST_URL") == null) {
            return new URL("http://localhost:" + port + "/service/darts/");
        } else {
            return new URL(url);
        }
    }

    public String getIpAndPort() {
        String ipaddressStr = null;
        try {
            Enumeration<NetworkInterface> networkEnum = NetworkInterface.getNetworkInterfaces();
            while (networkEnum.hasMoreElements()) {
                NetworkInterface networkInterfaces = (NetworkInterface) networkEnum.nextElement();
                Enumeration<InetAddress> ipaddressesOfNic = networkInterfaces.getInetAddresses();
                while (ipaddressesOfNic.hasMoreElements()) {
                    InetAddress ipaddress = (InetAddress) ipaddressesOfNic.nextElement();
                    if (!ipaddress.isLoopbackAddress() && ipaddress.getHostAddress().contains(".")) {
                        ipaddressStr = ipaddress.getHostAddress() + ":" + port;
                    }
                }
            }
        } catch (SocketException s) {
            ipaddressStr = localhost + ":" + port;
        }

        return ipaddressStr;
    }

    protected ProblemResponseMapping<?> getSpecificSoapErrorCode(String soapToExpect, APIProblemResponseMapper mapper) {
        for (ProblemResponseMappingOperation<?> responseMappingOperations : mapper.getResponseMappings()) {
            for (ProblemResponseMapping<?> responseMapping : responseMappingOperations.getProblemResponseMappingList()) {
                if (responseMapping.match(soapToExpect)) {
                    return responseMapping;
                }
            }
        }

        return null;
    }

    protected ContextRegistryClient getContextClient() {
        if (!contextClients.values().isEmpty()) {
            return contextClients.values().iterator().next();
        }

        throw new AssertionFailedError("Don't have a context registry client!!!");
    }

    protected void stopRedis() {
        RedisConfiguration.REDISSERVER.stop();
    }

    protected void startRedis() {
        RedisConfiguration.REDISSERVER.start();
    }
}
