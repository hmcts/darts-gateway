package uk.gov.hmcts.darts.log.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import uk.gov.hmcts.darts.log.conf.ExcludePayloadLogging;
import uk.gov.hmcts.darts.log.conf.LogProperties;

import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

class LogPropertiesTest {

    private static final String EXCLUSION_LOGGING_PAYLOAD_DL = """
        <ns5:addDocument xmlns:ns5="http://com.synapps.mojdarts.service.com">
                 <messageId>18418</messageId>
                 <type>DL</type>
                 <subType>DL</subType>
                 <document><![CDATA[<cs:DailyList xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.courtservice.gov.uk/schemas/courtservice DailyList-v5-2.xsd" xmlns:apd="http://www.govtalk.gov.uk/people/AddressAndPersonalDetails"><cs:DocumentID><cs:DocumentName>DL 18/02/10 FINAL v1</cs:DocumentName><cs:UniqueID>CSDDL000000000576147</cs:UniqueID><cs:DocumentType>DL</cs:DocumentType><cs:TimeStamp>2010-02-18T11:13:23.030</cs:TimeStamp><cs:Version>1.0</cs:Version><cs:SecurityClassification>NPM</cs:SecurityClassification><cs:SellByDate>2010-12-15</cs:SellByDate><cs:XSLstylesheetURL>http://www.courtservice.gov.uk/transforms/courtservice/dailyListHtml.xsl</cs:XSLstylesheetURL></cs:DocumentID><cs:ListHeader><cs:ListCategory>Criminal</cs:ListCategory><cs:StartDate>2010-02-18</cs:StartDate><cs:EndDate>2010-02-18</cs:EndDate><cs:Version>FINAL v1</cs:Version><cs:CRESTprintRef>MCD/112585</cs:CRESTprintRef><cs:PublishedTime>2010-02-17T16:16:50</cs:PublishedTime><cs:CRESTlistID>12298</cs:CRESTlistID></cs:ListHeader><cs:CrownCourt><cs:CourtHouseType>Crown Court</cs:CourtHouseType><cs:CourtHouseCode CourtHouseShortName="SNARE">453</cs:CourtHouseCode><cs:CourtHouseName>Bristol</cs:CourtHouseName><cs:CourtHouseAddress><apd:Line>THE CROWN COURT AT Bristol</apd:Line><apd:Line>75 HOLLYBUSH HILL</apd:Line><apd:Line>Bristol, LONDON</apd:Line><apd:PostCode>E11 1QW</apd:PostCode></cs:CourtHouseAddress><cs:CourtHouseDX>DX 98240 WANSTEAD 2</cs:CourtHouseDX><cs:CourtHouseTelephone>02085300000</cs:CourtHouseTelephone><cs:CourtHouseFax>02085300072</cs:CourtHouseFax></cs:CrownCourt><cs:CourtLists><cs:CourtList><cs:CourtHouse><cs:CourtHouseType>Crown Court</cs:CourtHouseType><cs:CourtHouseCode>453</cs:CourtHouseCode><cs:CourtHouseName>Bristol</cs:CourtHouseName></cs:CourtHouse><cs:Sittings><cs:Sitting><cs:CourtRoomNumber>1</cs:CourtRoomNumber><cs:SittingSequenceNo>1</cs:SittingSequenceNo><cs:SittingAt>10:00:00</cs:SittingAt><cs:SittingPriority>T</cs:SittingPriority><cs:Judiciary><cs:Judge><apd:CitizenNameSurname>N&#47;A</apd:CitizenNameSurname><apd:CitizenNameRequestedName>N&#47;A</apd:CitizenNameRequestedName><cs:CRESTjudgeID>0</cs:CRESTjudgeID></cs:Judge></cs:Judiciary><cs:Hearings><cs:Hearing><cs:HearingSequenceNumber>1</cs:HearingSequenceNumber><cs:HearingDetails HearingType="TRL"><cs:HearingDescription>For Trial</cs:HearingDescription><cs:HearingDate>2010-02-18</cs:HearingDate></cs:HearingDetails><cs:CRESThearingID>1</cs:CRESThearingID><cs:TimeMarkingNote>10:00 AM</cs:TimeMarkingNote><cs:CaseNumber>T20107001</cs:CaseNumber><cs:Prosecution ProsecutingAuthority="Crown Prosecution Service"><cs:ProsecutingReference>CPS</cs:ProsecutingReference><cs:ProsecutingOrganisation><cs:OrganisationName>Crown Prosecution Service</cs:OrganisationName></cs:ProsecutingOrganisation></cs:Prosecution><cs:CommittingCourt><cs:CourtHouseType>Magistrates Court</cs:CourtHouseType><cs:CourtHouseCode CourtHouseShortName="BAM">2725</cs:CourtHouseCode><cs:CourtHouseName>BARNET MAGISTRATES COURT</cs:CourtHouseName><cs:CourtHouseAddress><apd:Line>7C HIGH STREET</apd:Line><apd:Line>-</apd:Line><apd:Line>BARNET</apd:Line><apd:PostCode>EN5 5UE</apd:PostCode></cs:CourtHouseAddress><cs:CourtHouseDX>DX 8626 BARNET</cs:CourtHouseDX><cs:CourtHouseTelephone>02084419042</cs:CourtHouseTelephone></cs:CommittingCourt><cs:Defendants><cs:Defendant><cs:PersonalDetails><cs:Name><apd:CitizenNameForename>Franz</apd:CitizenNameForename><apd:CitizenNameSurname>KAFKA</apd:CitizenNameSurname></cs:Name><cs:IsMasked>no</cs:IsMasked><cs:DateOfBirth><apd:BirthDate>1962-06-12</apd:BirthDate><apd:VerifiedBy>not verified</apd:VerifiedBy></cs:DateOfBirth><cs:Sex>male</cs:Sex><cs:Address><apd:Line>ADDRESS LINE 1</apd:Line><apd:Line>ADDRESS LINE 2</apd:Line><apd:Line>ADDRESS LINE 3</apd:Line><apd:Line>ADDRESS LINE 4</apd:Line><apd:Line>SOMETOWN, SOMECOUNTY</apd:Line><apd:PostCode>GU12 7RT</apd:PostCode></cs:Address></cs:PersonalDetails><cs:ASNs><cs:ASN>0723XH1000000262665K</cs:ASN></cs:ASNs><cs:CRESTdefendantID>29161</cs:CRESTdefendantID><cs:PNCnumber>20123456789L</cs:PNCnumber><cs:URN>62AA1010646</cs:URN><cs:CustodyStatus>In custody</cs:CustodyStatus></cs:Defendant></cs:Defendants></cs:Hearing></cs:Hearings></cs:Sitting></cs:Sittings></cs:CourtList></cs:CourtLists></cs:DailyList>]]></document>
              </ns5:addDocument>
        """;

    private static final String EXCLUSION_LOGGING_PAYLOAD_CPPDL = """
        <ns5:addDocument xmlns:ns5="http://com.synapps.mojdarts.service.com">
                 <messageId>18418</messageId>
                 <type>CPPDL</type>
                 <subType>DL</subType>
                 <document><![CDATA[<cs:DailyList xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.courtservice.gov.uk/schemas/courtservice DailyList-v5-2.xsd" xmlns:apd="http://www.govtalk.gov.uk/people/AddressAndPersonalDetails"><cs:DocumentID><cs:DocumentName>DL 18/02/10 FINAL v1</cs:DocumentName><cs:UniqueID>CSDDL000000000576147</cs:UniqueID><cs:DocumentType>DL</cs:DocumentType><cs:TimeStamp>2010-02-18T11:13:23.030</cs:TimeStamp><cs:Version>1.0</cs:Version><cs:SecurityClassification>NPM</cs:SecurityClassification><cs:SellByDate>2010-12-15</cs:SellByDate><cs:XSLstylesheetURL>http://www.courtservice.gov.uk/transforms/courtservice/dailyListHtml.xsl</cs:XSLstylesheetURL></cs:DocumentID><cs:ListHeader><cs:ListCategory>Criminal</cs:ListCategory><cs:StartDate>2010-02-18</cs:StartDate><cs:EndDate>2010-02-18</cs:EndDate><cs:Version>FINAL v1</cs:Version><cs:CRESTprintRef>MCD/112585</cs:CRESTprintRef><cs:PublishedTime>2010-02-17T16:16:50</cs:PublishedTime><cs:CRESTlistID>12298</cs:CRESTlistID></cs:ListHeader><cs:CrownCourt><cs:CourtHouseType>Crown Court</cs:CourtHouseType><cs:CourtHouseCode CourtHouseShortName="SNARE">453</cs:CourtHouseCode><cs:CourtHouseName>Bristol</cs:CourtHouseName><cs:CourtHouseAddress><apd:Line>THE CROWN COURT AT Bristol</apd:Line><apd:Line>75 HOLLYBUSH HILL</apd:Line><apd:Line>Bristol, LONDON</apd:Line><apd:PostCode>E11 1QW</apd:PostCode></cs:CourtHouseAddress><cs:CourtHouseDX>DX 98240 WANSTEAD 2</cs:CourtHouseDX><cs:CourtHouseTelephone>02085300000</cs:CourtHouseTelephone><cs:CourtHouseFax>02085300072</cs:CourtHouseFax></cs:CrownCourt><cs:CourtLists><cs:CourtList><cs:CourtHouse><cs:CourtHouseType>Crown Court</cs:CourtHouseType><cs:CourtHouseCode>453</cs:CourtHouseCode><cs:CourtHouseName>Bristol</cs:CourtHouseName></cs:CourtHouse><cs:Sittings><cs:Sitting><cs:CourtRoomNumber>1</cs:CourtRoomNumber><cs:SittingSequenceNo>1</cs:SittingSequenceNo><cs:SittingAt>10:00:00</cs:SittingAt><cs:SittingPriority>T</cs:SittingPriority><cs:Judiciary><cs:Judge><apd:CitizenNameSurname>N&#47;A</apd:CitizenNameSurname><apd:CitizenNameRequestedName>N&#47;A</apd:CitizenNameRequestedName><cs:CRESTjudgeID>0</cs:CRESTjudgeID></cs:Judge></cs:Judiciary><cs:Hearings><cs:Hearing><cs:HearingSequenceNumber>1</cs:HearingSequenceNumber><cs:HearingDetails HearingType="TRL"><cs:HearingDescription>For Trial</cs:HearingDescription><cs:HearingDate>2010-02-18</cs:HearingDate></cs:HearingDetails><cs:CRESThearingID>1</cs:CRESThearingID><cs:TimeMarkingNote>10:00 AM</cs:TimeMarkingNote><cs:CaseNumber>T20107001</cs:CaseNumber><cs:Prosecution ProsecutingAuthority="Crown Prosecution Service"><cs:ProsecutingReference>CPS</cs:ProsecutingReference><cs:ProsecutingOrganisation><cs:OrganisationName>Crown Prosecution Service</cs:OrganisationName></cs:ProsecutingOrganisation></cs:Prosecution><cs:CommittingCourt><cs:CourtHouseType>Magistrates Court</cs:CourtHouseType><cs:CourtHouseCode CourtHouseShortName="BAM">2725</cs:CourtHouseCode><cs:CourtHouseName>BARNET MAGISTRATES COURT</cs:CourtHouseName><cs:CourtHouseAddress><apd:Line>7C HIGH STREET</apd:Line><apd:Line>-</apd:Line><apd:Line>BARNET</apd:Line><apd:PostCode>EN5 5UE</apd:PostCode></cs:CourtHouseAddress><cs:CourtHouseDX>DX 8626 BARNET</cs:CourtHouseDX><cs:CourtHouseTelephone>02084419042</cs:CourtHouseTelephone></cs:CommittingCourt><cs:Defendants><cs:Defendant><cs:PersonalDetails><cs:Name><apd:CitizenNameForename>Franz</apd:CitizenNameForename><apd:CitizenNameSurname>KAFKA</apd:CitizenNameSurname></cs:Name><cs:IsMasked>no</cs:IsMasked><cs:DateOfBirth><apd:BirthDate>1962-06-12</apd:BirthDate><apd:VerifiedBy>not verified</apd:VerifiedBy></cs:DateOfBirth><cs:Sex>male</cs:Sex><cs:Address><apd:Line>ADDRESS LINE 1</apd:Line><apd:Line>ADDRESS LINE 2</apd:Line><apd:Line>ADDRESS LINE 3</apd:Line><apd:Line>ADDRESS LINE 4</apd:Line><apd:Line>SOMETOWN, SOMECOUNTY</apd:Line><apd:PostCode>GU12 7RT</apd:PostCode></cs:Address></cs:PersonalDetails><cs:ASNs><cs:ASN>0723XH1000000262665K</cs:ASN></cs:ASNs><cs:CRESTdefendantID>29161</cs:CRESTdefendantID><cs:PNCnumber>20123456789L</cs:PNCnumber><cs:URN>62AA1010646</cs:URN><cs:CustodyStatus>In custody</cs:CustodyStatus></cs:Defendant></cs:Defendants></cs:Hearing></cs:Hearings></cs:Sitting></cs:Sittings></cs:CourtList></cs:CourtLists></cs:DailyList>]]></document>
              </ns5:addDocument>
        """;

    private static final String INCLUSION_LOGGING_PAYLOAD = """
        <registerNode xmlns="http://com.synapps.mojdarts.service.com">
             <document xmlns=""><![CDATA[<node type="DAR">
                 <courthouse>Bristol</courthouse>
                 <courtroom>1</courtroom>
                 <hostname>hostname</hostname>
                 <ip_address>ip_address</ip_address>
                 <mac_address>mac_address</mac_address>
                 </node>]]></document>
            </registerNode>
        """;

    private static final String INCLUSION_LOGGING_PAYLOAD_DOC_INCLUSION_TYPE = """
        <ns5:addDocument xmlns:ns5="http://com.synapps.mojdarts.service.com">
                 <messageId>18418</messageId>
                 <type>THIS WILL BE INCLUDED</type>
                 <subType>DL</subType>
                 <document><![CDATA[<cs:DailyList xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.courtservice.gov.uk/schemas/courtservice DailyList-v5-2.xsd" xmlns:apd="http://www.govtalk.gov.uk/people/AddressAndPersonalDetails"><cs:DocumentID><cs:DocumentName>DL 18/02/10 FINAL v1</cs:DocumentName><cs:UniqueID>CSDDL000000000576147</cs:UniqueID><cs:DocumentType>DL</cs:DocumentType><cs:TimeStamp>2010-02-18T11:13:23.030</cs:TimeStamp><cs:Version>1.0</cs:Version><cs:SecurityClassification>NPM</cs:SecurityClassification><cs:SellByDate>2010-12-15</cs:SellByDate><cs:XSLstylesheetURL>http://www.courtservice.gov.uk/transforms/courtservice/dailyListHtml.xsl</cs:XSLstylesheetURL></cs:DocumentID><cs:ListHeader><cs:ListCategory>Criminal</cs:ListCategory><cs:StartDate>2010-02-18</cs:StartDate><cs:EndDate>2010-02-18</cs:EndDate><cs:Version>FINAL v1</cs:Version><cs:CRESTprintRef>MCD/112585</cs:CRESTprintRef><cs:PublishedTime>2010-02-17T16:16:50</cs:PublishedTime><cs:CRESTlistID>12298</cs:CRESTlistID></cs:ListHeader><cs:CrownCourt><cs:CourtHouseType>Crown Court</cs:CourtHouseType><cs:CourtHouseCode CourtHouseShortName="SNARE">453</cs:CourtHouseCode><cs:CourtHouseName>Bristol</cs:CourtHouseName><cs:CourtHouseAddress><apd:Line>THE CROWN COURT AT Bristol</apd:Line><apd:Line>75 HOLLYBUSH HILL</apd:Line><apd:Line>Bristol, LONDON</apd:Line><apd:PostCode>E11 1QW</apd:PostCode></cs:CourtHouseAddress><cs:CourtHouseDX>DX 98240 WANSTEAD 2</cs:CourtHouseDX><cs:CourtHouseTelephone>02085300000</cs:CourtHouseTelephone><cs:CourtHouseFax>02085300072</cs:CourtHouseFax></cs:CrownCourt><cs:CourtLists><cs:CourtList><cs:CourtHouse><cs:CourtHouseType>Crown Court</cs:CourtHouseType><cs:CourtHouseCode>453</cs:CourtHouseCode><cs:CourtHouseName>Bristol</cs:CourtHouseName></cs:CourtHouse><cs:Sittings><cs:Sitting><cs:CourtRoomNumber>1</cs:CourtRoomNumber><cs:SittingSequenceNo>1</cs:SittingSequenceNo><cs:SittingAt>10:00:00</cs:SittingAt><cs:SittingPriority>T</cs:SittingPriority><cs:Judiciary><cs:Judge><apd:CitizenNameSurname>N&#47;A</apd:CitizenNameSurname><apd:CitizenNameRequestedName>N&#47;A</apd:CitizenNameRequestedName><cs:CRESTjudgeID>0</cs:CRESTjudgeID></cs:Judge></cs:Judiciary><cs:Hearings><cs:Hearing><cs:HearingSequenceNumber>1</cs:HearingSequenceNumber><cs:HearingDetails HearingType="TRL"><cs:HearingDescription>For Trial</cs:HearingDescription><cs:HearingDate>2010-02-18</cs:HearingDate></cs:HearingDetails><cs:CRESThearingID>1</cs:CRESThearingID><cs:TimeMarkingNote>10:00 AM</cs:TimeMarkingNote><cs:CaseNumber>T20107001</cs:CaseNumber><cs:Prosecution ProsecutingAuthority="Crown Prosecution Service"><cs:ProsecutingReference>CPS</cs:ProsecutingReference><cs:ProsecutingOrganisation><cs:OrganisationName>Crown Prosecution Service</cs:OrganisationName></cs:ProsecutingOrganisation></cs:Prosecution><cs:CommittingCourt><cs:CourtHouseType>Magistrates Court</cs:CourtHouseType><cs:CourtHouseCode CourtHouseShortName="BAM">2725</cs:CourtHouseCode><cs:CourtHouseName>BARNET MAGISTRATES COURT</cs:CourtHouseName><cs:CourtHouseAddress><apd:Line>7C HIGH STREET</apd:Line><apd:Line>-</apd:Line><apd:Line>BARNET</apd:Line><apd:PostCode>EN5 5UE</apd:PostCode></cs:CourtHouseAddress><cs:CourtHouseDX>DX 8626 BARNET</cs:CourtHouseDX><cs:CourtHouseTelephone>02084419042</cs:CourtHouseTelephone></cs:CommittingCourt><cs:Defendants><cs:Defendant><cs:PersonalDetails><cs:Name><apd:CitizenNameForename>Franz</apd:CitizenNameForename><apd:CitizenNameSurname>KAFKA</apd:CitizenNameSurname></cs:Name><cs:IsMasked>no</cs:IsMasked><cs:DateOfBirth><apd:BirthDate>1962-06-12</apd:BirthDate><apd:VerifiedBy>not verified</apd:VerifiedBy></cs:DateOfBirth><cs:Sex>male</cs:Sex><cs:Address><apd:Line>ADDRESS LINE 1</apd:Line><apd:Line>ADDRESS LINE 2</apd:Line><apd:Line>ADDRESS LINE 3</apd:Line><apd:Line>ADDRESS LINE 4</apd:Line><apd:Line>SOMETOWN, SOMECOUNTY</apd:Line><apd:PostCode>GU12 7RT</apd:PostCode></cs:Address></cs:PersonalDetails><cs:ASNs><cs:ASN>0723XH1000000262665K</cs:ASN></cs:ASNs><cs:CRESTdefendantID>29161</cs:CRESTdefendantID><cs:PNCnumber>20123456789L</cs:PNCnumber><cs:URN>62AA1010646</cs:URN><cs:CustodyStatus>In custody</cs:CustodyStatus></cs:Defendant></cs:Defendants></cs:Hearing></cs:Hearings></cs:Sitting></cs:Sittings></cs:CourtList></cs:CourtLists></cs:DailyList>]]></document>
              </ns5:addDocument>
        """;

    private LogProperties properties;

    @BeforeEach
    public void setup() {
        properties = new LogProperties();
        ExcludePayloadLogging excludePayloadLogging = new ExcludePayloadLogging();
        excludePayloadLogging.setNamespace("http://com.synapps.mojdarts.service.com");
        excludePayloadLogging.setTag("addDocument");
        excludePayloadLogging.setType("DL");

        ExcludePayloadLogging excludePayloadLogging2 = new ExcludePayloadLogging();
        excludePayloadLogging2.setNamespace("http://com.synapps.mojdarts.service.com");
        excludePayloadLogging2.setTag("addDocument");
        excludePayloadLogging2.setType("CPPDL");

        properties.setExcludePayloadRequestLoggingBasedOnPayloadNamespaceAndTag(List.of(excludePayloadLogging, excludePayloadLogging2));
    }

    @Test
    void testExclusion_DL() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new InputSource(new StringReader(EXCLUSION_LOGGING_PAYLOAD_DL)));

        Assertions.assertTrue(properties.excludePayload(new DOMSource(xmlDocument)).isPresent());
    }

    @Test
    void testExclusion_Cppdl() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new InputSource(new StringReader(EXCLUSION_LOGGING_PAYLOAD_CPPDL)));

        Assertions.assertTrue(properties.excludePayload(new DOMSource(xmlDocument)).isPresent());
    }

    @Test
    void testExclusion_DocWithNoDlType() throws Exception {
        properties = new LogProperties();
        ExcludePayloadLogging excludePayloadLogging = new ExcludePayloadLogging();
        excludePayloadLogging.setNamespace("http://com.synapps.mojdarts.service.com");
        excludePayloadLogging.setTag("addDocument");

        properties.setExcludePayloadRequestLoggingBasedOnPayloadNamespaceAndTag(List.of(excludePayloadLogging));

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new InputSource(new StringReader(EXCLUSION_LOGGING_PAYLOAD_DL)));

        Assertions.assertTrue(properties.excludePayload(new DOMSource(xmlDocument)).isPresent());
    }

    @Test
    void testInclusion() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new InputSource(new StringReader(INCLUSION_LOGGING_PAYLOAD)));

        Assertions.assertFalse(properties.excludePayload(new DOMSource(xmlDocument)).isPresent());
    }

    @Test
    void testInclusionWithDocButNoDlType() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new InputSource(new StringReader(INCLUSION_LOGGING_PAYLOAD_DOC_INCLUSION_TYPE)));

        Assertions.assertFalse(properties.excludePayload(new DOMSource(xmlDocument)).isPresent());
    }
}
