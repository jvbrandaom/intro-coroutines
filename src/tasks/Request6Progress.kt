package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: listOf()

    var allUsers = mutableListOf<User>()

    repos.forEachIndexed { index, repo ->
        allUsers.addAll(service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList())

        allUsers = allUsers.aggregate().toMutableList()
        updateResults(allUsers, index == repos.lastIndex)
    }
}
