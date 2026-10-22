package com.welfare.ai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.welfare.ai.api.dto.WelfareAiSearchCondition;
import com.welfare.ai.prompt.WelfareAiSearchPrompt;
import com.welfare.welfare.domain.WelfareService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAIClient openAIClient;

    private final ObjectMapper objectMapper;

    @Value("${openai.model}")
    private String model;

    /** * 사용자의 자연어 질문을 분석하여 복지 검색 조건으로 변환한다. */
    public WelfareAiSearchCondition extractSearchCondition(String question) {
        String prompt = WelfareAiSearchPrompt.extractSearchCondition(question);

        String response = callOpenAi(prompt);

        return parseJson(response, WelfareAiSearchCondition.class);
    }

    /** * OpenAI Responses API 호출 */
    public String callOpenAi(String prompt) {
        try {
            ResponseCreateParams params =
                    ResponseCreateParams.builder()
                            .model(model)
                            .input(prompt)
                            .build();

            Response response =
                    openAIClient.responses()
                            .create(params);

            return response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .flatMap(content -> content.outputText().stream())
                    .map(outputText -> outputText.text())
                    .collect(Collectors.joining());

        } catch (Exception e) {

            throw new RuntimeException(
                    "OpenAI API 호출 중 오류가 발생했습니다.", e
            );
        }
    }

    /** * JSON 문자열을 Java 객체로 변환 */
    private <T> T parseJson(
            String json, Class<T> clazz
    ) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("OpenAI 응답 JSON 파싱에 실패했습니다.", e);
        }
    }

    /** * 검색 결과에 따른 AI 설명 */
    public String generateAnswer(
            String question,
            WelfareAiSearchCondition condition,
            List<WelfareService> services
    ) {

        // 검색 결과가 없는 경우
        if (services == null || services.isEmpty()) {
            return "입력하신 조건과 관련된 복지서비스를 찾지 못했습니다.";
        }

        String welfareData = services.stream()
                .map(service -> """

                    서비스 ID: %s
                    서비스명: %s
                    서비스 요약: %s
                    지역: %s
                    대상: %s
                    내부상세경로: /welfare/%s
                    상세링크: %s

                    """.formatted(
                        service.getServCd(),
                        service.getServNm(),
                        nullToEmpty(service.getServDgst()),
                        nullToEmpty(service.getCtpvNm()),
                        nullToEmpty(service.getTgtrDtlCn()),
                        service.getId(),
                        nullToEmpty(service.getServDtlLink())
                ))
                .collect(Collectors.joining("\n"));

        String prompt = WelfareAiSearchPrompt.generateAnswer(question, condition, welfareData);

        return callOpenAi(prompt);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
