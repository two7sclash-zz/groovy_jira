
package com.onresolve.jira.groovy.test.scriptfields.scripts

import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField

import java.sql.Timestamp
import java.io.*;
import java.util.concurrent.*;


log.debug("Test data");
/**
 * Set max DueDate of all subtasks to parent issue
 */
//Get parent issue
Issue issue = (Issue) event.issue;
//Check, that it is a subtask type issue
if(!issue.isSubTask())
    return

MutableIssue parentIssue = issue.getParentObject();
boolean containsLabel = false;

for (String label : parentIssue.getLabels()) {
    if (label.contains("complex")){
        containsLabel = true;
        break;
    }}
if(!containsLabel)
    return

//variables should not be defined into loops
MutableIssue linkedIssue;
def customFieldManager = ComponentAccessor.getCustomFieldManager()
CustomField offset = customFieldManager.getCustomFieldObject("customfield_27032")
long number = 0;
IssueManager issueManager = ComponentAccessor.getIssueManager();
User curUser = ComponentAccessor.getJiraAuthenticationContext().getUser().getDirectoryUser();

ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.id).each {issueLink ->
    if (issueLink.issueLinkType.name == "Sequence") {
        linkedIssue = issueLink.destinationObject

        log.info "got here"

        number = (long) linkedIssue.getCustomFieldValue(offset)

        log.debug "Issue type is : ${number}"

        Timestamp dueDate = issue.getDueDate();
        //I think that it is better to use Calendar to add only working days
        dueDate.setTime(dueDate.getTime() + TimeUnit.DAYS.toMillis(number));

        if( ( linkedIssue.getDueDate() == null ) || (linkedIssue.getDueDate().before(dueDate))) {
            linkedIssue.setDueDate(dueDate)

            //linked issue should be updated into a loop
            issueManager.updateIssue(
                    curUser
                    , linkedIssue
                    , EventDispatchOption.ISSUE_UPDATED
                    , false)
        }
    }
}
