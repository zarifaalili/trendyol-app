package org.example.trendyolfinalproject.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.PaymentTransaction;
import org.example.trendyolfinalproject.dao.repository.PaymentTransactionRepository;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.service.AdminService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduler {

    private final AdminService adminService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void payToSellersPeriodically() {
        adminService.paySellersForToday();
    }


    @Scheduled(cron = "*/30 * * * * *")
    public void checkPaymentTransaction() {
        log.info("PaymentScheduler: checkPaymentTransaction started ");
        List<PaymentTransaction> paymentTransactions = paymentTransactionRepository.findAllByStatus(Status.PENDING);

        List<PaymentTransaction> list = new ArrayList<>();
        for (PaymentTransaction p : paymentTransactions) {
            p.setStatus(Status.SUCCESS);
            p.setProviderResponse("Confirmed by payment provider");
            list.add(p);

        }
        paymentTransactionRepository.saveAll(list);
        log.info("PaymentScheduler: checkPaymentTransaction finished ");

    }
}

