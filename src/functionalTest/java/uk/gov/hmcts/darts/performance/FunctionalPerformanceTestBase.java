package uk.gov.hmcts.darts.performance;

import org.apache.jmeter.assertions.DurationAssertion;
import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.junit.jupiter.api.Assertions;
import uk.gov.hmcts.darts.FunctionalTestBase;
import uk.gov.hmcts.darts.jmeter.JMeterAssertionResultListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;


class FunctionalPerformanceTestBase extends FunctionalTestBase {

    @SuppressWarnings({"PMD.LooseCoupling", "PMD.AvoidThreadGroup"})
    /**
     * A method that can be used to exercise jmeter. The underlying test asserts against a 200 response as well as a specific response duration
     * for each request
     * @param numberOfThreads The number of threads to run
     * @param rampUpPeriodInMillis How quickly to ramp up in millis
     * @param durationInMillis The duration that will be asserted for each reqyest
     * @param webContext The web context that will be used
     */
    void testSendPerformanceTest(int numberOfThreads, int rampUpPeriodInMillis, int durationInMillis,
                                 String body, String webContext) throws MalformedURLException {
        String file = Objects.requireNonNull(ContextRegistryPerformanceTest.class.getClassLoader().getResource("jmeter.properties")).getFile();
        JMeterUtils.loadJMeterProperties(file);
        JMeterUtils.initLocale();

        // set the allowed duration
        DurationAssertion durationAssertion = new DurationAssertion();
        durationAssertion.setAllowedDuration(durationInMillis);

        // set accepted response code
        ResponseAssertion responseAssertion = new ResponseAssertion();
        responseAssertion.setTestFieldResponseCode();
        responseAssertion.addTestString("200");

        HeaderManager headerManager = new HeaderManager();
        headerManager.add(new Header("Content-Type", "text/xml"));
        headerManager.setName(JMeterUtils.getResString("header_manager_title")); // $NON-NLS-1$
        headerManager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        headerManager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());

        LoopController loopController = getLoopController();

        ListedHashTree testPlanTree = new ListedHashTree();
        ThreadGroup threadGroup = getThreadGroup(loopController, numberOfThreads, rampUpPeriodInMillis);

        TestPlan testPlan = getTestPlan(threadGroup);

        // add the sample configuration
        HashTree requestHashTree = new HashTree();
        HTTPSamplerProxy httpSampler = getHttpSamplerProxy(new URL(getDartsGatewayOperationUrl()), body, webContext);

        requestHashTree.add(httpSampler, headerManager);
        requestHashTree.add(httpSampler, durationAssertion);
        requestHashTree.add(httpSampler, responseAssertion);

        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(requestHashTree);

        // add the data collection
        JMeterAssertionResultListener assertVisualiser = new JMeterAssertionResultListener();
        Summariser summer = new Summariser("summary");

        ResultCollector logger = new ResultCollector(summer);
        logger.setListener(assertVisualiser);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        StandardJMeterEngine engine = new StandardJMeterEngine();

        // run the tests
        engine.configure(testPlanTree);
        engine.run();

        // assert on the assertion results
        Assertions.assertFalse(assertVisualiser.anyFailures(), assertVisualiser.getFailureString());
    }

    private static HTTPSamplerProxy getHttpSamplerProxy(URL uri, String body, String webContext) {
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setPath(webContext);
        httpSampler.setProtocol(uri.getProtocol());
        httpSampler.setDomain(uri.getHost());
        httpSampler.setPort(uri.getPort());
        httpSampler.setMethod("POST");
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        httpSampler.setPostBodyRaw(true);
        httpSampler.addNonEncodedArgument("body", body, "");
        return httpSampler;
    }

    private static ThreadGroup getThreadGroup(LoopController loopController, Integer threads, Integer rampUp) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Sample Thread Group");
        threadGroup.setNumThreads(threads);
        threadGroup.setRampUp(rampUp);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        return threadGroup;
    }

    private static TestPlan getTestPlan(ThreadGroup threadGroup) {
        TestPlan testPlan = new TestPlan("Sample Test Plan");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.addThreadGroup(threadGroup);
        return testPlan;
    }

    private static LoopController getLoopController() {
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();
        return loopController;
    }

}