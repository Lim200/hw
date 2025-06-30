package com.example.internetbanking.service;

import com.example.internetbanking.model.Card;
import com.example.internetbanking.model.Client;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BankingService {

    private final Map<Long, Client> clients = new HashMap<>();
    private final Map<String, Card> cards = new HashMap<>();

    public BankingService() {
        // Инициализация данных
        Client client1 = new Client(1L, "Иван Иванов");
        Client client2 = new Client(2L, "Мария Смирнова");

        clients.put(client1.getId(), client1);
        clients.put(client2.getId(), client2);

        cards.put("1111-2222-3333-4444", new Card("1111-2222-3333-4444", client1.getId(), 5000.0));
        cards.put("5555-6666-7777-8888", new Card("5555-6666-7777-8888", client2.getId(), 3000.0));
    }

    public Optional<Client> getClientById(Long id) {
        return Optional.ofNullable(clients.get(id));
    }

    public Optional<Card> getCardByNumber(String cardNumber) {
        return Optional.ofNullable(cards.get(cardNumber));
    }

    public String transfer(String fromCardNumber, String toCardNumber, double amount) {
        Card fromCard = cards.get(fromCardNumber);
        Card toCard = cards.get(toCardNumber);

        if (fromCard == null || toCard == null) {
            return "Одна из карт не найдена";
        }

        if (fromCard.getBalance() < amount) {
            return "Недостаточно средств";
        }

        fromCard.setBalance(fromCard.getBalance() - amount);
        toCard.setBalance(toCard.getBalance() + amount);

        return "Перевод успешно выполнен";
    }

    public Collection<Card> getAllCards() {
        return cards.values();
    }
}
