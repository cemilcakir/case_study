package dev.ccakir.casestudy.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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

        peopleListViewModel.uiState.onEach { state ->
            adapter.submitList(state.people)

            val lastVisibleItemPosition =
                (binding.rclPeople.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            if (state.people.size - 1 == lastVisibleItemPosition) {
                peopleListViewModel.onEvent(PeopleListUIEvent.ReachedEndOfThePage)
            }

            binding.srlPeople.isRefreshing = state.isRefreshing

            binding.loadingIndicator.visibility = if (state.isFetching) View.VISIBLE else View.GONE

            state.error?.let { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                peopleListViewModel.onEvent(PeopleListUIEvent.DisplayedErrorMessage)
            }
        }.launchIn(lifecycleScope)
    }
}