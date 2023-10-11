package com.designlife.justdo.note

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.designlife.justdo.R
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.presentation.components.CommonCustomHeader
import com.designlife.justdo.common.presentation.components.CustomAttachementsTab
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.common.utils.constants.Constants
import com.designlife.justdo.common.utils.enums.ScreenType
import com.designlife.justdo.note.presentation.components.NoteComponent
import com.designlife.justdo.note.presentation.enums.NoteMode
import com.designlife.justdo.note.presentation.events.NoteEvents
import com.designlife.justdo.note.presentation.viewmodel.NoteViewModel
import com.designlife.justdo.note.presentation.viewmodel.NoteViewModelFactory
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NoteFragment : Fragment() {
    private lateinit var viewModel: NoteViewModel
    private var noteMode = NoteMode.CREATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = arguments?.getLong("noteId") ?: -1L
        val index = arguments?.getInt("categoryIndex") ?: -1
        val noteRepository = AppServiceLocator.provideNoteRepository(requireContext())
        val categoryRepository = AppServiceLocator.provideCategoryRepository(requireContext())
        val factory = NoteViewModelFactory(noteRepository,categoryRepository)
        viewModel =  ViewModelProvider(this,factory)[NoteViewModel::class.java]
        CoroutineScope(Dispatchers.IO).launch {
            async { viewModel.fetchCategories() }.await()
            if (noteId != -1L){
                noteMode = NoteMode.UPDATE
                viewModel.fetchNoteById(noteId)
            }
            if (index != -1){
                viewModel.onEvent(NoteEvents.OnCategoryIndexChange(index))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply{
            setContent {
                val noteTitle = viewModel.titleValue.value
                val noteContent = viewModel.contentValue.value
                val categoryList = viewModel.categoryList.value
                val selectedCategoryIndex = viewModel.selectedCategoryIndex.value
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = PrimaryBackgroundColor),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CommonCustomHeader(
                        headerTitle = if(noteTitle.isEmpty()) "New Note" else noteTitle,
                        onCloseEvent = {
                            // save updates
                            if (noteMode == NoteMode.CREATE){
                                viewModel.insertNote()
                            }else{
                                viewModel.updateNote()
                            }
                            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    ) { /* do something */ }
                    CustomAttachementsTab(
                        hasCover = true,
                        onGalleryEvent = { /*TODO*/ },
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
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // save updates
        if (noteMode == NoteMode.CREATE){
            viewModel.insertNote()
        }else{
            viewModel.updateNote()
        }
        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
    }

}