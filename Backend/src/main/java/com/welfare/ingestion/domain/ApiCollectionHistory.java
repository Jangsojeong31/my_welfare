package com.welfare.ingestion.domain;

import com.welfare.common.domain.UuidIdentifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "api_collection_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiCollectionHistory extends UuidIdentifiable {

    @Column(name = "api_cd", length = 20)
    private String apiCd;

    @Column(name = "api_type", nullable = false, length = 20)
    private String apiType;

    @Column(name = "request_url", columnDefinition = "text")
    private String requestUrl;

    @Convert(converter = RequestParamsConverter.class)
    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "request_params", columnDefinition = "longtext")
    private Map<String, String> requestParams;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "response_message", columnDefinition = "text")
    private String responseMessage;

    @Column(name = "page_no")
    private Integer pageNo;

    @Column(name = "num_of_rows")
    private Integer numOfRows;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "success_yn", nullable = false, length = 1, columnDefinition = "char(1)")
    private String successYn;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Builder
    private ApiCollectionHistory(
            String apiCd,
            String apiType,
            String requestUrl,
            Map<String, String> requestParams,
            Integer responseCode,
            String responseMessage,
            Integer pageNo,
            Integer numOfRows,
            Integer totalCount,
            String successYn,
            LocalDateTime startedAt,
            LocalDateTime finishedAt
    ) {
        this.apiCd = apiCd;
        this.apiType = apiType;
        this.requestUrl = requestUrl;
        this.requestParams = requestParams;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.pageNo = pageNo;
        this.numOfRows = numOfRows;
        this.totalCount = totalCount;
        this.successYn = successYn;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }
}
