package dev.ccakir.casestudy.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.ccakir.casestudy.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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

        peopleListViewModel.uiState.onEach { state ->
            if (state.reachedEndOfThePeople) {
                Snackbar.make(
                    binding.root,
                    "Displaying all of the people. Refresh the page to get new people.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            adapter.submitList(state.people)

            binding.srlPeople.isRefreshing = state.isRefreshing

            binding.loadingIndicator.visibility = if (state.isFetching) View.VISIBLE else View.GONE

            binding.txtNoPeople.visibility = if (state.noPeople) View.VISIBLE else View.GONE

            state.error?.let { errorMessage ->
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                peopleListViewModel.onEvent(PeopleListUIEvent.DisplayedErrorMessage)
            }
        }.launchIn(lifecycleScope)
    }
}