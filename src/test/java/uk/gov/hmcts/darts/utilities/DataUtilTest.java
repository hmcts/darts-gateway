package uk.gov.hmcts.darts.utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequestWithStorageGUID;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class DataUtilTest {


    private AddAudioMetadataRequest getAddAudioMetadataRequest() {
        AddAudioMetadataRequest addAudioMetadataRequest = new AddAudioMetadataRequest();
        addAudioMetadataRequest.setStartedAt(OffsetDateTime.parse("2021-01-01T00:00:00Z"));
        addAudioMetadataRequest.setEndedAt(OffsetDateTime.parse("2022-01-01T00:00:00Z"));
        addAudioMetadataRequest.setChannel(1);
        addAudioMetadataRequest.setTotalChannels(2);
        addAudioMetadataRequest.setFormat("format");
        addAudioMetadataRequest.setFilename("filename");
        addAudioMetadataRequest.setCourthouse("courthouse");
        addAudioMetadataRequest.setCourtroom("courtroom");
        addAudioMetadataRequest.setMediaFile("mediaFile");
        addAudioMetadataRequest.setFileSize(100L);
        addAudioMetadataRequest.setChecksum("checksum");
        addAudioMetadataRequest.setCases(List.of("case1", "case2"));
        return addAudioMetadataRequest;
    }

    @Test
    void positiveToMap() {
        AddAudioMetadataRequest addAudioMetadataRequest = getAddAudioMetadataRequest();

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("startedAt", "2021-01-01T00:00:00Z");
        expectedMap.put("endedAt", "2022-01-01T00:00:00Z");
        expectedMap.put("channel", "1");
        expectedMap.put("totalChannels", "2");
        expectedMap.put("format", "format");
        expectedMap.put("filename", "filename");
        expectedMap.put("courthouse", "courthouse");
        expectedMap.put("courtroom", "courtroom");
        expectedMap.put("mediaFile", "mediaFile");
        expectedMap.put("fileSize", "100");
        expectedMap.put("checksum", "checksum");
        expectedMap.put("cases", "case1,case2");

        Map<String, String> actualMap = DataUtil.toMap(addAudioMetadataRequest);
        Assertions.assertEquals(expectedMap, actualMap);
    }

    @Test
    void positiveToMapBlankRequest() {
        AddAudioMetadataRequest addAudioMetadataRequest = new AddAudioMetadataRequest();

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("startedAt", null);
        expectedMap.put("endedAt", null);
        expectedMap.put("channel", null);
        expectedMap.put("totalChannels", null);
        expectedMap.put("format", null);
        expectedMap.put("filename", null);
        expectedMap.put("courthouse", null);
        expectedMap.put("courtroom", null);
        expectedMap.put("mediaFile", null);
        expectedMap.put("fileSize", null);
        expectedMap.put("checksum", null);
        expectedMap.put("cases", "");

        Map<String, String> actualMap = DataUtil.toMap(addAudioMetadataRequest);
        Assertions.assertEquals(expectedMap, actualMap);
    }

    @Test
    void positiveConvertToStorageGuid() {
        AddAudioMetadataRequest addAudioMetadataRequest = getAddAudioMetadataRequest();
        UUID uuid = UUID.randomUUID();
        AddAudioMetadataRequestWithStorageGUID expected = new AddAudioMetadataRequestWithStorageGUID();
        expected.setStartedAt(OffsetDateTime.parse("2021-01-01T00:00:00Z"));
        expected.setEndedAt(OffsetDateTime.parse("2022-01-01T00:00:00Z"));
        expected.setChannel(1);
        expected.setTotalChannels(2);
        expected.setFormat("format");
        expected.setFilename("filename");
        expected.setCourthouse("courthouse");
        expected.setCourtroom("courtroom");
        expected.setMediaFile("mediaFile");
        expected.setFileSize(100L);
        expected.setChecksum("checksum");
        expected.setCases(List.of("case1", "case2"));
        expected.setStorageGuid(uuid);

        AddAudioMetadataRequestWithStorageGUID actual = DataUtil.convertToStorageGuid(addAudioMetadataRequest, uuid);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void positiveConvertToStorageGuidBlankRequest() {
        AddAudioMetadataRequest addAudioMetadataRequest = new AddAudioMetadataRequest();
        AddAudioMetadataRequestWithStorageGUID expected = new AddAudioMetadataRequestWithStorageGUID();
        UUID uuid = UUID.randomUUID();
        expected.setStorageGuid(uuid);

        AddAudioMetadataRequestWithStorageGUID actual = DataUtil.convertToStorageGuid(addAudioMetadataRequest, uuid);
        Assertions.assertEquals(expected, actual);
    }

}
