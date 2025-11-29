package com.itu.gest_emp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itu.gest_emp.model.ChatbotConversationRH;
import com.itu.gest_emp.service.ChatbotService;

@Controller
@RequestMapping("/employee/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @GetMapping
    public String chatbotPage(Model model) {
        try {
            List<ChatbotConversationRH> history = chatbotService.getConversationHistory();
            model.addAttribute("history", history);
            return "employee/chatbot";
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/ask")
    @ResponseBody
    public ChatbotService.ChatbotResponse askQuestion(@RequestParam String question) {
        return chatbotService.processQuestion(question);
    }

    @GetMapping("/history")
    @ResponseBody
    public List<ChatbotConversationRH> getHistory() {
        return chatbotService.getConversationHistory();
    }
}
