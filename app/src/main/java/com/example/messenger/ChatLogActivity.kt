package com.example.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val recycleviewchatlog: RecyclerView = findViewById(R.id.recycleview_chat_log)
        recycleviewchatlog.adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        if (user != null) {
            supportActionBar?.title = user.login
        }

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "")
            performSendMessage()
        }

        recycleviewchatlog.adapter = adapter


    }
    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)?.username.toString()

        val ref = FirebaseDatabase.getInstance("https://messenger-ecb45-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentuser = MessagesFeedActivity.currentUser
                        adapter.add(ChatToItem(chatMessage.text,currentuser!!))
                    } else {
                        val currentuser = MessagesFeedActivity.currentUser
                        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                        adapter.add(ChatFromItem(chatMessage.text, user!!))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })

    }

    class ChatMessage(val id: String, val fromId: String, val toId: String, timestamp: Long, val text: String){
        constructor(): this("","","",-1,"")
    }

    private fun performSendMessage() { val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.username.toString()
        val reference = FirebaseDatabase.getInstance("https://messenger-ecb45-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/user-messages/$fromId/$toId").push()
        val toreference = FirebaseDatabase.getInstance("https://messenger-ecb45-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(reference.key!!, fromId!!, toId, System.currentTimeMillis()/1000, text)


        reference.setValue(chatMessage)
            .addOnSuccessListener {
                edittext_chat_log.text.clear()
                recycleview_chat_log.scrollToPosition(adapter.itemCount-1)
            }
        toreference.setValue(chatMessage)


        val latestMessageRef = FirebaseDatabase.getInstance("https://messenger-ecb45-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val tolatestMessageRef = FirebaseDatabase.getInstance("https://messenger-ecb45-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/latest-messages/$toId/$fromId")
        tolatestMessageRef.setValue(chatMessage)
    }

}



class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_from.text = text

        val target = viewHolder.itemView.imageView_chat_from_row
        Picasso.get().load(user.profileImageUrl).into(target)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}


class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_to_m.text = text

        val target = viewHolder.itemView.imageView_chat_to_row
        Picasso.get().load(user.profileImageUrl).into(target)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}