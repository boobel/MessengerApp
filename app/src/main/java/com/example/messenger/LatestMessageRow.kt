package com.example.messenger

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatLogActivity.ChatMessage): Item<GroupieViewHolder>() {
    var chatPartnersSer: User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_latest_message.text = chatMessage.text

        val chatpartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatpartnerId = chatMessage.toId
        } else {
            chatpartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance("https://messenger-ecb45-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/users/$chatpartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnersSer = snapshot.getValue(User::class.java)
                viewHolder.itemView.username_latest_message.text = chatPartnersSer?.login

                val targetImageView = viewHolder.itemView.imageView_latest_message
                Picasso.get().load(chatPartnersSer?.profileImageUrl).into(targetImageView)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

}