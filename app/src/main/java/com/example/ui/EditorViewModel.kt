package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.EditingState
import com.example.processor.ImageProcessor
import com.example.util.ImageSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class EditorTab {
    ADJUST, FILTERS, CROP, WATERMARK
}

class EditorViewModel : ViewModel() {

    private val _editingState = MutableStateFlow(EditingState())
    val editingState: StateFlow<EditingState> = _editingState.asStateFlow()

    private val _currentTab = MutableStateFlow(EditorTab.ADJUST)
    val currentTab: StateFlow<EditorTab> = _currentTab.asStateFlow()

    private val _originalBitmap = MutableStateFlow<Bitmap?>(null)
    val originalBitmap: StateFlow<Bitmap?> = _originalBitmap.asStateFlow()

    private val _isComparing = MutableStateFlow(false)
    val isComparing: StateFlow<Boolean> = _isComparing.asStateFlow()

    private val _showHistogram = MutableStateFlow(false)
    val showHistogram: StateFlow<Boolean> = _showHistogram.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    // Undo / Redo stacks
    private val undoStack = mutableListOf<EditingState>()
    private val redoStack = mutableListOf<EditingState>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    fun loadImage(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                _originalBitmap.value = bitmap
                _editingState.value = EditingState()
                undoStack.clear()
                redoStack.clear()
                updateUndoRedoStates()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateState(newState: EditingState) {
        if (_editingState.value != newState) {
            undoStack.add(_editingState.value)
            if (undoStack.size > 25) undoStack.removeAt(0)
            redoStack.clear()
            _editingState.value = newState
            updateUndoRedoStates()
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(_editingState.value)
            val prev = undoStack.removeAt(undoStack.size - 1)
            _editingState.value = prev
            updateUndoRedoStates()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(_editingState.value)
            val next = redoStack.removeAt(redoStack.size - 1)
            _editingState.value = next
            updateUndoRedoStates()
        }
    }

    fun resetAll() {
        updateState(EditingState())
    }

    fun setTab(tab: EditorTab) {
        _currentTab.value = tab
    }

    fun setComparing(comparing: Boolean) {
        _isComparing.value = comparing
    }

    fun toggleHistogram() {
        _showHistogram.value = !_showHistogram.value
    }

    private fun updateUndoRedoStates() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }

    fun saveImageToGallery(context: Context, onSaved: (Uri?) -> Unit) {
        val bitmap = _originalBitmap.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            val savedUri = ImageSaver.saveToGallery(context, bitmap, _editingState.value)
            _isSaving.value = false
            onSaved(savedUri)
        }
    }
}
