package uk.gov.hmcts.darts.jmeter;

import lombok.extern.slf4j.Slf4j;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.Visualizer;

@Slf4j
public class JMeterAssertionResultListener implements Visualizer {
    private boolean assertFailure;

    public boolean anyFailures() {
        return assertFailure;
    }

    @Override
    public void add(SampleResult sampleResult) {
        for (AssertionResult result : sampleResult.getAssertionResults()) {
            if (!assertFailure && result.isFailure()) {
                log.error("Assertion failure " + result.getFailureMessage());
                assertFailure = true;
            }

            log.info("Response Message " + sampleResult.getResponseMessage());
            log.info("Test Duration Millis " + (sampleResult.getEndTime() - sampleResult.getStartTime()));
        }
    }

    @Override
    public boolean isStats() {
        return false;
    }
}