package com.zamezamo.keepsave.ui.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zamezamo.keepsave.R
import com.zamezamo.keepsave.data.Database
import com.zamezamo.keepsave.domain.Idea
import com.zamezamo.keepsave.ui.adapters.IdeasAdapter
import com.zamezamo.keepsave.ui.adapters.IdeasDetailsLookup
import com.zamezamo.keepsave.ui.adapters.IdeasKeyProvider
import java.util.*

class IdeasFragment : Fragment(), ActionMode.Callback {

    companion object {

        fun newInstance() = IdeasFragment()

        private const val TAG = "IdeasFragment"

        private const val DB_URL = "https://keepsave-pr-default-rtdb.europe-west1.firebasedatabase.app/"

        val currentCalendar: Calendar = Calendar.getInstance()

    }

    private var ideasAdapter: IdeasAdapter? = null

    private var recyclerView: RecyclerView? = null

    private var bottomAppBar: BottomAppBar? = null

    private lateinit var tracker: SelectionTracker<String>

    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ideas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        setupToolbar()

        setupBottomBar()

    }

    private fun setupToolbar() {

        val menuHostTop: MenuHost = requireActivity()

        menuHostTop.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_ideas_top, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                if (menuItem.itemId == R.id.addIdea) {
                    val intent = Intent(requireActivity(), IdeasEditActivity::class.java)
                    startActivity(intent)
                    return true
                }

                return false

            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun setupBottomBar() {

        bottomAppBar = (activity as IdeasActivity).findViewById(R.id.bottomAppBarIdeasActivity)

        bottomAppBar?.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {

                R.id.share -> {
                    true
                }

                R.id.delete -> {
                    val selected = ideasAdapter?.currentList?.filter {
                        tracker.selection.contains(it.id)
                    }
                    Database.deleteIdeasFromDB(selected)

                    true
                }

                R.id.add_to_favorites -> {
                    true
                }

                else -> false

            }

        }

    }

    private fun initViews(view: View) {

        recyclerView = view.findViewById(R.id.contentIdeasRecyclerView)
        ideasAdapter = IdeasAdapter()
        recyclerView!!.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ideasAdapter
        }

        tracker = SelectionTracker.Builder(
            getString(R.string.item_selection),
            recyclerView!!,
            IdeasKeyProvider(ideasAdapter!!),
            IdeasDetailsLookup(recyclerView!!),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker.addObserver(object : SelectionTracker.SelectionObserver<String>() {

            override fun onSelectionChanged() {
                super.onSelectionChanged()

                if (actionMode == null) {
                    val currentActivity = activity as IdeasActivity
                    actionMode = currentActivity.startSupportActionMode(this@IdeasFragment)
                }

                val ideas = tracker.selection.size()
                if (ideas > 0) {
                    actionMode?.title = getString(R.string.action_selected, ideas)
                } else {
                    actionMode?.finish()
                }

            }
        })

        ideasAdapter!!.tracker = tracker

        val database = Firebase.database(DB_URL).reference
        val uid = Firebase.auth.currentUser?.uid

        val dbRef = database.child("users").child("$uid").child("ideas")

        val ideasList: MutableList<Idea> = emptyList<Idea>().toMutableList()

        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val idea = snapshot.getValue<Idea>()
                if (idea != null) {
                    val list = ideasAdapter!!.currentList.toMutableList()
                    if (!list.contains(idea)) {
                        ideasList.add(idea)
                        ideasAdapter!!.submitList(ideasList)
                        ideasAdapter!!.notifyItemInserted(ideasAdapter!!.currentList.indexOfFirst { it == idea })
                        Log.d(TAG, "child added to ideas: ${snapshot.value.toString()}")
                    }

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val idea = snapshot.getValue<Idea>()
                if (idea != null) {
                    val list = ideasAdapter!!.currentList.toMutableList()
                    val ideaBeforeChange = ideasAdapter!!.currentList.first { it.id == idea.id }
                    if (list.contains(ideaBeforeChange)) {
                        ideasList[ideasList.indexOfFirst { it.id == idea.id }] = idea
                        ideasAdapter!!.submitList(ideasList)
                        ideasAdapter!!.notifyItemChanged(ideasAdapter!!.currentList.indexOfFirst { it == idea })
                        Log.d(TAG, "child changed in ideas: ${snapshot.value.toString()}")
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val idea = snapshot.getValue<Idea>()
                if (idea != null) {
                    val list = ideasAdapter!!.currentList.toMutableList()
                    if (list.contains(idea)) {
                        val pos = ideasAdapter!!.currentList.indexOfFirst { it == idea }
                        ideasList.remove(idea)
                        ideasAdapter!!.submitList(ideasList)
                        ideasAdapter!!.notifyItemRemoved(pos)
                    }
                    Log.d(TAG, "child removed from ideas: ${snapshot.value.toString()}")
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //Do nothing
                Log.d(TAG, "child moved in ideas: ${snapshot.value.toString()}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "error child event")
            }


        }

        dbRef.addChildEventListener(childEventListener)

            //        example for adding idea to db

//        for (i in 1..20){
//        val key = dbRef.push().key
//
//        val idea = Idea(
//            key,
//            R.color.theme_blue,
//            "Key of added idea, $key",
//            Idea.Location(53.93239560018892, 27.662400805831442, "Pervomayskiy, Minsk, Belarus"),
//            Idea.DateAndTime(20, 10 - 1, 2022, 18, 5),
//            listOf("https://bumptech.github.io/glide/favicon-32x32.png", "https://bumptech.github.io/glide/favicon-64x64.png"),
//            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In tincidunt tortor id varius hendrerit. In convallis maximus leo, euismod tempor magna. Fusce id mauris magna. Aenean pulvinar mattis cursus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam sit amet diam turpis. Aliquam erat volutpat. Ut bibendum pretium interdum. Aenean tristique mauris quis bibendum commodo. Aliquam non felis tellus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec elementum augue a libero facilisis, eu laoreet mi pharetra. In a porta dui. "
//                    )
//
//        dbRef.child("$key").setValue(idea).addOnSuccessListener {
//            Log.d(TAG, "adding idea complete")
//                    }.addOnFailureListener {
//            Log.e(TAG, "adding idea failed")
//                    }
//
//    }
    }

    private fun cancelSelectionActionMode() {
        if (actionMode != null) {
            tracker.clearSelection()
            actionMode = null
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

        mode?.menuInflater?.inflate(R.menu.menu_ideas_actions_top, menu)

        bottomAppBar?.visibility = View.VISIBLE

        return true

    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean =
        true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {

        return when (item?.itemId) {
            R.id.selectAll -> {
                val adapter = recyclerView?.adapter as IdeasAdapter

                val ideas = arrayListOf<String>()
                adapter.currentList.forEach {
                    if (!tracker.isSelected(it.id)) {
                        ideas.add(it.id.toString())
                    }
                }
                tracker.setItemsSelected(ideas.asIterable(), true)

                true

            }
            else -> false
        }

    }

    override fun onDestroyActionMode(mode: ActionMode?) {

        cancelSelectionActionMode()

        bottomAppBar?.visibility = View.GONE

    }

}