package uk.gov.hmcts.darts.shutdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
//TODO Fix loging so we still get logs even after logging shutdown hook is triggered
public class GracefulShutdownHook
    implements Runnable, GracefulShutdownCallback {
    private final ServletWebServerApplicationContext applicationContext;

    @Override
    public void run() {
        setReadinessToFalse();
        delayShutdown();
        shutdownApplication();
    }

    void setReadinessToFalse() {
        log.info("Setting readiness for application to false, so the application doesn't receive new connections from now on.");
        GracefulShutdownHealthCheck probeControllers = applicationContext.getBean(
            "dartsGatewayGracefulShutdownHealthCheck", GracefulShutdownHealthCheck.class);
        probeControllers.setReady(false);
    }

    //Required for graceful shutdown. Health check needs to fail for a short time, so the load balancer stops sending new requests.
    @SuppressWarnings("PMD.DoNotUseThreads")
    void delayShutdown() {
        try {
            String waitTimeString = applicationContext.getBeanFactory().resolveEmbeddedValue("${darts-gateway.shutdown.wait-time:30s}");
            Duration waitTime = DurationStyle.detectAndParse(waitTimeString);
            log.info("Waiting for {} before shutdown SpringContext!", waitTime);
            Thread.sleep(waitTime.toMillis());
        } catch (InterruptedException e) {
            log.error("Error while gracefulshutdown Thread.sleep", e);
            Thread.currentThread().interrupt();
        }
    }

    void shutdownApplication() {
        log.info("Shutting down Application");
        //First shutdown the web server, so it stops accepting new connections
        applicationContext.getWebServer().shutDownGracefully(this);
        //Then shutdown application context in shutdown callback
    }

    @Override
    public void shutdownComplete(GracefulShutdownResult result) {
        applicationContext.close();
        log.info("Graceful shutdown complete: {}", result);
    }
}
