package uk.gov.hmcts.darts.utils;

import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.UnmarshallerHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.xml.sax.XMLFilter;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.common.exceptions.soap.documentum.ServiceExceptionType;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
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
@SpringBootTest(classes = RedisConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationBase {

    protected EventApiStub theEventApi = new EventApiStub();
    protected DailyListApiStub dailyListApiStub = new DailyListApiStub();
    protected GetCourtLogsApiStub courtLogsApi = new GetCourtLogsApiStub();
    protected PostCourtLogsApiStub postCourtLogsApi = new PostCourtLogsApiStub();
    protected GetCasesApiStub getCasesApiStub = new GetCasesApiStub();
    protected AuthenticationStub authenticationStub = new AuthenticationStub();

    protected String DEFAULT_USERNAME = "some-user";

    protected String DEFAULT_PASSWORD = "password";

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

    protected ContextRegistryClient getContextClient() {
        if (contextClients.values().size() > 0) {
            return contextClients.values().iterator().next();
        }

        throw new AssertionFailedError("Don't have a context registry client!!!");
    }
}
