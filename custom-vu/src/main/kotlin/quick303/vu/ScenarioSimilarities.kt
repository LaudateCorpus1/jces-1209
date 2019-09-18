package quick303.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.ProjectSummaryAction
import com.atlassian.performance.tools.jiraactions.api.action.ViewDashboardAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveProjectMemory
import quick303.vu.action.WorkAnIssue
import quick303.vu.page.AbstractIssuePage
import java.util.Collections.nCopies

class ScenarioSimilarities(
    private val jira: WebJira,
    private val seededRandom: SeededRandom,
    private val meter: ActionMeter
) {

    val jqlMemory = AdaptiveJqlMemory(seededRandom)
        .also { it.remember(listOf("order by created DESC")) } // work around https://ecosystem.atlassian.net/browse/JPERF-573
        .also { it.remember(listOf("text ~ lorem")) }
    val issueKeyMemory = AdaptiveIssueKeyMemory(seededRandom)
    val projectMemory = AdaptiveProjectMemory(seededRandom)

    fun assembleScenario(
        issuePage: AbstractIssuePage,
        createIssue: Action,
        searchWithJql: Action,
        browseProjects: Action
    ): List<Action> = assembleScenario(
        createIssue = createIssue,
        searchWithJql = searchWithJql,
        workAnIssue = WorkAnIssue(
            issuePage = issuePage,
            jira = jira,
            meter = meter,
            issueKeyMemory = issueKeyMemory,
            random = seededRandom,
            editProbability = 0.10f,
            commentProbability = 1.00f
        ),
        projectSummary = ProjectSummaryAction(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        ),
        viewDashboard = ViewDashboardAction(
            jira = jira,
            meter = meter
        ),
        browseProjects = browseProjects
    )

    private fun assembleScenario(
        createIssue: Action,
        searchWithJql: Action,
        workAnIssue: Action,
        projectSummary: Action,
        viewDashboard: Action,
        browseProjects: Action
    ): List<Action> = mapOf(
        createIssue to 50, // TODO: 5
        searchWithJql to 20,
        workAnIssue to 2, // TODO: 55
        projectSummary to 5,
       // viewDashboard to 10,
        browseProjects to 5
    )
        .map { (action, proportion) -> nCopies(proportion, action) }
        .flatten()
        .shuffled(seededRandom.random)
}
