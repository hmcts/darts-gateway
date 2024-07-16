package uk.gov.hmcts.darts.jmeter;

import lombok.extern.slf4j.Slf4j;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.Visualizer;

@Slf4j
public class JMeterAssertionResultListener implements Visualizer {
    private boolean assertFailure;

    private String responseString = "";
    
    public boolean anyFailures() {
        return assertFailure;
    }

    public String getFailureString() {
        return responseString;
    }

    @Override
    public void add(SampleResult sampleResult) {
        for (AssertionResult result : sampleResult.getAssertionResults()) {
            if (!assertFailure && result.isFailure()) {
                log.error("Assertion failure " + result.getFailureMessage());
                assertFailure = true;
                responseString = responseString.concat(responseString.concat(
                    "\n").concat(result.getFailureMessage()));
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
