package dev.ccakir.casestudy.data.repository

import dev.ccakir.casestudy.data.datasource.DataSource
import dev.ccakir.casestudy.data.datasource.FetchResponse
import dev.ccakir.casestudy.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine

class PersonRepositoryImpl(private val dataSource: DataSource) : PersonRepository {

    override suspend fun fetchPeople(nextPage: String?): Flow<Result<FetchResponse>> {
        return flow {
            emit(dataSource.fetchAwait(nextPage))
        }
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