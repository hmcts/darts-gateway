package uk.gov.hmcts.darts.cases.impl;

import com.service.mojdarts.synapps.com.caseinfo.CaseStructure;
import com.service.mojdarts.synapps.com.caseinfo.CitizenNameStructure;
import com.service.mojdarts.synapps.com.caseinfo.CourtHouseStructure;
import com.service.mojdarts.synapps.com.caseinfo.DefendantStructure;
import com.service.mojdarts.synapps.com.caseinfo.DefendantsStructure;
import com.service.mojdarts.synapps.com.caseinfo.PersonalDetailsStructure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.darts.common.client.CasesClient;
import uk.gov.hmcts.darts.model.cases.AddCaseRequest;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseInfoRouteImplTest {

    private static final String SCHEMA_PATH = "src/main/resources/schemas/xhibit/CaseInfo.xsd";
    @Mock
    private XmlValidator xmlValidator;
    @Mock
    private CasesClient casesClient;

    @InjectMocks
    @Spy
    private CaseInfoRouteImpl caseInfoRoute;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(caseInfoRoute, "schemaPath", SCHEMA_PATH);
        ReflectionTestUtils.setField(caseInfoRoute, "validate", true);
    }

    @Test
    void handle_shouldValidate_whenValidateIsTrue() {
        ReflectionTestUtils.setField(caseInfoRoute, "validate", true);

        try (MockedStatic<XmlParser> xmlParserMockedStatic = mockStatic(XmlParser.class)) {
            CaseInfoRouteImpl.NewCaseMessage newCaseMessage = mock(CaseInfoRouteImpl.NewCaseMessage.class);
            CaseStructure caseStructure = mock(CaseStructure.class);
            when(newCaseMessage.getCase()).thenReturn(caseStructure);

            xmlParserMockedStatic.when(() -> XmlParser.unmarshal(anyString(), any()))
                .thenReturn(newCaseMessage);
            AddCaseRequest addCaseRequest = mock(AddCaseRequest.class);
            doReturn(addCaseRequest).when(caseInfoRoute).mapToAddCaseRequest(any());

            String document = "some-document";
            String type = "NEWCASE";


            assertThat(caseInfoRoute.handle(document, type))
                .hasFieldOrPropertyWithValue("code", CodeAndMessage.OK.getCode())
                .hasFieldOrPropertyWithValue("message", CodeAndMessage.OK.getMessage());

            verify(caseInfoRoute).mapToAddCaseRequest(caseStructure);
            verify(casesClient).casesAddDocumentPost(addCaseRequest);
            verify(xmlValidator).validate(document, SCHEMA_PATH);
        }
    }

    @Test
    void handle_shouldNotValidate_whenValidateIsFalse() {
        ReflectionTestUtils.setField(caseInfoRoute, "validate", false);

        try (MockedStatic<XmlParser> xmlParserMockedStatic = mockStatic(XmlParser.class)) {
            CaseInfoRouteImpl.NewCaseMessage newCaseMessage = mock(CaseInfoRouteImpl.NewCaseMessage.class);
            CaseStructure caseStructure = mock(CaseStructure.class);
            when(newCaseMessage.getCase()).thenReturn(caseStructure);

            xmlParserMockedStatic.when(() -> XmlParser.unmarshal(anyString(), any()))
                .thenReturn(newCaseMessage);
            AddCaseRequest addCaseRequest = mock(AddCaseRequest.class);
            doReturn(addCaseRequest).when(caseInfoRoute).mapToAddCaseRequest(any());

            String document = "some-document";
            String type = "NEWCASE";


            assertThat(caseInfoRoute.handle(document, type))
                .hasFieldOrPropertyWithValue("code", CodeAndMessage.OK.getCode())
                .hasFieldOrPropertyWithValue("message", CodeAndMessage.OK.getMessage());

            verify(caseInfoRoute).mapToAddCaseRequest(caseStructure);
            verify(casesClient).casesAddDocumentPost(addCaseRequest);
            verifyNoInteractions(xmlValidator);
        }

    }

    @Test
    void handle_shouldThrowException_whenTypeIsNotSupported() {
        String document = "some-document";
        String type = "UNKNOWN";

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                                                               () -> caseInfoRoute.handle(document, type));
        assertThat(exception.getMessage()).isEqualTo("Unsupported type: " + type);

        verifyNoInteractions(casesClient);
    }

    @Test
    void handle_shouldCallCasesClient_whenTypeIsNewCase() {
        try (MockedStatic<XmlParser> xmlParserMockedStatic = mockStatic(XmlParser.class)) {
            CaseInfoRouteImpl.NewCaseMessage newCaseMessage = mock(CaseInfoRouteImpl.NewCaseMessage.class);
            CaseStructure caseStructure = mock(CaseStructure.class);
            when(newCaseMessage.getCase()).thenReturn(caseStructure);

            xmlParserMockedStatic.when(() -> XmlParser.unmarshal(anyString(), any()))
                .thenReturn(newCaseMessage);
            AddCaseRequest addCaseRequest = mock(AddCaseRequest.class);
            doReturn(addCaseRequest).when(caseInfoRoute).mapToAddCaseRequest(any());

            String document = "some-document";
            String type = "NEWCASE";

            assertThat(caseInfoRoute.handle(document, type))
                .hasFieldOrPropertyWithValue("code", CodeAndMessage.OK.getCode())
                .hasFieldOrPropertyWithValue("message", CodeAndMessage.OK.getMessage());

            verify(caseInfoRoute).mapToAddCaseRequest(caseStructure);
            verify(casesClient).casesAddDocumentPost(addCaseRequest);
            verify(xmlValidator).validate(document, SCHEMA_PATH);
            xmlParserMockedStatic.verify(() -> XmlParser.unmarshal(document, CaseInfoRouteImpl.NewCaseMessage.class));
        }
    }

    @Test
    void handle_shouldCallCasesClient_whenTypeIsUpdateCase() {
        try (MockedStatic<XmlParser> xmlParserMockedStatic = mockStatic(XmlParser.class)) {
            CaseInfoRouteImpl.UpdatedCaseMessage updatedCaseMessage = mock(CaseInfoRouteImpl.UpdatedCaseMessage.class);
            CaseStructure caseStructure = mock(CaseStructure.class);
            when(updatedCaseMessage.getCase()).thenReturn(caseStructure);

            xmlParserMockedStatic.when(() -> XmlParser.unmarshal(anyString(), any()))
                .thenReturn(updatedCaseMessage);
            AddCaseRequest addCaseRequest = mock(AddCaseRequest.class);
            doReturn(addCaseRequest).when(caseInfoRoute).mapToAddCaseRequest(any());
            String document = "some-document";
            String type = "UPDCASE";

            assertThat(caseInfoRoute.handle(document, type))
                .hasFieldOrPropertyWithValue("code", CodeAndMessage.OK.getCode())
                .hasFieldOrPropertyWithValue("message", CodeAndMessage.OK.getMessage());

            verify(caseInfoRoute).mapToAddCaseRequest(caseStructure);
            verify(casesClient).casesAddDocumentPost(addCaseRequest);
            verify(xmlValidator).validate(document, SCHEMA_PATH);
            xmlParserMockedStatic.verify(() -> XmlParser.unmarshal(document, CaseInfoRouteImpl.UpdatedCaseMessage.class));
        }
    }

    @Test
    void mapToAddCaseRequest_shouldMapCaseStructureToRequest() {
        CourtHouseStructure courtHouseStructure = new CourtHouseStructure();
        courtHouseStructure.setCourtHouseName("Court House");
        CaseStructure caseStructure = new CaseStructure();
        caseStructure.setCourt(courtHouseStructure);
        caseStructure.setCaseNumber("1234");
        DefendantsStructure defendantsStructure = new DefendantsStructure();
        defendantsStructure.getDefendant().addAll(
            List.of(createDefendantStructure(List.of("John"), "Doe"),
                    createDefendantStructure(List.of("Jane"), "Smith"),
                    createDefendantStructure(List.of(" Ben "), " Smith "),
                    createDefendantStructure(List.of(" Jake "), null),
                    createDefendantStructure(List.of(" Jake ", "orange"), "joe"))
        );
        caseStructure.setDefendants(defendantsStructure);

        AddCaseRequest addCaseRequest = caseInfoRoute.mapToAddCaseRequest(caseStructure);
        assertThat(addCaseRequest).isNotNull();

        assertThat(addCaseRequest.getCourthouse()).isEqualTo("Court House");
        assertThat(addCaseRequest.getCaseNumber()).isEqualTo("1234");
        assertThat(addCaseRequest.getDefendants()).containsExactly("John Doe", "Jane Smith", "Ben Smith", "Jake", "Jake orange joe");
    }

    private DefendantStructure createDefendantStructure(List<String> foreName, String surname) {
        PersonalDetailsStructure personalDetailsStructure = new PersonalDetailsStructure();
        CitizenNameStructure citizenNameStructure = new CitizenNameStructure();
        citizenNameStructure.getCitizenNameForename().addAll(foreName);
        citizenNameStructure.setCitizenNameSurname(surname);
        personalDetailsStructure.setName(citizenNameStructure);
        DefendantStructure defendantStructure = new DefendantStructure();
        defendantStructure.setPersonalDetails(personalDetailsStructure);
        return defendantStructure;
    }

    @Test
    void mapToName_shouldTrimNames() {
        CitizenNameStructure citizenNameStructure = new CitizenNameStructure();
        citizenNameStructure.getCitizenNameForename().addAll(List.of(" John ", " doe "));
        citizenNameStructure.setCitizenNameSurname(" smith ");
        assertThat(caseInfoRoute.mapToName(citizenNameStructure)).isEqualTo("John doe smith");
    }

    @Test
    void mapToName_nullForenames_shouldReturnSurname() {
        CitizenNameStructure citizenNameStructure = new CitizenNameStructure();
        citizenNameStructure.setCitizenNameSurname(" smith ");
        assertThat(caseInfoRoute.mapToName(citizenNameStructure)).isEqualTo("smith");
    }

    @Test
    void mapToName_nullSurname_shouldReturnForenames() {
        CitizenNameStructure citizenNameStructure = new CitizenNameStructure();
        citizenNameStructure.getCitizenNameForename().addAll(List.of(" John ", " doe "));
        assertThat(caseInfoRoute.mapToName(citizenNameStructure)).isEqualTo("John doe");
    }

    @Test
    void mapToName_shouldIgnoreBlank() {
        CitizenNameStructure citizenNameStructure = new CitizenNameStructure();
        citizenNameStructure.getCitizenNameForename().addAll(List.of(" John ", " ", "", "", " doe "));
        citizenNameStructure.setCitizenNameSurname("");
        assertThat(caseInfoRoute.mapToName(citizenNameStructure)).isEqualTo("John doe");
    }
}
