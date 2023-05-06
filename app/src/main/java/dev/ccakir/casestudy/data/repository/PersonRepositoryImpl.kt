package dev.ccakir.casestudy.data.repository

import dev.ccakir.casestudy.data.datasource.DataSource
import dev.ccakir.casestudy.data.datasource.FetchResponse
import dev.ccakir.casestudy.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class PersonRepositoryImpl(private val dataSource: DataSource) : PersonRepository {

    override suspend fun fetchPeople(nextPage: String?): Result<FetchResponse> =
        withContext(Dispatchers.IO) {
            dataSource.fetchAwait(nextPage)
        }

}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun DataSource.fetchAwait(nextPage: String?) =
    suspendCancellableCoroutine { cont ->
        fetch(nextPage) { fetchResponse, fetchError ->
            if (fetchError != null) {
                cont.resume(Result.Error(fetchError.errorDescription), null)
            } else if (fetchResponse != null) {
                cont.resume(Result.Success(fetchResponse), null)
            } else {
                cont.resume(Result.Error("Unknown error"), null)
            }
        }
    }