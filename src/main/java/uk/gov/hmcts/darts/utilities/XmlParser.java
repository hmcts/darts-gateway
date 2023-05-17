package uk.gov.hmcts.darts.utilities;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.soap.DartsException;
import uk.gov.hmcts.darts.soap.ErrorCodeAndMessage;

import java.io.StringReader;

@Service
public class XmlParser {

    public <T> T unmarshal(String xml, Class<T> clazz) {
        JAXBContext context;
        Object clazzInstance;
        try {
            context = JAXBContext.newInstance(clazz);
            clazzInstance = context
                .createUnmarshaller()
                .unmarshal(new StringReader(xml));
        } catch (JAXBException jaxbException) {
            throw new DartsException(jaxbException, ErrorCodeAndMessage.INVALID_XML);
        }

        return clazz.cast(clazzInstance);
    }
}
