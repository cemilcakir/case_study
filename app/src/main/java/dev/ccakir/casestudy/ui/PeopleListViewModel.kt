package dev.ccakir.casestudy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ccakir.casestudy.data.repository.PersonRepository
import dev.ccakir.casestudy.utils.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeopleListViewModel @Inject constructor(private val personRepository: PersonRepository) :
    ViewModel() {

    private var _uiState = MutableStateFlow(PeopleListUIState())
    val uiState = _uiState.asStateFlow()

    private var nextPage: String? = null

    private var fetchPeopleJob: Job? = null

    init {
        fetchPeople()
    }

    private fun fetchPeople(isRefreshing: Boolean = false) {
        if (isRefreshing) {
            nextPage = null
            _uiState.update {
                it.copy(people = emptyList(), reachedEndOfThePeople = false, noPeople = false)
            }
        }

        if (_uiState.value.reachedEndOfThePeople) {
            return
        }

        cancelFetchPeopleJob()

        _uiState.update {
            it.copy(isFetching = true, isRefreshing = isRefreshing)
        }

        fetchPeopleJob = viewModelScope.launch {
            when (val result = personRepository.fetchPeople(nextPage)) {
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isFetching = false,
                            isRefreshing = false,
                            error = result.error + "\nWill try again in 3 seconds."
                        )
                    }

                    delay(3000)
                    fetchPeople(isRefreshing)
                }

                is Result.Success -> {
                    val people = _uiState.value.people.toMutableList()
                    val peopleSizeOld = people.size

                    result.data.people.forEach { person ->
                        if (people.none { it.id == person.id }) {
                            people.add(person)
                        }
                    }
                    val peopleSizeNew = people.size

                    _uiState.update {
                        it.copy(
                            isFetching = false,
                            isRefreshing = false,
                            people = people,
                            noPeople = people.isEmpty(),
                            reachedEndOfThePeople = peopleSizeOld == peopleSizeNew
                        )
                    }
                    nextPage = result.data.next
                }
            }
        }
    }

    fun onEvent(event: PeopleListUIEvent) {
        when (event) {
            PeopleListUIEvent.DisplayedErrorMessage -> {
                _uiState.update {
                    it.copy(error = null)
                }
            }

            PeopleListUIEvent.ReachedEndOfThePage -> fetchPeople()
            PeopleListUIEvent.Refreshed -> fetchPeople(true)
        }
    }

    private fun cancelFetchPeopleJob() {
        fetchPeopleJob?.cancel()
        fetchPeopleJob = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelFetchPeopleJob()
    }

}