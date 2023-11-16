package com.zamezamo.keepsave.ui.views

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zamezamo.keepsave.R
import com.zamezamo.keepsave.domain.Idea
import com.zamezamo.keepsave.ui.adapters.IdeasImagesAdapter
import com.zamezamo.keepsave.ui.viewmodels.IdeaEditViewModel
import com.zamezamo.keepsave.utils.DateTimeConverter
import java.util.*


class IdeasEditActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "IdeasEditActivity"
        private const val IDEA = "ideaSerialized"

    }

    private val viewModel: IdeaEditViewModel by viewModels()

    private val ideaBeforeSaving: Idea by lazy {
        (intent.getSerializableExtra(IDEA) ?: Idea()) as Idea
    }
    private var ideaColorPriorityToSave: Int? = null
    private var ideaImagesUriToSave: MutableList<String> = emptyList<String>().toMutableList()
    private var ideaDayToSave: Int? = null
    private var ideaMonthToSave: Int? = null
    private var ideaYearToSave: Int? = null
    private var ideaHourToSave: Int? = null
    private var ideaMinuteToSave: Int? = null
    private var ideaLatitudeToSave: Double? = null
    private var ideaLongitudeToSave: Double? = null
    private var ideaLocationNameToSave: String? = null

    private val currentCalendar: Calendar = Calendar.getInstance()
    private val calendarPicker: Calendar = currentCalendar as GregorianCalendar

    private var topAppBarIdeasEditActivity: MaterialToolbar? = null

    private var constraintLayoutIdeaTitle: ConstraintLayout? = null
    private var editTextIdeaTitle: EditText? = null
    private var editTextIdeaDescription: EditText? = null
    private var recyclerViewIdeaImages: RecyclerView? = null
    private var imageButtonAddImage: ImageButton? = null
    private var imageButtonChangeTitleColor: ImageButton? = null

    private var textViewDateAndTime: TextView? = null
    private var switchDateAndTimeExpand: SwitchMaterial? = null
    private var timePickerIdea: TimePicker? = null
    private var datePickerIdea: DatePicker? = null

    private var textViewLocation: TextView? = null
    private var switchLocationExpand: SwitchMaterial? = null

    private var menuHostTop: MenuHost? = null
    private val menuHostTopProvider: MenuProvider = object : MenuProvider {

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_idea_edit_top, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

            if (menuItem.itemId == R.id.editIdea) {
                enterEditMode()
                return true
            }
            return false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea_edit)

        viewModel.savingIdeaState.observe(this) { savingIdeaResult ->

            savingIdeaResult ?: return@observe

            when (savingIdeaResult) {
                IdeaEditViewModel.SavingIdeaResult.Saved -> {
                    Toast.makeText(
                        this,
                        getString(R.string.saving_idea_result_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                IdeaEditViewModel.SavingIdeaResult.ErrorFilling -> {
                    Toast.makeText(
                        this,
                        getString(R.string.saving_idea_result_error_filling),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                IdeaEditViewModel.SavingIdeaResult.ErrorChecking -> {
                    Toast.makeText(
                        this,
                        getString(R.string.saving_idea_result_error_checking),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        setupToolbar()
        initViews()

    }

    override fun onPause() {
        super.onPause()
        if (editTextIdeaTitle!!.isFocused || editTextIdeaDescription!!.isFocused) {
            exitEditMode()
            editTextIdeaTitle!!.onFocusChangeListener = null
            editTextIdeaDescription!!.onFocusChangeListener = null
        }
    }

    override fun onDestroy() {
        viewModel.savingIdeaState.removeObservers(this)
        removeMenuProvider(menuHostTopProvider)

        super.onDestroy()
    }

    private fun initViews() {

        constraintLayoutIdeaTitle = findViewById(R.id.constraintLayoutIdeaTitle)

        editTextIdeaTitle = findViewById(R.id.editTextIdeaTitle)
        editTextIdeaTitle!!.setOnFocusChangeListener { _, _ -> enterEditMode() }
        editTextIdeaDescription = findViewById(R.id.editTextIdeaDescription)
        editTextIdeaDescription!!.setOnFocusChangeListener { _, _ -> enterEditMode() }

        recyclerViewIdeaImages = findViewById(R.id.recyclerViewIdeaImages)
        recyclerViewIdeaImages!!.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = IdeasImagesAdapter(ideaImagesUriToSave)
        }
        imageButtonAddImage = findViewById(R.id.imageButtonAddImage)
        imageButtonChangeTitleColor = findViewById(R.id.imageButtonChangeTitleColor)

        textViewDateAndTime = findViewById(R.id.textViewDateAndTime)
        switchDateAndTimeExpand = findViewById(R.id.switchDateAndTimeExpand)
        switchDateAndTimeExpand!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                expandIdeaDateAndTime()
            else
                collapseIdeaDateAndTime()
        }
        timePickerIdea = findViewById(R.id.timePickerIdea)
        datePickerIdea = findViewById(R.id.datePickerIdea)

        textViewLocation = findViewById(R.id.textViewLocation)
        switchLocationExpand = findViewById(R.id.switchLocationExpand)
        switchLocationExpand!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                expandIdeaLocation()
            else
                collapseIdeaLocation()
        }

        ideaColorPriorityToSave = ideaBeforeSaving.colorPriority ?: R.color.theme_purple

        topAppBarIdeasEditActivity!!.background =
            ColorDrawable(ContextCompat.getColor(this, ideaColorPriorityToSave!!))

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, ideaColorPriorityToSave!!)

        constraintLayoutIdeaTitle!!.background =
            ColorDrawable(ContextCompat.getColor(this, ideaColorPriorityToSave!!))

        if (ideaBeforeSaving.title?.isNotEmpty() == true) {
            editTextIdeaTitle!!.setText(ideaBeforeSaving.title)
        }

        if (ideaBeforeSaving.description?.isNotEmpty() == true) {
            editTextIdeaDescription!!.visibility = View.VISIBLE
            editTextIdeaDescription!!.setText(ideaBeforeSaving.description)
        }

        if (ideaBeforeSaving.imagesUri.isNotEmpty()) {
            recyclerViewIdeaImages!!.visibility = View.VISIBLE
            for (i in 0 until ideaBeforeSaving.imagesUri.size) {
                ideaImagesUriToSave.add(ideaBeforeSaving.imagesUri[i])
                recyclerViewIdeaImages!!.adapter!!.notifyItemInserted(i)
            }
        }

        if (ideaBeforeSaving.dateAndTime != null) {

            ideaDayToSave = ideaBeforeSaving.dateAndTime!!.day
            ideaMonthToSave = ideaBeforeSaving.dateAndTime!!.month
            ideaYearToSave = ideaBeforeSaving.dateAndTime!!.year
            ideaHourToSave = ideaBeforeSaving.dateAndTime!!.hour
            ideaMinuteToSave = ideaBeforeSaving.dateAndTime!!.minute

            switchDateAndTimeExpand!!.isChecked = true

        } else {

            calendarPicker.add(Calendar.HOUR_OF_DAY, 1)

            ideaDayToSave = calendarPicker.get(Calendar.DAY_OF_MONTH)
            ideaMonthToSave = calendarPicker.get(Calendar.MONTH)
            ideaYearToSave = calendarPicker.get(Calendar.YEAR)
            ideaHourToSave = calendarPicker.get(Calendar.HOUR_OF_DAY)
            ideaMinuteToSave = calendarPicker.get(Calendar.MINUTE)

        }

        if (ideaBeforeSaving.location != null) {

            ideaLatitudeToSave = ideaBeforeSaving.location!!.latitude
            ideaLongitudeToSave = ideaBeforeSaving.location!!.longitude
            ideaLocationNameToSave = ideaBeforeSaving.location!!.locationName

            switchLocationExpand!!.isChecked = true

        }

        timePickerIdea!!.currentHour = ideaHourToSave!!
        timePickerIdea!!.currentMinute = ideaMinuteToSave!!
        timePickerIdea!!.setOnTimeChangedListener { _, hour, minute ->
            ideaHourToSave = hour
            ideaMinuteToSave = minute
            textViewDateAndTime!!.text = DateTimeConverter.convert(
                Idea.DateAndTime(
                    ideaDayToSave!!,
                    ideaMonthToSave!!,
                    ideaYearToSave!!,
                    hour,
                    minute
                ),
                currentCalendar
            )
        }

        datePickerIdea!!.init(
            ideaYearToSave!!,
            ideaMonthToSave!!,
            ideaDayToSave!!
        ) { _, year, month, day ->
            ideaYearToSave = year
            ideaMonthToSave = month
            ideaDayToSave = day
            textViewDateAndTime!!.text = DateTimeConverter.convert(
                Idea.DateAndTime(
                    day,
                    month,
                    year,
                    ideaHourToSave!!,
                    ideaMinuteToSave!!
                ), currentCalendar
            )
        }

    }

    private fun setupToolbar() {

        topAppBarIdeasEditActivity = findViewById(R.id.topAppBarIdeasEditActivity)

        setSupportActionBar(topAppBarIdeasEditActivity)

        topAppBarIdeasEditActivity!!.setNavigationOnClickListener {
            onBackPressed()
        }

        menuHostTop = this

        menuHostTop!!.addMenuProvider(menuHostTopProvider, this, Lifecycle.State.RESUMED)
    }

    private fun enterEditMode() {

        editTextIdeaTitle!!.onFocusChangeListener = null
        editTextIdeaDescription!!.onFocusChangeListener = null

        if (editTextIdeaDescription!!.visibility == View.GONE)
            editTextIdeaDescription!!.visibility = View.VISIBLE

        if (!editTextIdeaTitle!!.isFocused && !editTextIdeaDescription!!.isFocused)
            showKeyboard(editTextIdeaTitle!!)

        menuHostTop!!.removeMenuProvider(menuHostTopProvider)
        topAppBarIdeasEditActivity!!.setNavigationOnClickListener {
            exitEditMode()
        }

    }

    private fun exitEditMode() {

        hideKeyboard()

        if (editTextIdeaTitle!!.isFocused)
            editTextIdeaTitle!!.clearFocus()
        if (editTextIdeaDescription!!.isFocused)
            editTextIdeaDescription!!.clearFocus()

        if (editTextIdeaDescription!!.text.isNullOrEmpty())
            editTextIdeaDescription!!.visibility = View.GONE

        menuHostTop!!.addMenuProvider(menuHostTopProvider, this, Lifecycle.State.RESUMED)
        topAppBarIdeasEditActivity!!.setNavigationOnClickListener {
            onBackPressed()
        }

        editTextIdeaTitle!!.setOnFocusChangeListener { _, _ -> enterEditMode() }
        editTextIdeaDescription!!.setOnFocusChangeListener { _, _ -> enterEditMode() }

    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, 0)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun expandIdeaDateAndTime() {

        if (ideaDayToSave == null || ideaHourToSave == null) {

            ideaDayToSave = datePickerIdea!!.dayOfMonth
            ideaMonthToSave = datePickerIdea!!.month
            ideaYearToSave = datePickerIdea!!.year
            ideaHourToSave = timePickerIdea!!.currentHour
            ideaMinuteToSave = timePickerIdea!!.currentMinute

        }

        textViewDateAndTime!!.text = DateTimeConverter.convert(
            Idea.DateAndTime(
                ideaDayToSave,
                ideaMonthToSave,
                ideaYearToSave,
                ideaHourToSave,
                ideaMinuteToSave
            ),
            currentCalendar
        )

        timePickerIdea!!.visibility = View.VISIBLE
        datePickerIdea!!.visibility = View.VISIBLE

    }


    private fun collapseIdeaDateAndTime() {

        timePickerIdea!!.visibility = View.GONE
        datePickerIdea!!.visibility = View.GONE

        textViewDateAndTime!!.setText(R.string.textViewDateAndTime)

        ideaDayToSave = null
        ideaMonthToSave = null
        ideaYearToSave = null
        ideaHourToSave = null
        ideaMinuteToSave = null

    }

    private fun expandIdeaLocation() {

    }

    private fun collapseIdeaLocation() {

    }

    override fun onBackPressed() {
        if (editTextIdeaTitle!!.isFocused || editTextIdeaDescription!!.isFocused)
            exitEditMode()
        else {
            val ideaDateAndTimeToSave = if (ideaDayToSave != null) Idea.DateAndTime(
                day = ideaDayToSave,
                month = ideaMonthToSave,
                year = ideaYearToSave,
                hour = ideaHourToSave,
                minute = ideaMinuteToSave
            ) else null
            val ideaLocationToSave = if (ideaLongitudeToSave != null) Idea.Location(
                longitude = ideaLongitudeToSave,
                latitude = ideaLatitudeToSave,
                locationName = ideaLocationNameToSave
            ) else null
            val ideaToSave = Idea(
                id = ideaBeforeSaving.id,
                colorPriority = ideaColorPriorityToSave,
                title = editTextIdeaTitle!!.text.toString(),
                ideaLocationToSave,
                ideaDateAndTimeToSave,
                imagesUri = ideaImagesUriToSave.toList(),
                description = editTextIdeaDescription!!.text.toString()
            )
            viewModel.checkForChangesAndSave(ideaBeforeSaving, ideaToSave)
            super.onBackPressed()
        }
    }

}