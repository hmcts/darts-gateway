package uk.gov.hmcts.darts;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jorphan.collections.HashTree;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestContextRegistryPerformance {

    @Test
    public void testSendPerformanceTest() throws Exception {
        HashTree hashTree = SaveService.loadTree(new File(TestContextRegistryPerformance.class
                                                     .getClassLoader().getResource("Context Registry Requests.jmx").getFile()));

        StandardJMeterEngine engine = new StandardJMeterEngine();
        engine.configure(hashTree);

        engine.run();
    }
}
