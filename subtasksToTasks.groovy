import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.security.Permissions
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.UpdateIssueRequest;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.UpdateIssueRequest;
def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser.class)
def searchProvider = ComponentAccessor.getComponent(SearchProvider.class)
def issueManager = ComponentAccessor.getIssueManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getUser()
def parentIdKey = "DIG-11514"
def subTaskIssueTypeId = "5"
//def query = jqlQueryParser.parseQuery("project = AR AND 'Business Requirement Id' ~ '5' AND (" + queryParamsString + ")")
def query = jqlQueryParser.parseQuery("issue in (DIG-16826, DIG-17281, DIG-14493, DIG-13886, DIG-17939, DIG-17071, DIG-15430, DIG-15409, DIG-15409)")
def results = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter())
results.getIssues().each {documentIssue ->
    //log.debug(documentIssue.key)
    def issue = issueManager.getIssueObject(documentIssue.id)
    changeToSubTaskAndLink(parentIdKey, documentIssue.key, subTaskIssueTypeId)
}
//Method to do all the work
def void changeToSubTaskAndLink(String parentId, String childId, String subTaskIssueTypeId)
{
    IssueManager issueManager = ComponentAccessor.getIssueManager()
    //Get the parent Issue
    def parent = issueManager.getIssueObject(parentId)
    //Get the child Issue
    def child = issueManager.getIssueObject(childId)
    //Change the child to the subtask type
    child.setIssueTypeId(subTaskIssueTypeId)
    //Update the issue
    ComponentAccessor.getIssueManager().updateIssue((ApplicationUser)ComponentManager.getInstance().jiraAuthenticationContext?.user, (MutableIssue)child, UpdateIssueRequest.builder().build())
    //Create the subtask link, if this is not done you'll end up with orphans
    ComponentAccessor.getSubTaskManager().createSubTaskIssueLink(parent, child, ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser())
}
