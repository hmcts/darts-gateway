package uk.gov.hmcts.darts.utilities;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequestWithStorageGUID;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class DataUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private DataUtil() {

    }

    public static Map<String, String> toMap(@NotNull @Valid AddAudioMetadataRequest addAudioMetadataRequest) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("startedAt", formatDateTime(addAudioMetadataRequest.getStartedAt()));
        metadata.put("endedAt", formatDateTime(addAudioMetadataRequest.getEndedAt()));
        metadata.put("channel", toString(addAudioMetadataRequest.getChannel()));
        metadata.put("totalChannels", toString(addAudioMetadataRequest.getTotalChannels()));
        metadata.put("format", addAudioMetadataRequest.getFormat());
        metadata.put("filename", addAudioMetadataRequest.getFilename());
        metadata.put("courthouse", addAudioMetadataRequest.getCourthouse());
        metadata.put("courtroom", addAudioMetadataRequest.getCourtroom());
        metadata.put("mediaFile", addAudioMetadataRequest.getMediaFile());
        metadata.put("fileSize", toString(addAudioMetadataRequest.getFileSize()));
        metadata.put("checksum", addAudioMetadataRequest.getChecksum());
        metadata.put("cases", String.join(",", addAudioMetadataRequest.getCases()));
        return metadata;
    }

    public static AddAudioMetadataRequestWithStorageGUID convertToStorageGuid(@NotNull @Valid AddAudioMetadataRequest metadata,
                                                                              @NotNull @Valid UUID blobStoreUuid) {
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

    private static String formatDateTime(@NotNull @Valid OffsetDateTime date) {
        return Optional.ofNullable(date)
            .map(DATE_TIME_FORMATTER::format)
            .orElse(null);
    }

    private static String toString(Long longValue) {
        return Optional.ofNullable(longValue)
            .map(String::valueOf)
            .orElse(null);
    }

    private static String toString(Integer intValue) {
        return Optional.ofNullable(intValue)
            .map(String::valueOf)
            .orElse(null);
    }
}
