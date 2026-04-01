package com.designlife.justdo.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.designlife.justdo.MainActivity
import com.designlife.justdo.R
import com.designlife.justdo.common.presentation.components.ProgressBar
import com.designlife.justdo.setworkllm.SetworkOLLM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class OChatFragment : Fragment() {

    private var chatWindowProgress = mutableStateOf(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FLOW", "onCreate: progress : started")
        chatWindowProgress.value = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Box(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Log.i("FLOW", "onCreateView: progress during composition : ${chatWindowProgress.value}")
                    if (!chatWindowProgress.value){
                        MainActivity.setworkChat.ChatScreenView()
                    }
                    if (chatWindowProgress.value){
                        ProgressBar()
                    }
                }

            }
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}