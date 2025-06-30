package com.example.internetbanking.controller;

import com.example.internetbanking.service.BankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    private BankingService bankingService;

    @Autowired
    public PageController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("cards", bankingService.getAllCards());
        return "index";
    }
}
