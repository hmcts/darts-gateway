package uk.gov.hmcts.darts.utils.client.ctxt;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import uk.gov.hmcts.darts.utils.ApplicationContextProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A junit provider to feed the Spring injected context registry gateway client implementations through to the test.
 */
public class ContextRegistryClientProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

        // get the injected client
        Map<String, ContextRegistryClient> beans = ApplicationContextProvider.getApplicationContext().getBeansOfType(
            ContextRegistryClient.class);

        Arguments[] clientable = new Arguments[0];
        clientable = getCollection(beans.values()).toArray(clientable);

        return Stream.of(clientable);
    }

    private List<Arguments> getCollection(Collection<ContextRegistryClient> col) {
        List<Arguments> arguments = new ArrayList<>();
        for (ContextRegistryClient clientable : col) {
            arguments.add(Arguments.arguments(clientable));
        }

        return arguments;
    }
}
