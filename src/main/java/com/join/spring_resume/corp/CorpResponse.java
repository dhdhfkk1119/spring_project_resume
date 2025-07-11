package com.join.spring_resume.corp;

import lombok.AllArgsConstructor;
import lombok.Data;

public class CorpResponse {
    @Data
    @AllArgsConstructor
    public static class CorpDTO {
        private Long corpIdx;
        private String corpName;
        private String corpImage;
        private boolean isBasicImage;

        public static CorpDTO fromEntity(Corp corp) {
            String corpImage = corp.getCorpImage();
            boolean isBasic = "basic.png".equals(corpImage); // null-safe

            return new CorpDTO(
                    corp.getCorpIdx(),
                    corp.getCorpName(),
                    corpImage,
                    isBasic
            );
        }
    }

}
