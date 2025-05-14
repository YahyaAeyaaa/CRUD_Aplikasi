package com.example.yayaya.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yayaya.R
import com.example.yayaya.response.User

class UserAdapter(
    private val users: MutableList<User>,
    private val onEdit: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvId    = view.findViewById<TextView>(R.id.tvId)
        val tvName  = view.findViewById<TextView>(R.id.tvName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvRole  = view.findViewById<TextView>(R.id.tvRole)
        val btnEdit   = view.findViewById<Button>(R.id.btnEdit)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
    )

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = users[position]
        holder.tvId.text    = "ID: ${user.id}"
        holder.tvName.text  = "Nama: ${user.fullName}"
        holder.tvEmail.text = "Email: ${user.email}"
        holder.tvRole.text  = "Role: ${user.role}"

        holder.btnEdit.setOnClickListener { onEdit(user) }
        holder.btnDelete.setOnClickListener { onDelete(user) }
    }

    fun setData(newList: List<User>) {
        users.clear()
        users.addAll(newList)
        notifyDataSetChanged()
    }
}
