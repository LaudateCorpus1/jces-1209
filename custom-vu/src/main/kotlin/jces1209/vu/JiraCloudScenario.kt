package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import jces1209.vu.action.*
import jces1209.vu.page.CloudIssueNavigator
import jces1209.vu.page.CloudIssuePage
import jces1209.vu.page.bars.topBar.cloud.CloudTopBar
import jces1209.vu.page.boards.browse.cloud.CloudBrowseBoardsPage
import jces1209.vu.page.customizecolumns.CloudColumnsEditor
import jces1209.vu.page.dashboard.cloud.CloudDashboardPage
import jces1209.vu.page.filters.CloudFiltersPage
import jces1209.vu.page.project.CloudProjectNavigatorPage
import org.openqa.selenium.By
import org.openqa.selenium.TakesScreenshot

class JiraCloudScenario : Scenario {
    override fun getLogInAction(
        jira: WebJira,
        meter: ActionMeter,
        userMemory: UserMemory
    ): Action {
        val user = userMemory
            .recall()
            ?: throw Exception("I cannot recall which user I am")
        return LogInWithAtlassianId(user, jira, meter)
    }

    override fun getActions(
        jira: WebJira,
        seededRandom: SeededRandom,
        actionMeter: ActionMeter
    ): List<Action> {
        val meter = ActionMeter.Builder(actionMeter)
            .appendPostMetricHook(
                TakeScreenshotHook.Builder(
                    jira.driver as TakesScreenshot
                ).build())
            .build()

        val similarities = ScenarioSimilarities(jira, seededRandom, meter)
        return similarities.assembleScenario(
            issuePage = CloudIssuePage(jira.driver),
            filtersPage = CloudFiltersPage(jira, jira.driver),
            browseBoardsPage = CloudBrowseBoardsPage(jira),
            createIssue = CreateAnIssue(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory,
                createIssueButtons = listOf(By.id("createGlobalItem"), By.id("createGlobalItemIconButton"))
            ),
            searchWithJql = SearchCloudFilter(
                jira = jira,
                meter = meter,
                filters = similarities.filtersMemory
            ),
            browseProjects = BrowseCloudProjects(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory
            ),
            workOnDashboard = WorkOnDashboard(
                jira = jira,
                meter = meter,
                projectKeyMemory = similarities.projectMemory,
                dashboardPage = CloudDashboardPage(jira)
            ),
            browseProjectIssues = BrowseProjectIssues(
                jira = jira,
                meter = meter,
                projectKeyMemory = similarities.projectMemory,
                browseProjectPage = CloudProjectNavigatorPage(jira.driver)
            ),
            customizeColumns = CustomizeColumns(
                jira = jira,
                meter = meter,
                columnsEditor = CloudColumnsEditor(jira.driver)
            ),
            issueNavigator = CloudIssueNavigator(jira.driver),
            topBar = CloudTopBar(jira.driver)
        )
    }
}

