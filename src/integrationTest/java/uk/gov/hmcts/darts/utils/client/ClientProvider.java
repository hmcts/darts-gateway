package uk.gov.hmcts.darts.utils.client;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import uk.gov.hmcts.darts.utils.ApplicationContextProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ClientProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Map<String, DartsGatewayClientable> beans = ApplicationContextProvider.getApplicationContext().getBeansOfType(DartsGatewayClientable.class);

        Arguments[] clientable = new Arguments[0];
        clientable = getCollection(beans.values()).toArray(clientable);

        return Stream.of(clientable);
    }

    private List<Arguments> getCollection(Collection<DartsGatewayClientable> col)
    {
        List<Arguments> arguments = new ArrayList<>();
        for(DartsGatewayClientable clientable : col) {
            arguments.add(Arguments.arguments(clientable));
        }

        return arguments;
    }
}
