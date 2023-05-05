package dev.ccakir.casestudy.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.ccakir.casestudy.data.datasource.Person
import dev.ccakir.casestudy.databinding.ListItemPersonBinding

class PeopleAdapter : ListAdapter<Person, PeopleAdapter.PersonViewHolder>(PersonDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PersonViewHolder(
        ListItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PersonDiffUtil() : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem == newItem
        }
    }

    class PersonViewHolder(private val binding: ListItemPersonBinding) : ViewHolder(binding.root) {
        fun bind(person: Person) {
            binding.txtPersonInfo.text = "${person.fullName} (${person.id})"
        }
    }
}