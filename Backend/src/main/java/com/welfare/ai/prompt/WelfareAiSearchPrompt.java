package com.welfare.ai.prompt;

import com.welfare.ai.api.dto.WelfareAiSearchCondition;

public final class WelfareAiSearchPrompt {

    private WelfareAiSearchPrompt() {
    }

    /** 사용자의 자연어 질문을 복지 검색 조건 JSON으로 추출하는 프롬프트 */
    public static String extractSearchCondition(String question) {
        return """
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
    }

    /** 검색 결과를 바탕으로 사용자에게 설명할 답변 프롬프트 */
    public static String generateAnswer(
            String question,
            WelfareAiSearchCondition condition,
            String welfareData
    ) {
        return """
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
               위에서부터 최대 3개 서비스만 설명 하세요.
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
    }
}
