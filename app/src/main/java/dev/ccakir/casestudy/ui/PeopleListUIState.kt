package dev.ccakir.casestudy.ui

import dev.ccakir.casestudy.data.datasource.Person

data class PeopleListUIState(
    val people: List<Person> = emptyList(),
    val isFetching: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val reachedEndOfThePeople: Boolean = false,
    val noPeople: Boolean = false,
)
