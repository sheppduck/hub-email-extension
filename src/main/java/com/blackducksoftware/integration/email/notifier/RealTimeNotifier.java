/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.email.notifier;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.extension.config.ExtensionInfo;
import com.blackducksoftware.integration.email.model.DateRange;
import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.service.EmailMessagingService;
import com.blackducksoftware.integration.hub.dataservices.DataServicesFactory;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class RealTimeNotifier extends AbstractDigestNotifier {

    private final Logger logger = LoggerFactory.getLogger(RealTimeNotifier.class);

    private final String lastRunPath;

    public RealTimeNotifier(final ExtensionProperties extensionProperties, final EmailMessagingService emailMessagingService,
            final DataServicesFactory dataServicesFactory, final ExtensionInfo extensionInfoData) {
        super(extensionProperties, emailMessagingService, dataServicesFactory, extensionInfoData);
        lastRunPath = getExtensionProperties().getNotifierVariableProperties()
                .get(getNotifierPropertyKey() + ".lastrun.file");

    }

    @Override
    public DateRange createDateRange() {
        final Date endDate = new Date();
        Date startDate = endDate;
        try {
            final File lastRunFile = new File(lastRunPath);
            if (lastRunFile.exists()) {
                final String lastRunValue = FileUtils.readFileToString(lastRunFile, "UTF-8");
                startDate = RestConnection.parseDateString(lastRunValue);
                startDate = new Date(startDate.getTime());
            } else {
                startDate = endDate;
            }
            FileUtils.write(lastRunFile, RestConnection.formatDate(endDate), "UTF-8");
        } catch (final Exception e) {
            logger.error("Error creating date range", e);
        }
        return new DateRange(startDate, endDate);
    }

    @Override
    public String getNotifierPropertyKey() {
        return "realTimeDigest";
    }

    @Override
    public String getCategory() {
        return "Real Time";
    }
}
