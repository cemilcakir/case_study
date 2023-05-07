package dev.ccakir.casestudy

import dev.ccakir.casestudy.data.datasource.Person
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random


class UniquePeopleUnitTest {

    private fun generatePersonList(): List<Person> {
        val people = mutableListOf<Person>()

        (Random.nextInt(0, 50)..Random.nextInt(50, 100)).shuffled().forEach { id ->
            people.add(Person(id, "fullName"))
        }

        return people
    }

    @Test
    fun `filter contains test list`() = runBlocking {
        val people1 = generatePersonList()
        val people2 = generatePersonList()

        val people = mutableListOf<Person>()
        people.addAll(people1)
        println("list before size: ${people.size}")

        people2.forEach { person ->
            if (people.none { it.id == person.id }) {
                people.add(person)
            }
        }
        println("list after size: ${people.size}")

        assertTrue(true)
    }

    @Test
    fun `filter contains test hashset`() = runBlocking {
        val people1 = generatePersonList()
        val people2 = generatePersonList()

        val people = mutableListOf<Person>()
        people.addAll(people1)
        println("hashset before size: ${people.size}")

        val currentPeopleIds = people.mapTo(HashSet(people.size)) { it.id }

        people2.forEach { person ->
            if (!currentPeopleIds.contains(person.id)) {
                people.add(person)
            }
        }
        println("hashset after size: ${people.size}")

        assertTrue(true)
    }
}