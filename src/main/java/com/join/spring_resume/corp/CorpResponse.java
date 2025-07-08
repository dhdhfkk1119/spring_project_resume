package com.join.spring_resume.corp;

import lombok.AllArgsConstructor;
import lombok.Data;

public class CorpResponse {
    @Data
    @AllArgsConstructor
    public static class CorpDTO {
        private Long corpIdx;
        private String corpName;

        public static CorpDTO fromEntity(Corp corp) {
            return new CorpDTO(
                    corp.getCorpIdx(),
                    corp.getCorpName()
            );
        }
    }
}
