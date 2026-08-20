package com.nexus.platform.controller;

import com.nexus.platform.entity.Position;
import com.nexus.platform.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trading")
@CrossOrigin(origins = "*")
public class TradeController {

    @Autowired
    private TradingService tradingService;

    // 1. เปิดคำสั่งซื้อขายใหม่ (Manual Trade)
    @PostMapping("/order")
    public ResponseEntity<?> executeOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> payload) {
        try {
            String symbol = (String) payload.get("symbol");
            String side = (String) payload.get("side");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            Integer leverage = Integer.parseInt(payload.get("leverage").toString());
            String accountType = (String) payload.getOrDefault("accountType", "DEMO");

            Position position = tradingService.createOrder(token, symbol, side, amount, leverage, accountType);
            return ResponseEntity.ok(position);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. ปิดสถานะการเทรด (Close Position)
    @PostMapping("/close/{positionId}")
    public ResponseEntity<?> closePosition(
            @RequestHeader("Authorization") String token,
            @PathVariable String positionId,
            @RequestBody Map<String, Object> payload) {
        try {
            BigDecimal exitPrice = new BigDecimal(payload.get("exitPrice").toString());
            Position closedPosition = tradingService.closePosition(token, positionId, exitPrice);
            return ResponseEntity.ok(closedPosition);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. ดึงรายการออเดอร์ที่เปิดอยู่ทั้งหมด
    @GetMapping("/positions")
    public ResponseEntity<?> getActivePositions(@RequestHeader("Authorization") String token) {
        List<Position> positions = tradingService.getActivePositionsByUser(token);
        return ResponseEntity.ok(positions);
    }
}
