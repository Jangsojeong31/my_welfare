package com.welfare.ingestion.api;

import com.welfare.ingestion.api.dto.IngestionBatchResponse;
import com.welfare.ingestion.api.dto.IngestionResultResponse;
import com.welfare.ingestion.application.WelfareIngestionService;
import com.welfare.security.JwtAuthenticationFilter;
import com.welfare.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IngestionController.class)
@AutoConfigureMockMvc(addFilters = false)
class IngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WelfareIngestionService welfareIngestionService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @DisplayName("파라미터 없이 복지 데이터를 전체 수집한다.")
    @Test
    void ingestAllWithoutParams() throws Exception {
        // given
        when(welfareIngestionService.ingestAll(null, null)).thenReturn(batchResponse());

        // when, then
        mockMvc.perform(post("/api/ingestion/welfare")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("복지 Open API 수집이 완료되었습니다."))
                .andExpect(jsonPath("$.data.national.listApiCd").value("NAW01"))
                .andExpect(jsonPath("$.data.national.detailApiCd").value("NAW02"))
                .andExpect(jsonPath("$.data.national.fetchedCount").value(10))
                .andExpect(jsonPath("$.data.national.insertedCount").value(4))
                .andExpect(jsonPath("$.data.national.skippedCount").value(5))
                .andExpect(jsonPath("$.data.national.failedCount").value(1))
                .andExpect(jsonPath("$.data.local.listApiCd").value("LCW01"))
                .andExpect(jsonPath("$.data.local.insertedCount").value(2));

        verify(welfareIngestionService).ingestAll(null, null);
    }

    @DisplayName("페이지 번호와 건수를 지정해 복지 데이터를 전체 수집한다.")
    @Test
    void ingestAllWithParams() throws Exception {
        // given
        when(welfareIngestionService.ingestAll(1, 100)).thenReturn(batchResponse());

        // when, then
        mockMvc.perform(post("/api/ingestion/welfare")
                        .param("pageNo", "1")
                        .param("numOfRows", "100")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("복지 Open API 수집이 완료되었습니다."))
                .andExpect(jsonPath("$.data.national.fetchedCount").value(10))
                .andExpect(jsonPath("$.data.local.fetchedCount").value(8));

        verify(welfareIngestionService).ingestAll(1, 100);
    }

    @DisplayName("중앙부처 복지 데이터를 수집한다.")
    @Test
    void ingestNational() throws Exception {
        // given
        when(welfareIngestionService.ingestNationalWelfare(any(), any()))
                .thenReturn(result("NAW01", "NAW02", 10, 4, 5, 1));

        // when, then
        mockMvc.perform(post("/api/ingestion/welfare/national")
                        .param("pageNo", "2")
                        .param("numOfRows", "50")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("중앙부처 복지 수집이 완료되었습니다."))
                .andExpect(jsonPath("$.data.listApiCd").value("NAW01"))
                .andExpect(jsonPath("$.data.detailApiCd").value("NAW02"))
                .andExpect(jsonPath("$.data.fetchedCount").value(10))
                .andExpect(jsonPath("$.data.insertedCount").value(4))
                .andExpect(jsonPath("$.data.skippedCount").value(5))
                .andExpect(jsonPath("$.data.failedCount").value(1));

        verify(welfareIngestionService).ingestNationalWelfare(2, 50);
    }

    @DisplayName("지자체 복지 데이터를 수집한다.")
    @Test
    void ingestLocal() throws Exception {
        // given
        when(welfareIngestionService.ingestLocalWelfare(any(), any()))
                .thenReturn(result("LCW01", "LCW02", 8, 2, 6, 0));

        // when, then
        mockMvc.perform(post("/api/ingestion/welfare/local")
                        .param("pageNo", "1")
                        .param("numOfRows", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("지자체 복지 수집이 완료되었습니다."))
                .andExpect(jsonPath("$.data.listApiCd").value("LCW01"))
                .andExpect(jsonPath("$.data.detailApiCd").value("LCW02"))
                .andExpect(jsonPath("$.data.fetchedCount").value(8))
                .andExpect(jsonPath("$.data.insertedCount").value(2))
                .andExpect(jsonPath("$.data.skippedCount").value(6))
                .andExpect(jsonPath("$.data.failedCount").value(0));

        verify(welfareIngestionService).ingestLocalWelfare(1, 20);
    }

    @DisplayName("전체 수집 시 페이지 번호는 1 이상이어야 한다.")
    @Test
    void ingestAllWithInvalidPageNo() throws Exception {
        expectInvalidParam("/api/ingestion/welfare", "pageNo", "0", "ingestAll.pageNo: must be greater than or equal to 1");
    }

    @DisplayName("전체 수집 시 한 페이지 결과 수는 1 이상이어야 한다.")
    @Test
    void ingestAllWithTooSmallNumOfRows() throws Exception {
        expectInvalidParam("/api/ingestion/welfare", "numOfRows", "0", "ingestAll.numOfRows: must be greater than or equal to 1");
    }

    @DisplayName("전체 수집 시 한 페이지 결과 수는 500 이하여야 한다.")
    @Test
    void ingestAllWithTooLargeNumOfRows() throws Exception {
        expectInvalidParam("/api/ingestion/welfare", "numOfRows", "501", "ingestAll.numOfRows: must be less than or equal to 500");
    }

    @DisplayName("중앙부처 수집 시 페이지 번호는 1 이상이어야 한다.")
    @Test
    void ingestNationalWithInvalidPageNo() throws Exception {
        expectInvalidParam("/api/ingestion/welfare/national", "pageNo", "0", "ingestNational.pageNo: must be greater than or equal to 1");
    }

    @DisplayName("중앙부처 수집 시 한 페이지 결과 수는 500 이하여야 한다.")
    @Test
    void ingestNationalWithTooLargeNumOfRows() throws Exception {
        expectInvalidParam("/api/ingestion/welfare/national", "numOfRows", "501", "ingestNational.numOfRows: must be less than or equal to 500");
    }

    @DisplayName("지자체 수집 시 페이지 번호는 1 이상이어야 한다.")
    @Test
    void ingestLocalWithInvalidPageNo() throws Exception {
        expectInvalidParam("/api/ingestion/welfare/local", "pageNo", "0", "ingestLocal.pageNo: must be greater than or equal to 1");
    }

    @DisplayName("지자체 수집 시 한 페이지 결과 수는 500 이하여야 한다.")
    @Test
    void ingestLocalWithTooLargeNumOfRows() throws Exception {
        expectInvalidParam("/api/ingestion/welfare/local", "numOfRows", "501", "ingestLocal.numOfRows: must be less than or equal to 500");
    }

    private void expectInvalidParam(String uri, String name, String value, String message) throws Exception {
        mockMvc.perform(post(uri)
                        .param(name, value)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"));

        verifyNoInteractions(welfareIngestionService);
    }

    private IngestionBatchResponse batchResponse() {
        return IngestionBatchResponse.builder()
                .national(result("NAW01", "NAW02", 10, 4, 5, 1))
                .local(result("LCW01", "LCW02", 8, 2, 6, 0))
                .build();
    }

    private IngestionResultResponse result(
            String listApiCd,
            String detailApiCd,
            int fetchedCount,
            int insertedCount,
            int skippedCount,
            int failedCount
    ) {
        return IngestionResultResponse.builder()
                .listApiCd(listApiCd)
                .detailApiCd(detailApiCd)
                .fetchedCount(fetchedCount)
                .insertedCount(insertedCount)
                .skippedCount(skippedCount)
                .failedCount(failedCount)
                .build();
    }
}
