package uk.gov.hmcts.darts.addaudio;

import com.service.mojdarts.synapps.com.AddAudio;
import com.service.mojdarts.synapps.com.addaudio.Audio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.addaudio.validator.AddAudioFileValidator;
import uk.gov.hmcts.darts.addaudio.validator.AddAudioValidator;
import uk.gov.hmcts.darts.api.audio.AudiosApi;
import uk.gov.hmcts.darts.common.function.ConsumerWithIoException;
import uk.gov.hmcts.darts.common.multipart.SizeableInputSource;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.datastore.DataManagementConfiguration;
import uk.gov.hmcts.darts.datastore.DataManagementService;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;
import uk.gov.hmcts.darts.utilities.XmlParser;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddAudioRouteTest {

    @Mock
    private AudiosApi audiosClient;
    @Mock
    private AddAudioMapper addAudioMapper;
    @Mock
    private XmlWithFileMultiPartRequestHolder multiPartRequestHolder;
    @Mock
    private AddAudioValidator addAudioValidator;
    @Mock
    private AddAudioFileValidator multipartFileValidator;
    @Mock
    private DataManagementService dataManagementService;
    @Mock
    private DataManagementConfiguration dataManagementConfiguration;
    @Mock
    private LogApi logApi;

    @InjectMocks
    private AddAudioRoute addAudioRoute;


    @Test
    void route_onBlobStoreUploadFailure_shouldLogError() throws Exception {
        XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
        when(multiPartRequestHolder.getRequest()).thenReturn(Optional.of(request));
        AtomicBoolean firstCall = new AtomicBoolean(false);

        when(request.consumeFileBinaryStream(any())).thenAnswer(invocation -> {
            if (firstCall.get()) {
                ConsumerWithIoException<SizeableInputSource> consumer = invocation.getArgument(0);
                consumer.accept(mock(SizeableInputSource.class));
            } else {
                firstCall.set(true);
            }
            return true;
        });
        AddAudioMetadataRequest addAudioMetadataRequest = mock(AddAudioMetadataRequest.class);

        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-29T10:44:25.028498Z");
        OffsetDateTime endAt = OffsetDateTime.parse("2025-01-31T10:44:25.028526Z");

        when(addAudioMetadataRequest.getCourthouse()).thenReturn("courtHouse");
        when(addAudioMetadataRequest.getCourtroom()).thenReturn("courtroom");
        when(addAudioMetadataRequest.getStartedAt()).thenReturn(startAt);
        when(addAudioMetadataRequest.getEndedAt()).thenReturn(endAt);
        when(addAudioMetadataRequest.getCases()).thenReturn(List.of("case1", "case2"));
        when(addAudioMetadataRequest.getChecksum()).thenReturn("checksum");

        when(addAudioMapper.mapToDartsApi(any())).thenReturn(addAudioMetadataRequest);

        RuntimeException exception = new RuntimeException("Some exception");
        when(dataManagementService.saveBlobData(any(), any(), any()))
            .thenThrow(exception);

        AddAudio addAudio = mock(AddAudio.class);

        when(addAudio.getDocument()).thenReturn("");

        try (MockedStatic<XmlParser> xmlParserMockedStatic = mockStatic(XmlParser.class)) {

            xmlParserMockedStatic.when(() -> XmlParser.unmarshal(anyString(), any()))
                .thenReturn(mock(Audio.class));

            assertThrows(RuntimeException.class,
                         () -> addAudioRoute.route(addAudio));
        }

        verify(logApi)
            .failedToLinkAudioToCases(
                "courtHouse",
                "courtroom",
                startAt,
                endAt,
                List.of("case1", "case2"),
                "checksum",
                null
            );
        verify(dataManagementService, never())
            .deleteBlobData(any(), any());
    }

    @Test
    void route_onApiFailure_shouldDeleteBlobData() throws Exception {
        final String containerName = "container-name";
        when(dataManagementConfiguration.getInboundContainerName())
            .thenReturn(containerName);
        XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
        when(multiPartRequestHolder.getRequest()).thenReturn(Optional.of(request));
        AtomicBoolean firstCall = new AtomicBoolean(false);

        when(request.consumeFileBinaryStream(any())).thenAnswer(invocation -> {
            if (firstCall.get()) {
                ConsumerWithIoException<SizeableInputSource> consumer = invocation.getArgument(0);
                consumer.accept(mock(SizeableInputSource.class));
            } else {
                firstCall.set(true);
            }
            return true;
        });
        AddAudioMetadataRequest addAudioMetadataRequest = mock(AddAudioMetadataRequest.class);

        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-29T10:44:25.028498Z");
        OffsetDateTime endAt = OffsetDateTime.parse("2025-01-31T10:44:25.028526Z");

        when(addAudioMetadataRequest.getCourthouse()).thenReturn("courtHouse");
        when(addAudioMetadataRequest.getCourtroom()).thenReturn("courtroom");
        when(addAudioMetadataRequest.getStartedAt()).thenReturn(startAt);
        when(addAudioMetadataRequest.getEndedAt()).thenReturn(endAt);
        when(addAudioMetadataRequest.getCases()).thenReturn(List.of("case1", "case2"));
        when(addAudioMetadataRequest.getChecksum()).thenReturn("checksum");

        when(addAudioMapper.mapToDartsApi(any())).thenReturn(addAudioMetadataRequest);

        UUID blobUuid = UUID.randomUUID();
        when(dataManagementService.saveBlobData(any(), any(), any()))
            .thenReturn(blobUuid);

        RuntimeException exception = new RuntimeException("Some exception");
        when(audiosClient.addAudioMetaData(any()))
            .thenThrow(exception);

        AddAudio addAudio = mock(AddAudio.class);

        when(addAudio.getDocument()).thenReturn("");

        try (MockedStatic<XmlParser> xmlParserMockedStatic = mockStatic(XmlParser.class)) {

            xmlParserMockedStatic.when(() -> XmlParser.unmarshal(anyString(), any()))
                .thenReturn(mock(Audio.class));

            assertThrows(RuntimeException.class,
                         () -> addAudioRoute.route(addAudio));
        }
        verify(dataManagementService)
            .deleteBlobData(containerName, blobUuid);
    }
}
