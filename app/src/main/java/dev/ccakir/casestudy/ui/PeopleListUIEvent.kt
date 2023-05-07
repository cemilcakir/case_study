package dev.ccakir.casestudy.ui

sealed interface PeopleListUIEvent {

    object ReachedEndOfThePage : PeopleListUIEvent

    object Refreshed : PeopleListUIEvent

    object DisplayedErrorMessage : PeopleListUIEvent

    object DisplayedReachedEndOfThePageMessage : PeopleListUIEvent

}