ComponentManager componentManager = ComponentManager.getInstance()
IssueManager issueManager = componentManager.getIssueManager()
SubTaskManager subTaskManager = componentManager.getSubTaskManager()
JiraAuthenticationContext authenticationContext = componentManager.getJiraAuthenticationContext()
IssueFactory issueFactory = componentManager.getIssueFactory();
ProjectRoleManager projectRoleManager = componentManager.getComponentInstanceOfType(com.atlassian.jira.security.roles.ProjectRoleManager.class)
CustomFieldManager customFieldManager = componentManager.getCustomFieldManager()
ProjectManager projectManager = componentManager.getProjectManager()

GenericValue testCaseIssueType = subTaskManager.getSubTasksIssueTypes().find { it.name == 'Sub-task'}
MutableIssue subtaskIssueObject = issueFactory.getIssue()
subtaskIssueObject.setProject(projectManager.getProject(issue.getProjectObject().getId()))
subtaskIssueObject.setIssueType(subTaskManager.getSubTasksIssueTypes().find {it.name == 'Sub-task'})
subtaskIssueObject.setSummary(issue.getSummary() + "-" + componentName)


CustomFieldManager cfManager = ComponentManager.getInstance().getCustomFieldManager()
CustomField customField = cfManager.getCustomFieldObject(10000)
Object customFieldValue = issue.getCustomFieldValue(customField)
CustomFieldManager cfm = ComponentManager.getInstance().getCustomFieldManager();

subtaskIssueObject.setCustomFieldValue(cfm.getCustomFieldObject(10000),customFieldValue)
subtaskIssueObject.setDueDate(issue.getDueDate())

subtaskIssueObject.setAssignee(authenticationContext.getUser())
subtaskIssueObject.setReporter(authenticationContext.getUser())


Map params = new HashMap();
params.put("issue", subtaskIssueObject);

def wasIndexing = ImportUtils.indexIssues
ImportUtils.indexIssues = true

GenericValue subTask = issueManager.createIssue(authenticationContext.getUser(), params);
subTaskManager.createSubTaskIssueLink(issue.getGenericValue(), subTask, authenticationContext.getUser());

ImportUtils.indexIssues = wasIndexing
