package com.mami.chatgptbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGptResponse {

    private List<Choice> choices;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Choice {
        private int index;
        private Message message;
    }
}
