package uk.gov.hmcts.darts.testutils;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.nimbusds.jose.JOSEException;
import jakarta.xml.bind.JAXBException;
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
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.conf.RedisConfiguration;
import uk.gov.hmcts.darts.testutils.stub.DailyListApiStub;
import uk.gov.hmcts.darts.testutils.stub.EventApiStub;
import uk.gov.hmcts.darts.testutils.stub.GetCasesApiStub;
import uk.gov.hmcts.darts.testutils.stub.GetCourtLogsApiStub;
import uk.gov.hmcts.darts.testutils.stub.PostCasesApiStub;
import uk.gov.hmcts.darts.testutils.stub.PostCourtLogsApiStub;
import uk.gov.hmcts.darts.testutils.stub.TokenStub;
import uk.gov.hmcts.darts.workflow.command.Command;
import uk.gov.hmcts.darts.workflow.command.CommandHolder;
import uk.gov.hmcts.darts.workflow.command.DeployRedisCommand;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.Map;

@ImportAutoConfiguration({FeignAutoConfiguration.class})
@ComponentScan("uk.gov.hmcts.darts")
@ActiveProfiles("int-test")
@AutoConfigureWireMock(port = 8090)
@SpringBootTest(classes = RedisConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {"DARTS_SOAP_REQUEST_LOG_LEVEL=TRACE", "DARTS_LOG_LEVEL=TRACE"})
public class IntegrationBase implements CommandHolder {

    protected PostCasesApiStub postCasesApiStub = new PostCasesApiStub();
    protected EventApiStub theEventApi = new EventApiStub();
    protected DailyListApiStub dailyListApiStub = new DailyListApiStub();
    protected GetCourtLogsApiStub courtLogsApi = new GetCourtLogsApiStub();
    protected PostCourtLogsApiStub postCourtLogsApi = new PostCourtLogsApiStub();
    protected GetCasesApiStub getCasesApiStub = new GetCasesApiStub();
    protected AuthenticationAssertion authenticationStub = new AuthenticationAssertion();
    protected MemoryLogAppender logAppender = LogUtil.getMemoryLogger();

    protected static final String DEFAULT_HEADER_USERNAME = "some-user";

    protected static final String DEFAULT_HEADER_PASSWORD = "password";

    protected static final String DEFAULT_REGISTER_USERNAME = "user";

    protected static final String DEFAULT_REGISTER_PASSWORD = "pass";

    @Value("${local.server.port}")
    protected int port;

    @Autowired
    private Map<String, ContextRegistryClient> contextClients;

    private static String localhost;

    @Autowired
    protected RedisTemplate<String, Object> template;

    @Autowired
    protected SecurityProperties securityProperties;

    protected TokenStub tokenStub = new TokenStub();

    static {
        try {
            localhost = InetAddress.getByName("localhost").getHostAddress();
        } catch (UnknownHostException he) {
            localhost = "unknown";
        }
    }

    @BeforeEach
    void clearStubs() {
        template.getConnectionFactory().getConnection().flushAll();

        WireMock.reset();

        // populate the jkws keys endpoint with a global public key
        tokenStub.stubExternalJwksKeys(DartsTokenGenerator.getGlobalKey());

        logAppender.reset();
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
        return getIp() + ":" + port;
    }

    public static String getIp() {
        String ipaddressStr = null;
        try {
            Enumeration<NetworkInterface> networkEnum = NetworkInterface.getNetworkInterfaces();
            while (networkEnum.hasMoreElements()) {
                NetworkInterface networkInterfaces = networkEnum.nextElement();
                Enumeration<InetAddress> ipaddressesOfNic = networkInterfaces.getInetAddresses();
                while (ipaddressesOfNic.hasMoreElements()) {
                    InetAddress ipaddress = ipaddressesOfNic.nextElement();
                    if (!ipaddress.isLoopbackAddress() && ipaddress.getHostAddress().contains(".")) {
                        ipaddressStr = ipaddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException s) {
            ipaddressStr = localhost;
        }

        return ipaddressStr;
    }

    protected ContextRegistryClient getContextClient() {
        if (!contextClients.isEmpty()) {
            return contextClients.values().iterator().next();
        }

        throw new AssertionFailedError("Don't have a context registry client!!!");
    }

    @Override
    public Command getCommand() {
        return new DeployRedisCommand();
    }

    @Override
    public void setCommand(Command command) {

    }

    @SuppressWarnings({"PMD.DoNotUseThreads"})
    public String runOperationExpectingJwksRefresh(String token, JkwsRefreshableRunnable runnable)
        throws InterruptedException, IOException, JAXBException, JOSEException {
        // make sure we have left it enough time for the refresh to place
        Thread.sleep(securityProperties.getJwksCacheRefreshPeriod().toMillis() + Duration.of(1, ChronoUnit.SECONDS).toMillis());
        return runnable.run(token);
    }

    @FunctionalInterface
    public interface JkwsRefreshableRunnable {
        String run(String token) throws InterruptedException, IOException, JAXBException, JOSEException;
    }


}