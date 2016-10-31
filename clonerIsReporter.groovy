def componentManager = ComponentManager.getInstance()
def currentUser = componentManager.jiraAuthenticationContext?.user
def permissionManager = componentManager.getPermissionManager()

if (permissionManager.hasPermission(Permissions.ASSIGNABLE_USER, issue, currentUser)) {
   MutableIssue mi = (MutableIssue)issue
   mi.setReporter(currentUser)
   mi.store()
}
