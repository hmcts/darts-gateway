package uk.gov.hmcts.darts.addaudio;

import com.service.mojdarts.synapps.com.AddAudio;
import com.service.mojdarts.synapps.com.addaudio.Audio;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.addaudio.validator.AddAudioFileValidator;
import uk.gov.hmcts.darts.addaudio.validator.AddAudioValidator;
import uk.gov.hmcts.darts.api.audio.AudiosApi;
import uk.gov.hmcts.darts.common.client.multipart.StreamingMultipart;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.datastore.DataManagementConfiguration;
import uk.gov.hmcts.darts.datastore.DataManagementService;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;
import uk.gov.hmcts.darts.utilities.DataUtil;
import uk.gov.hmcts.darts.utilities.FileContentChecksum;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddAudioRoute {
    private final AudiosApi audiosClient;
    private final AddAudioMapper addAudioMapper;
    private final XmlWithFileMultiPartRequestHolder multiPartRequestHolder;
    private final AddAudioValidator addAudioValidator;
    private final AddAudioFileValidator multipartFileValidator;
    private final DataManagementService dataManagementService;
    private final DataManagementConfiguration dataManagementConfiguration;
    private final LogApi logApi;

    public DARTSResponse route(AddAudio addAudio) {

        addAudioValidator.validate(addAudio);

        String audioXml = addAudio.getDocument();

        Audio addAudioLegacy;

        try {
            addAudioLegacy = XmlParser.unmarshal(audioXml, Audio.class);

            addAudioValidator.validate(addAudioLegacy);

            Optional<XmlWithFileMultiPartRequest> request = multiPartRequestHolder.getRequest();

            if (request.isPresent()) {
                addAudioValidator.validateCourtroom(addAudioLegacy);

                AtomicReference<String> checksum = new AtomicReference<>(null);
                request.get().consumeFileBinaryStream(uploadedStream -> checksum.set(FileContentChecksum.calculate(uploadedStream.getInputStream())));
                // consume the uploaded file and proxy downstream
                request.get().consumeFileBinaryStream(uploadedStream -> {
                    StreamingMultipart multipartFile = new StreamingMultipart(
                        addAudioLegacy.getMediafile(),
                        addAudioLegacy.getMediaformat(),
                        uploadedStream
                    );
                    AddAudioMetadataRequest metaData = addAudioMapper.mapToDartsApi(addAudioLegacy);
                    metaData.setFileSize(request.get().getBinarySize());
                    metaData.setChecksum(checksum.get());
                    multipartFileValidator.validate(multipartFile);
                    UUID blobStoreUuid;
                    try {
                        blobStoreUuid = dataManagementService.saveBlobData(
                            dataManagementConfiguration.getInboundContainerName(),
                            multipartFile.getInputStream(),
                            DataUtil.toMap(metaData));
                    } catch (Exception e) {
                        log.error("Failed to upload audio file to the inbound blob store", e);
                        logApi.failedToLinkAudioToCases(
                            metaData.getCourthouse(),
                            metaData.getCourtroom(),
                            metaData.getStartedAt(),
                            metaData.getEndedAt(),
                            metaData.getCases(),
                            metaData.getChecksum(),
                            null
                        );
                        throw new DartsException(CodeAndMessage.ERROR);
                    }
                    log.info("Audio file uploaded successfully to the inbound blob store. BlobStoreUuid: {}", blobStoreUuid);
                    audiosClient.addAudioMetaData(DataUtil.convertToStorageGuid(metaData, blobStoreUuid));
                });
            } else {
                log.error("The add audio endpoint requires a file to be specified. No file was found");
                throw new DartsException(CodeAndMessage.ERROR);
            }
        } catch (IOException ioe) {
            throw new DartsException(ioe, CodeAndMessage.ERROR);
        }

        CodeAndMessage message = CodeAndMessage.OK;
        return message.getResponse();
    }
}
