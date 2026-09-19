package com.welfare.welfare.api.dto;

import com.welfare.welfare.domain.WelfareApplication;
import com.welfare.welfare.domain.WelfareContact;
import com.welfare.welfare.domain.WelfareForm;
import com.welfare.welfare.domain.WelfareLaw;
import com.welfare.welfare.domain.WelfareLink;
import com.welfare.welfare.domain.WelfareService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "복지 서비스 상세")
public class WelfareDetailResponse {

    @Schema(description = "복지서비스 ID")
    private final String id;

    @Schema(description = "수집 API 코드")
    private final String apiCd;

    @Schema(description = "복지서비스 코드")
    private final String servCd;

    @Schema(description = "복지서비스명")
    private final String servNm;

    @Schema(description = "소관 부처명")
    private final String jurMnofNm;

    @Schema(description = "소관 기관명")
    private final String jurOrgNm;

    @Schema(description = "조회수")
    private final Integer inqNum;

    @Schema(description = "서비스 요약")
    private final String servDgst;

    @Schema(description = "서비스 상세 링크")
    private final String servDtlLink;

    @Schema(description = "서비스 최초 등록 일시")
    private final String svcfrstRegTs;

    @Schema(description = "지원 주기")
    private final String sprtCycNm;

    @Schema(description = "서비스 제공 형태")
    private final String srvPvsnNm;

    @Schema(description = "대표 문의처")
    private final String rprsCtadr;

    @Schema(description = "온라인 신청 가능 여부 (Y/N)")
    private final String onapPsbltYn;

    @Schema(description = "시행 시작일")
    private final String enfcBgngYmd;

    @Schema(description = "시행 종료일")
    private final String enfcEndYmd;

    @Schema(description = "사업 담당 부서명")
    private final String bizChrDeptNm;

    @Schema(description = "시도명")
    private final String ctpvNm;

    @Schema(description = "시군구명")
    private final String sggNm;

    @Schema(description = "복지서비스 개요")
    private final String wlfareInfoOutlCn;

    @Schema(description = "기준연도")
    private final String crtrYr;

    @Schema(description = "대상자 상세 내용")
    private final String tgtrDtlCn;

    @Schema(description = "선정 기준")
    private final String slctCritCn;

    @Schema(description = "급여 서비스 내용")
    private final String alwServCn;

    @Schema(description = "지원 대상 내용")
    private final String sprtTrgtCn;

    @Schema(description = "최종 수정일")
    private final String lastModYmd;

    @Schema(description = "생애주기")
    private final List<WelfareCodeNameResponse> lifeStages;

    @Schema(description = "가구형태")
    private final List<WelfareCodeNameResponse> householdTypes;

    @Schema(description = "관심분야")
    private final List<WelfareCodeNameResponse> interests;

    @Schema(description = "신청 방법")
    private final List<ApplicationItem> applications;

    @Schema(description = "문의처")
    private final List<ContactItem> contacts;

    @Schema(description = "관련 링크")
    private final List<LinkItem> links;

    @Schema(description = "서식")
    private final List<FormItem> forms;

    @Schema(description = "관련 법령")
    private final List<LawItem> laws;

    public static WelfareDetailResponse from(WelfareService service) {
        return WelfareDetailResponse.builder()
                .id(service.getId())
                .apiCd(service.getApiCd())
                .servCd(service.getServCd())
                .servNm(service.getServNm())
                .jurMnofNm(service.getJurMnofNm())
                .jurOrgNm(service.getJurOrgNm())
                .inqNum(service.getInqNum())
                .servDgst(service.getServDgst())
                .servDtlLink(service.getServDtlLink())
                .svcfrstRegTs(service.getSvcfrstRegTs())
                .sprtCycNm(service.getSprtCycNm())
                .srvPvsnNm(service.getSrvPvsnNm())
                .rprsCtadr(service.getRprsCtadr())
                .onapPsbltYn(service.getOnapPsbltYn())
                .enfcBgngYmd(service.getEnfcBgngYmd())
                .enfcEndYmd(service.getEnfcEndYmd())
                .bizChrDeptNm(service.getBizChrDeptNm())
                .ctpvNm(service.getCtpvNm())
                .sggNm(service.getSggNm())
                .wlfareInfoOutlCn(service.getWlfareInfoOutlCn())
                .crtrYr(service.getCrtrYr())
                .tgtrDtlCn(service.getTgtrDtlCn())
                .slctCritCn(service.getSlctCritCn())
                .alwServCn(service.getAlwServCn())
                .sprtTrgtCn(service.getSprtTrgtCn())
                .lastModYmd(service.getLastModYmd())
                .lifeStages(service.getLifeStages().stream()
                        .map(WelfareCodeNameResponse::from)
                        .sorted(Comparator.comparing(WelfareCodeNameResponse::getCode, Comparator.nullsLast(String::compareTo)))
                        .toList())
                .householdTypes(service.getHouseholdTypes().stream()
                        .map(WelfareCodeNameResponse::from)
                        .sorted(Comparator.comparing(WelfareCodeNameResponse::getCode, Comparator.nullsLast(String::compareTo)))
                        .toList())
                .interests(service.getInterests().stream()
                        .map(WelfareCodeNameResponse::from)
                        .sorted(Comparator.comparing(WelfareCodeNameResponse::getCode, Comparator.nullsLast(String::compareTo)))
                        .toList())
                .applications(service.getApplications().stream()
                        .sorted(Comparator.comparing(WelfareApplication::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                        .map(ApplicationItem::from)
                        .toList())
                .contacts(service.getContacts().stream()
                        .sorted(Comparator.comparing(WelfareContact::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                        .map(ContactItem::from)
                        .toList())
                .links(service.getLinks().stream()
                        .sorted(Comparator.comparing(WelfareLink::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                        .map(LinkItem::from)
                        .toList())
                .forms(service.getForms().stream()
                        .sorted(Comparator.comparing(WelfareForm::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                        .map(FormItem::from)
                        .toList())
                .laws(service.getLaws().stream()
                        .sorted(Comparator.comparing(WelfareLaw::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                        .map(LawItem::from)
                        .toList())
                .build();
    }

    @Getter
    @Builder
    @Schema(description = "신청 방법")
    public static class ApplicationItem {

        @Schema(description = "서비스 구분 코드")
        private final String servSeCode;

        @Schema(description = "신청 방법 상세명")
        private final String servSeDetailNm;

        @Schema(description = "신청 방법 상세 링크")
        private final String servSeDetailLink;

        public static ApplicationItem from(WelfareApplication application) {
            return ApplicationItem.builder()
                    .servSeCode(application.getServSeCode())
                    .servSeDetailNm(application.getServSeDetailNm())
                    .servSeDetailLink(application.getServSeDetailLink())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "문의처")
    public static class ContactItem {

        @Schema(description = "서비스 구분 코드")
        private final String servSeCode;

        @Schema(description = "문의처명")
        private final String contactName;

        @Schema(description = "문의처 정보")
        private final String contactValue;

        public static ContactItem from(WelfareContact contact) {
            return ContactItem.builder()
                    .servSeCode(contact.getServSeCode())
                    .contactName(contact.getContactName())
                    .contactValue(contact.getContactValue())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "관련 링크")
    public static class LinkItem {

        @Schema(description = "서비스 구분 코드")
        private final String servSeCode;

        @Schema(description = "링크명")
        private final String linkName;

        @Schema(description = "링크 URL")
        private final String linkUrl;

        public static LinkItem from(WelfareLink link) {
            return LinkItem.builder()
                    .servSeCode(link.getServSeCode())
                    .linkName(link.getLinkName())
                    .linkUrl(link.getLinkUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "서식")
    public static class FormItem {

        @Schema(description = "서식명")
        private final String formName;

        @Schema(description = "서식 다운로드 URL")
        private final String formUrl;

        public static FormItem from(WelfareForm form) {
            return FormItem.builder()
                    .formName(form.getFormName())
                    .formUrl(form.getFormUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "관련 법령")
    public static class LawItem {

        @Schema(description = "법령명")
        private final String lawName;

        @Schema(description = "법령 관련 URL")
        private final String lawUrl;

        public static LawItem from(WelfareLaw law) {
            return LawItem.builder()
                    .lawName(law.getLawName())
                    .lawUrl(law.getLawUrl())
                    .build();
        }
    }
}
