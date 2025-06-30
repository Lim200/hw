package com.example.internetbanking.controller;

import com.example.internetbanking.model.Card;
import com.example.internetbanking.model.Client;
import com.example.internetbanking.service.BankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;

import java.util.Optional;

@Tag(name = "Банковский API", description = "Операции с клиентами, картами и переводами")
@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
public class BankingController {


    private  BankingService bankingService;

    @Autowired
    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @Operation(
            summary = "Получить клиента по ID",
            description = "Возвращает информацию о клиенте с указанным ID, если он существует"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент найден"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @GetMapping("/client/{id}")
    public Optional<Client> getClient(
    @Parameter(description = "ID клиента", example = "123")
    @PathVariable Long id) {
        return bankingService.getClientById(id);
    }
    @Operation(
            summary = "Получить по номеру карты ID клиента и баланс",
            description = "Возвращает информацию о карте с указанным: номера карты, ID, баланса если он существует"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиент найден"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })

    @GetMapping("/card/{cardNumber}")
    public Optional<Card> getCard(@Parameter(description = "Номер карты", example = "4000 1000 5000 2000")
                                      @PathVariable String cardNumber) {
        return bankingService.getCardByNumber(cardNumber);
    }

    @Operation(
            summary = "Перевод средств",
            description = "Выполняет перевод указанной суммы от одного клиента к другому"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Недопустимые параметры или недостаточно средств")
    })
    @PostMapping("/transfer")
    public String transfer(
            @Parameter(description = "Номер отправителя", example = "1234567890")
            @RequestParam String from,

            @Parameter(description = "Номер получателя", example = "0987654321")
            @RequestParam String to,

            @Parameter(description = "Сумма перевода", example = "100.50")
            @RequestParam double amount) {
        return bankingService.transfer(from, to, amount);
    }

    @Operation(
            summary = "Загрузка файла",
            description = "Загружает файл на сервер и сохраняет его в директорию `/uploads`"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно загружен"),
            @ApiResponse(responseCode = "400", description = "Файл не был выбран или произошла ошибка")
    })
    @PostMapping("/upload")
    public String handleFileUpload(
            @Parameter(description = "Файл для загрузки")
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "Файл не выбран для загрузки.";
        }

        try {
            // Абсолютный путь к папке uploads
            String uploadDir = new File("uploads").getAbsolutePath();
            System.out.println("Абсолютный путь к папке загрузки: " + uploadDir);

            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                boolean created = uploadFolder.mkdirs();
                if (!created) {
                    return "Ошибка при создании папки для загрузки файлов.";
                }
            }

            // Сохраняем файл в абсолютный путь
            File dest = new File(uploadFolder, file.getOriginalFilename());
            System.out.println("Путь для сохранения файла: " + dest.getAbsolutePath());

            file.transferTo(dest);

            return "Файл " + file.getOriginalFilename() + " успешно загружен!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при загрузке файла: " + e.getMessage();
        }
    }
}
