package uk.gov.hmcts.darts.dailylist.mapper;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import uk.gov.courtservice.schemas.courtservice.ChargeStructure;
import uk.gov.courtservice.schemas.courtservice.CourtHouseStructure;
import uk.gov.courtservice.schemas.courtservice.DailyCourtListStructure;
import uk.gov.courtservice.schemas.courtservice.DailyListStructure;
import uk.gov.courtservice.schemas.courtservice.DefendantStructure;
import uk.gov.courtservice.schemas.courtservice.HearingStructure;
import uk.gov.courtservice.schemas.courtservice.JudiciaryStructure;
import uk.gov.courtservice.schemas.courtservice.SittingStructure;
import uk.gov.hmcts.darts.model.dailyList.Charge;
import uk.gov.hmcts.darts.model.dailyList.CitizenName;
import uk.gov.hmcts.darts.model.dailyList.CourtHouse;
import uk.gov.hmcts.darts.model.dailyList.CourtList;
import uk.gov.hmcts.darts.model.dailyList.DailyList;
import uk.gov.hmcts.darts.model.dailyList.Defendant;
import uk.gov.hmcts.darts.model.dailyList.Hearing;
import uk.gov.hmcts.darts.model.dailyList.Sitting;
import uk.gov.hmcts.darts.utilities.DateUtil;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.OffsetDateTime;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface DailyListRequestMapper {

    @Mappings({
        @Mapping(source = "listHeader.publishedTime", target = "listHeader.publishedTime", qualifiedByName = "XMLGregorianCalendarToOffsetDateTime"),
        @Mapping(source = "documentID", target = "documentId"),
        @Mapping(source = "documentID.uniqueID", target = "documentId.uniqueId"),
        @Mapping(source = "documentID.timeStamp", target = "documentId.timeStamp", qualifiedByName = "XMLGregorianCalendarToOffsetDateTime"),
        @Mapping(source = "crownCourt.courtHouseCode.value", target = "crownCourt.courtHouseCode.code")
    })
    DailyList mapToEntity(DailyListStructure dailyListStructure);

    @Named("XMLGregorianCalendarToOffsetDateTime")
    default OffsetDateTime toOffsetDateTime(XMLGregorianCalendar date) {
        return DateUtil.toOffsetDateTime(date);
    }

    default List<CourtList> map(DailyListStructure.CourtLists courtlists) {
        if(courtlists==null){
            return null;
        }
        return CollectionUtils.emptyIfNull(courtlists.getCourtList()).stream().map(this::map).toList();
    }

    CourtList map(DailyCourtListStructure dailyCourtListStructure);

    default List<Sitting> map(DailyCourtListStructure.Sittings sittings) {
        if(sittings==null){
            return null;
        }
        return CollectionUtils.emptyIfNull(sittings.getSitting()).stream().map(this::map).toList();
    }

    Sitting map(SittingStructure dailyCourtListStructure);

    default List<CitizenName> map(JudiciaryStructure value){
        return List.of(map(value.getJudge()));
    }

    CitizenName map(JudiciaryStructure.Judge judge);

    default List<Hearing> map(SittingStructure.Hearings hearings){
        if(hearings==null){
            return null;
        }
        return CollectionUtils.emptyIfNull(hearings.getHearing()).stream().map(this::map).toList();
    }


    Hearing map(HearingStructure hearing);

    default List<Defendant> map(HearingStructure.Defendants defendants){
        if(defendants==null){
            return null;
        }
        return CollectionUtils.emptyIfNull(defendants.getDefendant()).stream().map(this::map).toList();
    }

    Defendant map(DefendantStructure defendant);

    @Named("nameMapper")
    default String mapName(List<String> names){
        return String.join(" ", names);
    }


    default List<Charge> map(DefendantStructure.Charges charges){
        if(charges==null){
            return null;
        }
        return CollectionUtils.emptyIfNull(charges.getCharge()).stream().map(this::map).toList();
    }

    Charge map(ChargeStructure defendant);


    @Mappings({
        @Mapping(source = "courtHouseCode.value", target = "courtHouseCode.code")
    })
    CourtHouse map(CourtHouseStructure courtHouseStructure);


}
