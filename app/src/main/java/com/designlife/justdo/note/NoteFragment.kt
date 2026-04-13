package com.designlife.justdo.note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.designlife.justdo.MainActivity
import com.designlife.justdo.R
import com.designlife.justdo.common.presentation.components.CommonCustomHeader
import com.designlife.justdo.common.presentation.components.CustomAttachmentsTab
import com.designlife.justdo.common.presentation.components.ProgressBar
import com.designlife.justdo.common.presentation.components.ToolBarPopUpComponent
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.constants.Constants
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.note.presentation.components.NoteComponent
import com.designlife.justdo.note.presentation.components.NoteReminderComponent
import com.designlife.justdo.note.presentation.enums.NoteMode
import com.designlife.justdo.note.presentation.events.NoteEvents
import com.designlife.justdo.note.presentation.viewmodel.NoteViewModel
import com.designlife.justdo.note.presentation.viewmodel.NoteViewModelFactory
import com.designlife.justdo.setworkllm.SetworkOLLM
import com.designlife.justdo.ui.theme.UIComponentBackground
import com.designlife.orchestrator.NotificationScheduler
import com.designlife.orchestrator.SchedulingEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar

class NoteFragment : Fragment(), SetworkOLLM.SetworkMessage {
    private lateinit var viewModel: NoteViewModel
    private var noteMode = NoteMode.CREATE
    private val READ_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val REQUEST_EXTERNAL_STORAGE = 1

    private lateinit var notificationScheduler: NotificationScheduler
    private var isPermissionGranted : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.ollmSDK?.protocol(this)
        val noteId = arguments?.getLong("noteId") ?: -1L
        val index = arguments?.getInt("categoryIndex") ?: -1
        val noteRepository = AppServiceLocator.provideNoteRepository(requireContext())
        val categoryRepository = AppServiceLocator.provideCategoryRepository(requireContext())
        notificationScheduler = SchedulingEngine(requireActivity().applicationContext).notificationScheduler()
        val factory = NoteViewModelFactory(noteRepository, categoryRepository,notificationScheduler)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        val noteView = arguments?.getBoolean(Constants.NOTE_VIEW) ?: false
        if (noteView){
            fetchFromNotification(noteView)
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                async { viewModel.fetchCategories() }.await()
                if (index != -1) {
                    viewModel.onEvent(NoteEvents.OnCategoryIndexChange(index))
                }
                if (noteId != -1L) {
                    noteMode = NoteMode.UPDATE
                    viewModel.fetchNoteById(noteId)
                }
            }
        }
        isPermissionGranted = checkPermission()

    }

    private fun fetchFromNotification(noteView : Boolean) {
        val noteId = arguments?.getInt(Constants.NOTE_VIEW_ID) ?: -1
        noteId.let {
            if (noteView && noteId != -1){
                CoroutineScope(Dispatchers.IO).launch {
                    async { viewModel.fetchCategories() }.await()
                    noteMode = NoteMode.UPDATE
                    viewModel.fetchNoteById(noteId.toLong())
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE_PERMISSION
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val noteTitle = viewModel.titleValue.value
                val noteContent = viewModel.contentValue.value
                val categoryList = viewModel.categoryList.value
                val selectedCategoryIndex = viewModel.selectedCategoryIndex.value
                val progressBar = viewModel.progressBar.value
                val threeDot = viewModel.threeDot.value
                val aiChatState = viewModel.aiChatState.value
                val reminderState = viewModel.reminderState.value
                val calendar = Calendar.getInstance()
                val selectedDateText = viewModel.selectedDateText.value
                val selectedTimeText = viewModel.selectedTimeText.value
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (progressBar) .4F else 1F)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = UIComponentBackground.value)
                            .imePadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CommonCustomHeader(
                            headerTitle = noteTitle.ifEmpty { "New Note" },
                            onCloseEvent = {
                                // save updates
                                try {
                                    if (noteMode == NoteMode.CREATE) {
                                        viewModel.insertNote()
                                    } else {
                                        viewModel.updateNote()
                                    }
                                }catch (e : Exception){
                                    e.printStackTrace()
                                }finally {
                                    Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                                    findNavController().navigateUp()
                                }

                            },
                            hasDone = noteMode == NoteMode.UPDATE,
                            forTask = noteMode == NoteMode.UPDATE,
                            isOverview = noteMode == NoteMode.UPDATE,
                            viewType = ViewType.NOTE,
                            onReminderEvent = {
                                viewModel.onEvent(NoteEvents.OnReminderToggle)

                            },
                            onAutoSaveEvent = {
                                try {
                                    if (noteMode == NoteMode.CREATE) {
                                        viewModel.insertNote()
                                    } else {
                                        viewModel.updateNote()
                                    }
                                }catch (e : Exception){
                                    e.printStackTrace()
                                }
                            },
                            onAIChatEvent = {
                                viewModel.onEvent(NoteEvents.OnAIChatToggle)
                            },
                            onThreeDotEvent = {
                                viewModel.onEvent(NoteEvents.OnThreeDotToggle(!threeDot))
                            }
                        ) {

                        }
                        AnimatedVisibility(reminderState) {
                            NoteReminderComponent(
                                dateText = selectedDateText,
                                timeText = selectedTimeText,
                                onDateChange = {
                                    val year = calendar[Calendar.YEAR]
                                    val month = calendar[Calendar.MONTH]
                                    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
                                    val datePicker = DatePickerDialog(
                                        context,
                                        R.style.CustomDatePickerTheme,
                                        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                                            viewModel.onEvent(NoteEvents.OnDateChange("$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"))
                                        }, year, month, dayOfMonth
                                    )
                                    datePicker.show()
                                },
                                onTimeChange = {
                                    val hour = calendar[Calendar.HOUR_OF_DAY]
                                    val minute = calendar[Calendar.MINUTE]
                                    val timePicker = TimePickerDialog(
                                        context,
                                        R.style.CustomTimePickerTheme,
                                        { _, selectedHour: Int, selectedMinute: Int ->
                                            viewModel.onEvent(NoteEvents.OnTimeChange("$selectedHour:$selectedMinute"))
                                        }, hour, minute, false
                                    )
                                    timePicker.show()
                                }
                            )
                        }
                        AnimatedVisibility(aiChatState) {
                            SetworkOLLM.ChatTextView()
                        }
                        CustomAttachmentsTab(
                            hasCover = true,
                            onGalleryEvent = { bitMap ->
                                if (!isPermissionGranted) {
                                    takeUserPermission()
                                }
                                if (isPermissionGranted) {
                                    viewModel.onEvent(NoteEvents.OnCoverChange(bitMap))
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.image_picked),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            categoryList = categoryList,
                            selectedCategoryIndex = selectedCategoryIndex,
                            onCategoryEvent = {
                                viewModel.onEvent(NoteEvents.OnCategoryIndexChange(it))
                            },
                            addCategoryEvent = {
                                val bundle = bundleOf()
                                bundle.putInt(Constants.SCREEN_TYPE, ScreenType.CATEGORY.ordinal)
                                findNavController().navigate(
                                    R.id.containerFragment,
                                    bundle
                                )
                            }
                        )
                        NoteComponent(
                            title = noteTitle,
                            onTitleUpdate = {
                                viewModel.onEvent(NoteEvents.OnTitleChange(it))
                            },
                            noteText = noteContent,
                            onNoteUpdate = {
                                viewModel.onEvent(NoteEvents.OnContentChange(it))
                            }
                        )
                    }
                    if (progressBar) {
                        ProgressBar()
                    }

                    AnimatedVisibility(
                        threeDot,
                        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                    ) {
                        ToolBarPopUpComponent(
                            onExportPdfEvent = {
                                viewModel.onEvent(NoteEvents.OnThreeDotToggle(false))
                                try {
                                    viewModel.onEvent(NoteEvents.OnPdfExport(requireContext()))
                                }catch (e : Exception){
                                    e.printStackTrace()
                                }
                            },
                            onExportPngEvent = {
                                viewModel.onEvent(NoteEvents.OnThreeDotToggle(false))
                                try {
                                    viewModel.onEvent(NoteEvents.OnPngExport(requireContext()))
                                }catch (e : Exception){
                                    e.printStackTrace()
                                }
                            },
                            onDeleteEvent = {
                                viewModel.onEvent(NoteEvents.OnThreeDotToggle(false))
                                viewModel.onEvent(NoteEvents.OnDeleteNote(requireContext()))
                                findNavController().navigateUp()
                            }
                        )
                    }

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    try {
                        if (noteMode == NoteMode.CREATE) {
                            viewModel.insertNote()
                        } else {
                            viewModel.updateNote()
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }finally {
                        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                    }
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    override fun onResume() {
        super.onResume()
        parentFragmentManager.setFragmentResultListener(
            Constants.CONTAINER_VIEW,
            this
        ) { _, bundle ->
            lifecycleScope.launch(Dispatchers.Main.immediate) {
                val selectedIndex = bundle.getInt(Constants.CONTAINER_POP_INDEX)
                viewModel.onEvent(NoteEvents.OnCategoryIndexChange(selectedIndex))
            }
        }
    }

    private fun takeUserPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), READ_EXTERNAL_STORAGE_PERMISSION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission hasn't been granted, request it
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(READ_EXTERNAL_STORAGE_PERMISSION),
                REQUEST_EXTERNAL_STORAGE
            )
        } else {
            isPermissionGranted = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    isPermissionGranted = true
                } else {
                    isPermissionGranted = false
                    Toast.makeText(
                        requireActivity(),
                        "Permission Not Granted :/\nGo to settings --> Apps --> Setwork --> App Permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // save updates
        try {
            if (noteMode == NoteMode.CREATE) {
//            viewModel.insertNote()
            } else {
                viewModel.updateNote()
            }
            if (viewModel.hasNoteModified.value) {
                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
            }
//            lifecycleScope?.cancel()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onChatRelay(message: String) {
        val completeContent = viewModel.contentValue.value +"\n -- Setwork Chat --> \n" + message
        viewModel.onEvent(NoteEvents.OnContentChange(completeContent))
        // Once copied save immediately all
        try {
            lifecycleScope.launch(Dispatchers.Main) {
                if (noteMode == NoteMode.CREATE) {
                    viewModel.insertNote()
                } else {
                    viewModel.updateNote()
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}