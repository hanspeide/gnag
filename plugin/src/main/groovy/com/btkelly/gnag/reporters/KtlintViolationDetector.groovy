/*
 * Copyright 2016 Bryan Kelly
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.btkelly.gnag.reporters

import com.btkelly.gnag.extensions.ReporterExtension
import com.btkelly.gnag.models.Violation
import com.btkelly.gnag.reporters.utils.CheckstyleParser
import com.btkelly.gnag.utils.ProjectHelper
import org.gradle.api.Project

/**
 * Created by bobbake4 on 4/1/16.
 */
class KtlintViolationDetector extends BaseViolationDetector {

    private final ReporterExtension ktlintReporterExtension
    private final ProjectHelper projectHelper = new ProjectHelper(project)
    private final CheckstyleParser checkstyleParser = new CheckstyleParser()

    KtlintViolationDetector(final Project project, final ReporterExtension reporterExtension) {
        super(project)
        this.ktlintReporterExtension = reporterExtension
    }

    @Override
    List<Violation> getDetectedViolations() {
        return checkstyleParser.parseViolations(project, reportFile().text, name())
    }

    @Override
    boolean isEnabled() {
        return ktlintReporterExtension.enabled
    }

    @Override
    String name() {
        return "ktlint"
    }

    @Override
    File reportFile() {
        return projectHelper.getKtlintReportFile()
    }

}
