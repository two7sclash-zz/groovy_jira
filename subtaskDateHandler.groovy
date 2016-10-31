import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue

import java.sql.Timestamp

/**
 * Set max DueDate of all subtasks to parent issue
 */
//Get parent issue
Issue issue = (Issue) event.issue;

//Check, that it is a subtask type issue
if(!issue.isSubTask())
    return

MutableIssue parentIssue = issue.getParentObject();

Set issueLabels = parentIssue.getLabels();
boolean containsLabel = false;


    for (String label : issueLabels) {
        if (label.contains("complex")){
            containsLabel = true;
        }}

if(!containsLabel)
	return

//Get max due date of all subtasks
Timestamp maxSubtakDueDate = parentIssue.getDueDate();
for(Issue subtask : parentIssue.getSubTaskObjects())
    if( ( subtask.getDueDate() != null ) && (subtask.getDueDate().after(maxSubtakDueDate)))
        maxSubtakDueDate = subtask.getDueDate();

//Update parent if requred
if( parentIssue.getDueDate().before(maxSubtakDueDate)){
    parentIssue.setDueDate(maxSubtakDueDate);


//This line is not requred into postfunction also
 ComponentAccessor.getIssueManager().updateIssue(ComponentAccessor.getJiraAuthenticationContext().getUser().getDirectoryUser(), parentIssue,EventDispatchOption.ISSUE_UPDATED, false)
}
