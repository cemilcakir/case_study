package dev.ccakir.casestudy.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.ccakir.casestudy.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val peopleListViewModel: PeopleListViewModel by viewModels()

    private val adapter = PeopleAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rclPeople.adapter = adapter

        binding.srlPeople.setOnRefreshListener {
            peopleListViewModel.onEvent(PeopleListUIEvent.Refreshed)
        }

        binding.rclPeople.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (peopleListViewModel.uiState.value.reachedEndOfThePeople) {
                    return
                }

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                val lastVisibleItemPosition = layoutManager?.findLastVisibleItemPosition()

                if (lastVisibleItemPosition != null && lastVisibleItemPosition != -1 && lastVisibleItemPosition >= peopleListViewModel.uiState.value.people.size - 3) {
                    peopleListViewModel.onEvent(PeopleListUIEvent.ReachedEndOfThePage)
                }
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                peopleListViewModel.uiState.onEach { state ->
                    if (state.reachedEndOfThePeople) {
                        Snackbar.make(
                            binding.root,
                            "Displaying all of the people. Refresh the page to get new people.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    adapter.submitList(state.people)

                    with(binding) {
                        srlPeople.isRefreshing = state.isRefreshing

                        loadingIndicator.isVisible = state.isFetching && !state.isRefreshing

                        txtNoPeople.isVisible = state.noPeople
                    }

                    state.error?.let { errorMessage ->
                        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                        peopleListViewModel.onEvent(PeopleListUIEvent.DisplayedErrorMessage)
                    }
                }.collect()
            }
        }
    }
}