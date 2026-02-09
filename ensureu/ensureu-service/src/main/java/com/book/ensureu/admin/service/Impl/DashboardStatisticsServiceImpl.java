package com.book.ensureu.admin.service.Impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.DashboardStatisticsDto;
import com.book.ensureu.admin.service.DashboardStatisticsService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.model.Subscription;
import com.book.ensureu.repository.FreePaperCollectionRepository;
import com.book.ensureu.repository.PaidPaperCollectionRepository;
import com.book.ensureu.repository.SubscriptionRepository;
import com.book.ensureu.repository.UserRepository;

@Service
public class DashboardStatisticsServiceImpl implements DashboardStatisticsService {

    @Autowired
    private FreePaperCollectionRepository freePaperCollectionRepository;

    @Autowired
    private PaidPaperCollectionRepository paidPaperCollectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public DashboardStatisticsDto getDashboardStatistics() {
        long totalFreePapers = freePaperCollectionRepository.count();
        long totalPaidPapers = paidPaperCollectionRepository.count();
        long totalUsers = userRepository.count();
        long activeSubscriptions = subscriptionRepository.countByState(Subscription.SubscriptionState.ACTIVE);

        Map<String, Long> freePapersByCategory = new LinkedHashMap<>();
        Map<String, Long> paidPapersByCategory = new LinkedHashMap<>();
        for (PaperCategory category : PaperCategory.values()) {
            long freeCount = freePaperCollectionRepository.countByPaperCategory(category);
            long paidCount = paidPaperCollectionRepository.countByPaperCategory(category);
            freePapersByCategory.put(category.name(), freeCount);
            paidPapersByCategory.put(category.name(), paidCount);
        }

        Map<String, Long> freePapersByState = new LinkedHashMap<>();
        Map<String, Long> paidPapersByState = new LinkedHashMap<>();
        for (PaperStateStatus status : PaperStateStatus.values()) {
            long freeCount = freePaperCollectionRepository.countByPaperStateStatus(status);
            long paidCount = paidPaperCollectionRepository.countByPaperStateStatus(status);
            freePapersByState.put(status.name(), freeCount);
            paidPapersByState.put(status.name(), paidCount);
        }

        return DashboardStatisticsDto.builder()
                .totalFreePapers(totalFreePapers)
                .totalPaidPapers(totalPaidPapers)
                .totalPapers(totalFreePapers + totalPaidPapers)
                .totalUsers(totalUsers)
                .activeSubscriptions(activeSubscriptions)
                .freePapersByCategory(freePapersByCategory)
                .paidPapersByCategory(paidPapersByCategory)
                .freePapersByState(freePapersByState)
                .paidPapersByState(paidPapersByState)
                .build();
    }
}
