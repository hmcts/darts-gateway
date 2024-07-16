package uk.gov.hmcts.darts.common.client;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A junit provider to feed the Spring injected context registry gateway client implementations through to the test.
 */
public class ContextRegistryClientWrapperProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        // get the injected client
        Map<String, ContextRegistryClientWrapper> beans = SpringExtension.getApplicationContext(context).getBeansOfType(
            ContextRegistryClientWrapper.class);

        Arguments[] clientable = new Arguments[0];
        clientable = getCollection(beans.values()).toArray(clientable);

        return Stream.of(clientable);
    }

    private List<Arguments> getCollection(Collection<ContextRegistryClientWrapper> col) {
        List<Arguments> arguments = new ArrayList<>();
        for (ContextRegistryClientWrapper clientable : col) {
            arguments.add(Arguments.arguments(clientable));
        }

        return arguments;
    }
}
