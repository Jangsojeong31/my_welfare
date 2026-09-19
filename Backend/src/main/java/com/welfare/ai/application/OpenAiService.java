package com.welfare.ai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.welfare.ai.api.dto.WelfareAiSearchCondition;
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
        String prompt = """
                사용자의 복지서비스 검색 질문을 분석하여
                검색에 필요한 조건을 JSON으로 추출하세요.

                사용자가 언급하지 않은 정보는 null로 반환하세요.

                반드시 다음 형식의 JSON만 반환하세요.

                {
                  "age": number | null,
                  "region": string | null,
                  "employment": string | null,
                  "housing": string | null,
                  "income": string | null,
                  "keywords": string[]
                }
                
                검색 키워드는 사용자의 질문에서 직접 언급된 단어뿐 아니라 검색에 도움이 되는 관련 키워드도 포함하세요.
                검색 키워드에 '복지' 또는 '복지 서비스'가 포함된 단어는 제외해주세요.

                사용자 질문:
                %s
                """.formatted(question);

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

        String prompt = """
            당신은 복지서비스 검색 결과를 설명하는 AI입니다.

            사용자의 질문:
            %s

            사용자의 검색 조건:
            나이: %s
            지역: %s
            취업상태: %s
            주거형태: %s
            소득: %s
            검색키워드: %s

            아래는 관련성 상위 복지서비스입니다.
            이 목록의 서비스만 설명하세요.

            ============================
            복지서비스 검색 결과
            ============================

            %s

            다음 규칙을 반드시 지키세요.

            1. 반드시 제공된 복지서비스 데이터만을 근거로 답변하세요.

            2. 데이터에 존재하지 않는 지원금액,
               소득기준, 나이조건, 신청기간 등을
               임의로 생성하지 마세요.

            3. 아래 목록은 관련성 높은 순입니다.
               위에서부터 최대 3개 서비스만 설명하세요.
               목록에 없는 서비스는 언급하지 마세요.

            4. 각 복지서비스가 사용자의 질문과
               어떤 관련이 있는지 간단하게 설명하세요.

            5. 신청방법 정보가 존재하는 경우에만
               신청방법을 설명하세요.

            6. 확인할 수 없는 정보는
               "제공된 정보에서 확인되지 않습니다."
               라고 표현하세요.

            7. 답변은 한국어로 작성하세요.

            8. 복지서비스의 실제 지원 대상 여부를
               확정적으로 판단하지 마세요.
               "관련성이 높습니다",
               "확인해볼 수 있습니다"와 같이 표현하세요.

            9. 각 서비스를 설명한 뒤에는 반드시
               [자세한 내용 보기](/welfare/{서비스 UUID})
               형식의 마크다운 링크를 넣으세요.
               내부상세경로만 사용하고, 복지로 외부 상세링크는 사용하지 마세요.

            답변은 사용자가 이해하기 쉽게
            3~5문장 정도로 작성하세요.
            """.formatted(
                question,
                condition.getAge(),
                condition.getRegion(),
                condition.getEmployment(),
                condition.getHousing(),
                condition.getIncome(),
                condition.getKeywords(),
                welfareData
        );

        return callOpenAi(prompt);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
