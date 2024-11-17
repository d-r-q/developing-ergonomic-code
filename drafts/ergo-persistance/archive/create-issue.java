class IssueService {
    public Issue createIssue(Issue issueParams,
    List<LinkedIssue> linkedIssues,
    boolean checkRequiredFields,
    AdditionalIssueParams additionalIssueParams) {
        Issue issue = createIssue(issueParams,
        linkedIssues,
        issueParams.getCurrency(),
        checkRequiredFields,
        additionalIssueParams,
        false);
        
        boolean remindDeclarant = issue.getService().getNotificationSettings().getStatusOrAssigneeChangeForDeclarant();
        if (!issue.getService().isDisableFunctionalityForDeclarants() && remindDeclarant) {
            notifyService.notify(issueMessageService.createOnNewIssueForDeclarantMessage(issue, true),
            track(issue.getId(), issue.getClass()));
        }
        
        return issue;
    }
    
    private @NonNull Issue createIssue(@NonNull Issue issueParams,
    List<LinkedIssue> linkedIssues,
    @NonNull String currency,
    boolean checkRequiredFields,
    AdditionalIssueParams additionalIssueParams,
    boolean ignoreAutoAssign) {
        Service service = serviceService.findService(issueParams.getService().getId());
        Issue issue = new Issue(service, currency);
        Folder rootFolder = folderRepository.findRootFolder();
        // Temporary: issue root folder is mandatory,
        // but we can't create a folder while issue is not created too,
        // as the folder requires a persistent issue.
        issue.setRootFolder(rootFolder);
        
        validateIssueParams(issueParams, checkRequiredFields, service);
        
        if (issueParams.getFields() != null) {
            mergeFields(issue, issueParams, service);
        }
        
        fillDefaultValues(issue);
        fillDefaultReminders(issue);
        
        if (issueParams.getParentIssue() != null) {
            Issue parentIssue = issueRepository.findById(issueParams.getParentIssue().getId()).get();
            setParentIssue(issue, parentIssue);
        }
        
        issue.setSubsidiaryIssues(new ArrayList<>());
        issue.setOpponents(new ArrayList<>());
        issue.setIssueActivities(new ArrayList<>());
        issue.setProceedings(new ArrayList<>());
        issue.setIssueContacts(new ArrayList<>());
        issue.setPublishees(new ArrayList<>());
        issue.setProjectManagement(issueParams.getProjectManagement());
        issue.setChats(new ArrayList<>());
        issue.setTags(new ArrayList<>());
        
        ProjectManagement projectManagement = issue.getProjectManagement();
        boolean existProjectManagementDeadline = Optional.ofNullable(projectManagement)
        .map(ProjectManagement::getDeadline)
        .isPresent();
        if (authService.isAuthenticated() && existProjectManagementDeadline) {
            projectManagement.setLastDeadlineChangeUser(authService.fetchCurrentUserFromDatabase());
        }
        
        issue.setPriority(issueParams.isPriority());
        issue.setConfidential(issueParams.isConfidential());
        issue.setCtime(new Date());
        issue = issueRepository.save(issue);
        
        mergeIssueParams(issue, issueParams, true);
        mergePricing(issue, issueParams);
        for (LinkedIssue linkedIssue : linkedIssues) {
            linkedIssue.setIssue(issue);
        }
        linkedIssueService.saveLinkedIssues(linkedIssues);
        
        issue.setManagerRating(issue.getService().getDefaultManagerRating());
        updateIssueWorkload(issue);
        
        // TODO Why the save is here? Explanation is needed.
        issue = issueRepository.save(issue);
        if (issue.getService().getServiceProceeding() != null) {
            issue.setJudicialStatus(IssueResultStatus.NOT_COMPLETED);
            issue.setDisputeDirection(Issue.DisputeDirectionType.UNKNOWN);
            issue.setCanEditDisputeDirection(true);
        }
        mergeReminders(issue, issueParams.getReminders());
        issue.setAuthor(authService.isAuthenticated()
        ? authService.fetchCurrentUserFromDatabase()
        : issueParams.getAuthor());
        if (issueParams.getAssignee() != null) {
            issueAssigneeService.assigneeIssue(issue, userRepository.findById(issueParams.getAssignee().getId()).get());
        } else {
            issue.setAssignee(null);
        }
        
        contractHelper.initRelatedContractsForNewIssue(issueParams, additionalIssueParams, issue);
        
        // TODO Why the save is here? Explanation is needed.
        issue = issueRepository.save(issue);
        
        assignIssueIfNeeded(issue, issueParams, true, false, ignoreAutoAssign);
        
        if (issue.getService().isHasProjectManagement() && issue.getProjectManagement().getDeadline() == null) {
            final Long issueId = issue.getId();
            CustomUserContact declarantInfo = Optional.ofNullable(issue.getDeclarant())
            .map(declarant -> new CustomUserContact(issueId, declarant))
            .orElse(null);
            Date deadline = slaService.calculateDeadlineByStateWaitToResolved(new CalculateDeadlineSimpleObject(issue,
            declarantInfo),
            issue.getCtime());
            issue.getProjectManagement().setDeadline(deadline);
        }
        
        if (issue.getManagerRating() != null) {
            onManagerRatingChange(issue, null, issue.getManagerRating());
        }
        
        issue.setRootFolder(issueFolderCreator.createIssueFolder(issue));
        initDefaultProceedings(service, issue);
        initMicrosoftTeamsChatLink(issue);
        
        if (issue.mustHaveDeclarantFolder()) {
            issue.setDeclarantFolder(issueFolderCreator.createIssueDeclarantFolder(issue));
        }
        
        if (issue.getRootFolder() == rootFolder) {
            throw new AssertionError();
        }
        if (additionalIssueParams != null && additionalIssueParams.fileIds != null) {
            temporaryFilesService.moveTemporaryFilesTo(additionalIssueParams.fileIds, issue.getRootFolder());
        }
        
        autoAssigneeService.initContractsAssigneesAndContractorsRespLawyersByIssue(issue);
        
        autocompleteRulesService.fillSystemFieldByClassOnDefaultValue(issue.getClass(), issue, service);
        autocompleteRulesService.fillAllIssueFieldDefinitionsOnDefaultValue(issue);
        
        if (issue.getAuthor() != null) {
            publisher.publishEvent(new IssueUpdated(issue.getId(), issue.getAuthor().getId()));
        }
        return issue;
    }
    
    
}