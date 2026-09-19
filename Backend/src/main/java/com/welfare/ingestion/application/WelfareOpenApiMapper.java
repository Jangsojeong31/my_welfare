package com.welfare.ingestion.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.welfare.ingestion.infrastructure.OpenApiNodeUtils;
import com.welfare.welfare.domain.WelfareApplication;
import com.welfare.welfare.domain.WelfareContact;
import com.welfare.welfare.domain.WelfareForm;
import com.welfare.welfare.domain.WelfareLaw;
import com.welfare.welfare.domain.WelfareLink;
import com.welfare.welfare.domain.WelfareService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WelfareOpenApiMapper {

    public String extractServCd(JsonNode node) {
        return OpenApiNodeUtils.text(node, "servId", "servCd", "복지 서비스 코드", "서비스ID");
    }

    public String extractServNm(JsonNode node) {
        return OpenApiNodeUtils.text(node, "servNm", "serv_nm", "복지 서비스명", "서비스명");
    }

    public WelfareService toNewService(String apiCd, JsonNode listNode) {
        String servNm = extractServNm(listNode);
        if (servNm == null) {
            return null;
        }
        return WelfareService.builder()
                .apiCd(apiCd)
                .servCd(extractServCd(listNode))
                .servNm(servNm)
                .jurMnofNm(OpenApiNodeUtils.text(listNode, "jurMnofNm", "소관 부처명"))
                .jurOrgNm(OpenApiNodeUtils.text(listNode, "jurOrgNm", "소관 기관명"))
                .inqNum(OpenApiNodeUtils.integerOrZero(listNode, "inqNum", "조회수"))
                .servDgst(OpenApiNodeUtils.text(listNode, "servDgst", "서비스 요약"))
                .servDtlLink(OpenApiNodeUtils.text(listNode, "servDtlLink", "서비스 상세 링크"))
                .svcfrstRegTs(OpenApiNodeUtils.text(listNode, "svcfrstRegTs", "서비스 최초 등록 일시"))
                .sprtCycNm(OpenApiNodeUtils.text(listNode, "sprtCycNm", "지원 주기"))
                .srvPvsnNm(OpenApiNodeUtils.text(listNode, "srvPvsnNm", "서비스 제공 형태"))
                .rprsCtadr(OpenApiNodeUtils.text(listNode, "rprsCtadr", "대표 문의처"))
                .onapPsbltYn(OpenApiNodeUtils.yn(listNode, "onapPsbltYn", "온라인 신청 가능 여부"))
                .enfcBgngYmd(OpenApiNodeUtils.text(listNode, "enfcBgngYmd"))
                .enfcEndYmd(OpenApiNodeUtils.text(listNode, "enfcEndYmd"))
                .bizChrDeptNm(OpenApiNodeUtils.text(listNode, "bizChrDeptNm"))
                .ctpvNm(OpenApiNodeUtils.text(listNode, "ctpvNm"))
                .sggNm(OpenApiNodeUtils.text(listNode, "sggNm"))
                .wlfareInfoOutlCn(OpenApiNodeUtils.text(listNode, "wlfareInfoOutlCn"))
                .crtrYr(OpenApiNodeUtils.text(listNode, "crtrYr"))
                .tgtrDtlCn(OpenApiNodeUtils.text(listNode, "tgtrDtlCn"))
                .slctCritCn(OpenApiNodeUtils.text(listNode, "slctCritCn"))
                .alwServCn(OpenApiNodeUtils.text(listNode, "alwServCn"))
                .sprtTrgtCn(OpenApiNodeUtils.text(listNode, "sprtTrgtCn"))
                .lastModYmd(OpenApiNodeUtils.text(listNode, "lastModYmd"))
                .build();
    }

    public void applyDetail(WelfareService service, JsonNode detailNode) {
        if (detailNode == null) {
            return;
        }
        service.applyDetail(
                OpenApiNodeUtils.text(detailNode, "jurMnofNm", "소관 부처명"),
                OpenApiNodeUtils.text(detailNode, "servDgst", "서비스 요약"),
                OpenApiNodeUtils.text(detailNode, "sprtCycNm", "지원 주기"),
                OpenApiNodeUtils.text(detailNode, "srvPvsnNm", "서비스 제공 형태"),
                OpenApiNodeUtils.text(detailNode, "rprsCtadr", "대표 문의처"),
                OpenApiNodeUtils.integerOrNull(detailNode, "inqNum", "조회수"),
                OpenApiNodeUtils.text(detailNode, "enfcBgngYmd"),
                OpenApiNodeUtils.text(detailNode, "enfcEndYmd"),
                OpenApiNodeUtils.text(detailNode, "bizChrDeptNm"),
                OpenApiNodeUtils.text(detailNode, "ctpvNm"),
                OpenApiNodeUtils.text(detailNode, "sggNm"),
                OpenApiNodeUtils.text(detailNode, "wlfareInfoOutlCn"),
                OpenApiNodeUtils.text(detailNode, "crtrYr"),
                OpenApiNodeUtils.text(detailNode, "tgtrDtlCn"),
                OpenApiNodeUtils.text(detailNode, "slctCritCn"),
                OpenApiNodeUtils.text(detailNode, "alwServCn"),
                OpenApiNodeUtils.text(detailNode, "sprtTrgtCn"),
                OpenApiNodeUtils.text(detailNode, "lastModYmd")
        );
    }

    public List<String> extractLifeStageValues(JsonNode node) {
        return OpenApiNodeUtils.splitValues(
                OpenApiNodeUtils.text(node, "lifeArray", "lifeNmArray")
        );
    }

    public List<String> extractHouseholdTypeValues(JsonNode node) {
        return OpenApiNodeUtils.splitValues(
                OpenApiNodeUtils.text(node, "trgterIndvdlArray", "trgterIndvdlNmArray")
        );
    }

    public List<String> extractInterestValues(JsonNode node) {
        return OpenApiNodeUtils.splitValues(
                OpenApiNodeUtils.text(node, "intrsThemaArray", "intrsThemaNmArray")
        );
    }

    public void mapDetailChildren(WelfareService service, JsonNode detailNode) {
        if (detailNode == null) {
            return;
        }
        mapApplications(service, detailNode);
        mapContacts(service, detailNode);
        mapLinks(service, detailNode);
        mapForms(service, detailNode);
        mapLaws(service, detailNode);
    }

    private void mapApplications(WelfareService service, JsonNode detailNode) {
        JsonNode applmetList = OpenApiNodeUtils.child(detailNode, "applmetList");
        if (applmetList != null) {
            int order = 0;
            for (JsonNode item : OpenApiNodeUtils.asList(applmetList)) {
                service.addApplication(WelfareApplication.builder()
                        .servSeCode(OpenApiNodeUtils.text(item, "servSeCode"))
                        .servSeDetailNm(OpenApiNodeUtils.text(item, "servSeDetailNm"))
                        .servSeDetailLink(OpenApiNodeUtils.text(item, "servSeDetailLink"))
                        .sortOrder(order++)
                        .build());
            }
            return;
        }
        String aplyMtdNm = OpenApiNodeUtils.text(detailNode, "aplyMtdNm");
        String aplyMtdCn = OpenApiNodeUtils.text(detailNode, "aplyMtdCn");
        if (aplyMtdNm != null || aplyMtdCn != null) {
            service.addApplication(WelfareApplication.builder()
                    .servSeDetailNm(aplyMtdNm)
                    .servSeDetailLink(aplyMtdCn)
                    .sortOrder(0)
                    .build());
        }
    }

    private void mapContacts(WelfareService service, JsonNode detailNode) {
        JsonNode list = OpenApiNodeUtils.child(detailNode, "inqplCtadrList");
        if (list == null) {
            return;
        }
        int order = 0;
        for (JsonNode item : OpenApiNodeUtils.asList(list)) {
            service.addContact(WelfareContact.builder()
                    .servSeCode(OpenApiNodeUtils.text(item, "servSeCode", "wlfareInfoDtlCd"))
                    .contactName(OpenApiNodeUtils.text(item, "servSeDetailNm", "wlfareInfoReldNm"))
                    .contactValue(OpenApiNodeUtils.text(item, "servSeDetailLink", "wlfareInfoReldCn"))
                    .sortOrder(order++)
                    .build());
        }
    }

    private void mapLinks(WelfareService service, JsonNode detailNode) {
        JsonNode list = OpenApiNodeUtils.child(detailNode, "inqplHmpgReldList");
        if (list == null) {
            return;
        }
        int order = 0;
        for (JsonNode item : OpenApiNodeUtils.asList(list)) {
            service.addLink(WelfareLink.builder()
                    .servSeCode(OpenApiNodeUtils.text(item, "servSeCode", "wlfareInfoDtlCd"))
                    .linkName(OpenApiNodeUtils.text(item, "servSeDetailNm", "wlfareInfoReldNm"))
                    .linkUrl(OpenApiNodeUtils.text(item, "servSeDetailLink", "wlfareInfoReldCn"))
                    .sortOrder(order++)
                    .build());
        }
    }

    private void mapForms(WelfareService service, JsonNode detailNode) {
        JsonNode list = OpenApiNodeUtils.child(detailNode, "basfrmList");
        if (list == null) {
            return;
        }
        int order = 0;
        for (JsonNode item : OpenApiNodeUtils.asList(list)) {
            service.addForm(WelfareForm.builder()
                    .formName(OpenApiNodeUtils.text(item, "servSeDetailNm", "wlfareInfoReldNm"))
                    .formUrl(OpenApiNodeUtils.text(item, "servSeDetailLink", "wlfareInfoReldCn"))
                    .sortOrder(order++)
                    .build());
        }
    }

    private void mapLaws(WelfareService service, JsonNode detailNode) {
        JsonNode list = OpenApiNodeUtils.child(detailNode, "baslawList");
        if (list == null) {
            return;
        }
        int order = 0;
        for (JsonNode item : OpenApiNodeUtils.asList(list)) {
            service.addLaw(WelfareLaw.builder()
                    .lawName(OpenApiNodeUtils.text(item, "servSeDetailNm", "wlfareInfoReldNm"))
                    .lawUrl(OpenApiNodeUtils.text(item, "servSeDetailLink", "wlfareInfoReldCn"))
                    .sortOrder(order++)
                    .build());
        }
    }
}
