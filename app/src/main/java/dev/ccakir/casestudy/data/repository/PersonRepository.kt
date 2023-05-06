package dev.ccakir.casestudy.data.repository

import dev.ccakir.casestudy.data.datasource.FetchResponse
import dev.ccakir.casestudy.utils.Result
import kotlinx.coroutines.flow.Flow

interface PersonRepository {

    suspend fun fetchPeople(nextPage: String? = null): Result<FetchResponse>

}