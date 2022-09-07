package com.zamezamo.keepsave.ui.views

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
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

        val currentCalendar: Calendar = Calendar.getInstance()

    }

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

        setupToolbar()

        initViews(view)

        setupBottombar()

    }

    private fun setupToolbar() {

        val menuHostTop: MenuHost = requireActivity()

        menuHostTop.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_ideas_top, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                if (menuItem.itemId == R.id.addIdea) {
                    //
                    return true
                }

                return false

            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val currentActivity = activity as IdeasActivity
        currentActivity.title = "All: Synced"

    }

    private fun setupBottombar() {

        bottomAppBar = (activity as IdeasActivity).findViewById(R.id.bottomAppBarIdeasActivity)
        bottomAppBar?.performHide(false)

    }

    private fun initViews(view: View) {

        recyclerView = view.findViewById(R.id.contentIdeasRecyclerView)
        val ideasAdapter = IdeasAdapter()
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ideasAdapter
        }

        tracker = SelectionTracker.Builder(
            getString(R.string.item_selection),
            recyclerView!!,
            IdeasKeyProvider(ideasAdapter),
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

        ideasAdapter.tracker = tracker

        Database.initDatabase()
        Database.initAuth()

        val database = Database.database.reference
        val uid = Database.auth.uid

        val dbRef = database.child("users").child("$uid").child("ideas")

        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                cancelSelectionActionMode()
                val idea = snapshot.getValue<Idea>()
                val list = ideasAdapter.currentList.toMutableList()
                list.add(idea)
                ideasAdapter.submitList(list)
                Log.d(TAG, "child added to ideas: ${snapshot.value.toString()}")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                cancelSelectionActionMode()
                val idea = snapshot.getValue<Idea>()
                if (idea != null) {
                    val list = ideasAdapter.currentList.toMutableList()
                    list[list.indexOfFirst { it.id == idea.id }] = idea
                    ideasAdapter.submitList(list)
                }
                Log.d(TAG, "child changed in ideas: ${snapshot.value.toString()}")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                cancelSelectionActionMode()
                val idea = snapshot.getValue<Idea>()
                val list = ideasAdapter.currentList.toMutableList()
                list.remove(idea)
                Log.d(TAG, "child removed from ideas: ${snapshot.value.toString()}")
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


        // --temporary

        val list = listOf<Idea>(
            Idea(
            "first",
                R.color.theme_green,
                "Hello world",
                Idea.Location(234.045, 123.015, "NoName"),
                Idea.DateAndTime(10,9-1,2022,17,5),
                "https://bumptech.github.io/glide/favicon-32x32.png",
                "Something interesting here..."
            )
        )

        ideasAdapter.submitList(list)

        // --temporary

    }

    private fun updateAndSave(list: List<Idea>) {
        (recyclerView?.adapter as IdeasAdapter).submitList(list)
        //saveListOfIdeas()
    }

    private fun cancelSelectionActionMode(){
        tracker.clearSelection()
        actionMode = null
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

        mode?.menuInflater?.inflate(R.menu.menu_ideas_actions_top, menu)

        bottomAppBar?.performShow(true)

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

        bottomAppBar?.performHide(true)

    }

}