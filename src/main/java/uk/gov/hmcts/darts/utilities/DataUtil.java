package uk.gov.hmcts.darts.utilities;

import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequestWithStorageGUID;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DataUtil {

    private DataUtil() {

    }

    public static Map<String, String> toMap(AddAudioMetadataRequest addAudioMetadataRequest) {
        DateTimeFormatter dataTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        Map<String, String> metadata = new HashMap<>();
        metadata.put("startedAt", addAudioMetadataRequest.getStartedAt().format(dataTimeFormatter));
        metadata.put("endedAt", addAudioMetadataRequest.getEndedAt().format(dataTimeFormatter));
        metadata.put("channel", String.valueOf(addAudioMetadataRequest.getChannel()));
        metadata.put("totalChannels", String.valueOf(addAudioMetadataRequest.getTotalChannels()));
        metadata.put("format", addAudioMetadataRequest.getFormat());
        metadata.put("filename", addAudioMetadataRequest.getFilename());
        metadata.put("courthouse", addAudioMetadataRequest.getCourthouse());
        metadata.put("courtroom", addAudioMetadataRequest.getCourtroom());
        metadata.put("mediaFile", addAudioMetadataRequest.getMediaFile());
        metadata.put("fileSize", String.valueOf(addAudioMetadataRequest.getFileSize()));
        metadata.put("checksum", addAudioMetadataRequest.getChecksum());
        metadata.put("cases", String.join(",", addAudioMetadataRequest.getCases()));
        return metadata;
    }

    public static AddAudioMetadataRequestWithStorageGUID convertToStorageGuid(AddAudioMetadataRequest metadata, UUID blobStoreUuid) {
        AddAudioMetadataRequestWithStorageGUID addAudioMetadataRequestWithStorageGuid = new AddAudioMetadataRequestWithStorageGUID();
        addAudioMetadataRequestWithStorageGuid.setStartedAt(metadata.getStartedAt());
        addAudioMetadataRequestWithStorageGuid.setEndedAt(metadata.getEndedAt());
        addAudioMetadataRequestWithStorageGuid.setChannel(metadata.getChannel());
        addAudioMetadataRequestWithStorageGuid.setTotalChannels(metadata.getTotalChannels());
        addAudioMetadataRequestWithStorageGuid.setFormat(metadata.getFormat());
        addAudioMetadataRequestWithStorageGuid.setFilename(metadata.getFilename());
        addAudioMetadataRequestWithStorageGuid.setCourthouse(metadata.getCourthouse());
        addAudioMetadataRequestWithStorageGuid.setCourtroom(metadata.getCourtroom());
        addAudioMetadataRequestWithStorageGuid.setMediaFile(metadata.getMediaFile());
        addAudioMetadataRequestWithStorageGuid.setFileSize(metadata.getFileSize());
        addAudioMetadataRequestWithStorageGuid.setChecksum(metadata.getChecksum());
        addAudioMetadataRequestWithStorageGuid.setCases(metadata.getCases());
        addAudioMetadataRequestWithStorageGuid.setStorageGuid(blobStoreUuid);
        return addAudioMetadataRequestWithStorageGuid;
    }
}
