package dev.ccakir.casestudy.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ccakir.casestudy.data.datasource.DataSource
import dev.ccakir.casestudy.data.repository.PersonRepository
import dev.ccakir.casestudy.data.repository.PersonRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideDataSource() = DataSource()

    @Provides
    fun providePersonRepository(dataSource: DataSource): PersonRepository =
        PersonRepositoryImpl(dataSource)
}