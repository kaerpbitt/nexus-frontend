package com.nexus.platform.service;

import com.nexus.platform.entity.Position;
import com.nexus.platform.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class AiEngineService {

    @Autowired
    private PositionRepository positionRepository;

    // ระบบประมวลผลคำสั่ง AI Auto-Bot ทำงานอัตโนมัติแบบ Realtime
    @Async
    public void processAiAutoTrade(Long userId, String symbol, BigDecimal currentMarketPrice) {
        int confidence = (int) (Math.random() * 12 + 86); // Confidence 86% - 98%
        String side = Math.random() > 0.4 ? "BUY" : "SELL";
        BigDecimal tradeAmount = new BigDecimal("500.00");

        // บันทึกการเปิดออเดอร์ด้วย AI
        Position aiPosition = new Position();
        aiPosition.setId("ai_" + System.currentTimeMillis());
        aiPosition.setUserId(userId);
        aiPosition.setSymbol(symbol);
        aiPosition.setSide(side);
        aiPosition.setAmount(tradeAmount);
        aiPosition.setLeverage(20);
        aiPosition.setEntryPrice(currentMarketPrice);
        aiPosition.setAccountType("DEMO");
        aiPosition.setIsAiTrade(true);
        aiPosition.setStatus("OPEN");

        positionRepository.save(aiPosition);

        // จำลองระบบทำกำไรและปิดออเดอร์อัตโนมัติภายในช่วงเวลาสั้น (High Frequency Scalping)
        try {
            Thread.sleep(3000); // 3 วินาที
            BigDecimal profitMultiplier = new BigDecimal(Math.random() * 0.15 + 0.05); // กำไร 5%-20%
            BigDecimal pnl = tradeAmount.multiply(profitMultiplier).setScale(2, RoundingMode.HALF_UP);

            aiPosition.setStatus("CLOSED");
            aiPosition.setClosePrice(currentMarketPrice.add(pnl.divide(tradeAmount)));
            aiPosition.setPnl(pnl);

            positionRepository.save(aiPosition);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
