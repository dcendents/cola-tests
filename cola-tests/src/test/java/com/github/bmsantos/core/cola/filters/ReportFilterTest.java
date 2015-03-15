package com.github.bmsantos.core.cola.filters;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import gherkin.deps.com.google.gson.Gson;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;
import com.github.bmsantos.core.cola.formatter.FeaturesLoader;
import com.github.bmsantos.core.cola.formatter.ReportDetails;
import com.github.bmsantos.core.cola.story.annotations.Feature;

public class ReportFilterTest {

    private static final String REPORT_NAME = "report-me";
    private static final String REPORT_ARGS = "arg1 arg2";
    private ReportFilter uut;

    @Before
    public void setUp() {
        uut = new ReportFilter();
    }

    @Test
    public void shouldGetFeatureReportDetails() {
        // Given
        final FeatureDetails feature = FeaturesLoader.loadFeaturesFrom(ReportFeatureClass.class).get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(false));
        final ReportDetails reportDetails = feature.getReports().get(0);
        assertThat(reportDetails.getReport(), is(REPORT_NAME));
        assertThat(reportDetails.getArguments(), is(REPORT_ARGS));
    }

    @Test
    public void shouldGetScenarioReportDetails() {
        // Given
        final FeatureDetails feature = FeaturesLoader.loadFeaturesFrom(ReportScenarioClass.class).get(0);

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(false));
        final ReportDetails reportDetails = feature.getScenarios().get(0).getReports().get(0);
        assertThat(reportDetails.getReport(), is(REPORT_NAME));
        assertThat(reportDetails.getArguments(), is(REPORT_ARGS));
    }

    @Test
    public void shouldNotHaveComments() {
        // Given
        final FeatureDetails feature = FeaturesLoader.loadFeaturesFrom(NormalFeatureClass.class).get(0);
        new Gson().toJson(Collections.emptyList());

        // When
        final boolean result = uut.filtrate(feature);

        // Then
        assertThat(result, is(false));
        assertThat(feature.getReports().isEmpty(), is(true));
        assertThat(feature.getScenarios().get(0).getReports().isEmpty(), is(true));
    }

    @Test
    public void shouldParseReportDetailsOnly() {
        // Given
        final FeatureDetails feature = FeaturesLoader.loadFeaturesFrom(MultilineCommentClass.class).get(0);

        // When
        uut.filtrate(feature);

        // Then
        ReportDetails reportDetails = feature.getReports().get(0);
        assertThat(reportDetails.getReport(), is(REPORT_NAME));
        assertThat(reportDetails.getArguments(), is(REPORT_ARGS));

        reportDetails = feature.getScenarios().get(0).getReports().get(0);
        assertThat(reportDetails.getReport(), is(REPORT_NAME));
        assertThat(reportDetails.getArguments(), is(REPORT_ARGS));
    }

    private static class ReportFeatureClass {
        @Feature
        private final String skippedFeature =
        "# report-me> arg1 arg2\n"
            + "Feature: Load feature\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }

    private static class ReportScenarioClass {
        @Feature
        private final String skippedScenario =
        "Feature: Load feature\n"
            + "#report-me> arg1 arg2\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }

    private static class NormalFeatureClass {
        @Feature
        private final String normalFeature =
        "Feature: Load feature\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }

    private static class MultilineCommentClass {
        @Feature
        private final String normalFeature =
        "# Please check this out\n"
            + "# report-me> arg1 arg2\n"
            + "@tagMe @andYou\n"
            + "Feature: Load feature\n"
            + "# Please check this out\n"
            + "# report-me> arg1 arg2\n"
            + "@tagMe @andYou\n"
            + "Scenario: Should have scenario steps\n"
            + "Given A\n"
            + "When B\n"
            + "Then C\n";
    }
}