package com.book.ensureu.admin.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.admin.dto.DashboardStatisticsDto;
import com.book.ensureu.admin.service.DashboardStatisticsService;

@RestController
@RequestMapping("/admin/dashboard")
public class DashboardStatisticsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardStatisticsApi.class);

    @Autowired
    private DashboardStatisticsService dashboardStatisticsService;

    @CrossOrigin
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public DashboardStatisticsDto getDashboardStatistics() {
        LOGGER.info("getDashboardStatistics");
        try {
            return dashboardStatisticsService.getDashboardStatistics();
        } catch (Exception ex) {
            LOGGER.error("getDashboardStatistics error", ex);
        }
        return null;
    }
}
