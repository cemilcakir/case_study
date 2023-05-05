package dev.ccakir.casestudy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ccakir.casestudy.data.repository.PersonRepository
import dev.ccakir.casestudy.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeopleListViewModel @Inject constructor(private val personRepository: PersonRepository) :
    ViewModel() {

    private var _uiState = MutableStateFlow(PeopleListUIState())
    val uiState = _uiState.asStateFlow()

    private var nextPage: String? = null

    init {
        fetchPeople()
    }

    private fun fetchPeople(isRefreshing: Boolean = false) {
        _uiState.update {
            it.copy(isFetching = true, isRefreshing = isRefreshing)
        }

        if (isRefreshing) {
            nextPage = null
        }

        viewModelScope.launch {
            personRepository.fetchPeople(nextPage)
                .flowOn(Dispatchers.IO)
                .onEach { result ->
                    when (result) {
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isFetching = false,
                                    isRefreshing = false,
                                    error = result.error
                                )
                            }
                        }

                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isFetching = false,
                                    isRefreshing = false,
                                    people = result.data.people
                                )
                            }
                            nextPage = result.data.next
                        }
                    }
                }
                .collect()
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

}