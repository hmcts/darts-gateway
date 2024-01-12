package uk.gov.hmcts.darts.utils;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

@ImportAutoConfiguration({FeignAutoConfiguration.class})
@ComponentScan("uk.gov.hmcts.darts")
@ActiveProfiles("int-test")
@AutoConfigureWireMock(port = 8090)
@SpringBootTest(classes = RedisConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationBase {

    protected EventApiStub theEventApi = new EventApiStub();
    protected DailyListApiStub dailyListApiStub = new DailyListApiStub();
    protected GetCourtLogsApiStub courtLogsApi = new GetCourtLogsApiStub();
    protected PostCourtLogsApiStub postCourtLogsApi = new PostCourtLogsApiStub();
    protected GetCasesApiStub getCasesApiStub = new GetCasesApiStub();

    @Autowired
    protected RedisTemplate<String, Object> template;

    private static String localhost;

    static {
        try {
            localhost = InetAddress.getByName("localhost").getHostAddress();
        } catch (UnknownHostException he) {
            localhost = "unknown";
        }
    }

    @Value("${server.port}")
    private int serverPort;

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
            return new URL("http://localhost:" + serverPort + "/service/darts/");
        } else {
            return new URL(url);
        }
    }

    public String getIpAndPort() {
        String ipaddressStr = null;
        try {
            Enumeration networkEnum = NetworkInterface.getNetworkInterfaces();
            while (networkEnum.hasMoreElements()) {
                NetworkInterface networkInterfaces = (NetworkInterface) networkEnum.nextElement();
                Enumeration ipaddressesOfNic = networkInterfaces.getInetAddresses();
                while (ipaddressesOfNic.hasMoreElements()) {
                    InetAddress ipaddress = (InetAddress) ipaddressesOfNic.nextElement();
                    if (!ipaddress.isLoopbackAddress() && ipaddress.getHostAddress().contains(".")) {
                        ipaddressStr = ipaddress.getHostAddress() + ":" + serverPort;
                    }
                }
            }
        } catch (SocketException s) {
            ipaddressStr = localhost + ":" + serverPort;
        }

        return ipaddressStr;
    }
}
