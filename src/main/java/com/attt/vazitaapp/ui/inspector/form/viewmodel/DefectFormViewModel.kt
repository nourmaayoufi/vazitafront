package com.attt.vazitaapp.ui.inspector.form.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.attt.vazitaapp.data.model.Alteration
import com.attt.vazitaapp.data.model.CarQueue
import com.attt.vazitaapp.data.model.Chapter
import com.attt.vazitaapp.data.model.DefectForm
import com.attt.vazitaapp.data.model.DefectPoint
import com.attt.vazitaapp.data.repository.DefectFormRepository
import com.attt.vazitaapp.data.repository.InspectionRepository
import com.attt.vazitaapp.ui.common.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefectFormViewModel @Inject constructor(
    private val defectFormRepository: DefectFormRepository,
    private val inspectionRepository: InspectionRepository
) : BaseViewModel() {

    private val _carDossier = MutableLiveData<CarQueue>()
    val carDossier: LiveData<CarQueue> = _carDossier

    private val _chapters = MutableLiveData<List<Chapter>>()
    val chapters: LiveData<List<Chapter>> = _chapters

    private val _points = MutableLiveData<List<DefectPoint>>()
    val points: LiveData<List<DefectPoint>> = _points

    private val _alterations = MutableLiveData<List<Alteration>>()
    val alterations: LiveData<List<Alteration>> = _alterations

    private val _selectedChapter = MutableLiveData<Chapter>()
    val selectedChapter: LiveData<Chapter> = _selectedChapter

    private val _selectedPoint = MutableLiveData<DefectPoint>()
    val selectedPoint: LiveData<DefectPoint> = _selectedPoint

    private val _selectedAlterations = MutableLiveData<MutableList<Alteration>>(mutableListOf())
    val selectedAlterations: LiveData<MutableList<Alteration>> = _selectedAlterations

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _formCompleteEvent = MutableLiveData<Boolean>()
    val formCompleteEvent: LiveData<Boolean> = _formCompleteEvent

    private val _errorEvent = MutableLiveData<String>()
    val errorEvent: LiveData<String> = _errorEvent

    fun setCarDossier(carDossier: CarQueue) {
        _carDossier.value = carDossier
        loadChapters()
    }

    private fun loadChapters() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val chapters = inspectionRepository.getChapters()
                _chapters.value = chapters
            } catch (e: Exception) {
                _errorEvent.value = e.message ?: "Failed to load chapters"
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectChapter(chapter: Chapter) {
        _selectedChapter.value = chapter
        loadPoints(chapter.codeChapter)
    }

    private fun loadPoints(chapterCode: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val points = inspectionRepository.getPointsByChapter(chapterCode)
                _points.value = points
            } catch (e: Exception) {
                _errorEvent.value = e.message ?: "Failed to load defect points"
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectPoint(point: DefectPoint) {
        _selectedPoint.value = point
        loadAlterations(point.codeChapter, point.codePoint)
    }

    private fun loadAlterations(chapterCode: String, pointCode: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val alterations = inspectionRepository.getAlterationsByPointAndChapter(chapterCode, pointCode)
                _alterations.value = alterations
            } catch (e: Exception) {
                _errorEvent.value = e.message ?: "Failed to load alterations"
            } finally {
                _loading.value = false
            }
        }
    }

    fun toggleAlteration(alteration: Alteration) {
        val currentList = _selectedAlterations.value ?: mutableListOf()

        if (currentList.contains(alteration)) {
            currentList.remove(alteration)
        } else {
            currentList.add(alteration)
        }

        _selectedAlterations.value = currentList
    }

    fun isAlterationSelected(alteration: Alteration): Boolean {
        return _selectedAlterations.value?.contains(alteration) ?: false
    }

    fun resetSelections() {
        _selectedAlterations.value = mutableListOf()
    }

    fun submitForm() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val carDossier = _carDossier.value ?: throw IllegalStateException("Car dossier is not set")
                val selectedAlterations = _selectedAlterations.value ?: throw IllegalStateException("No alterations selected")

                if (selectedAlterations.isEmpty()) {
                    _errorEvent.value = "Please select at least one defect"
                    return@launch
                }

                val defectCodes = selectedAlterations.map { alteration ->
                    "${alteration.codeChapter}${alteration.codePoint}${alteration.codeAlteration}"
                }

                val defectForm = DefectForm(
                    nDossier = carDossier.nDossier,
                    numChassis = carDossier.numChassis,
                    defectCodes = defectCodes
                )

                defectFormRepository.submitDefectForm(defectForm)
                _formCompleteEvent.value = true
            } catch (e: Exception) {
                _errorEvent.value = e.message ?: "Failed to submit form"
            } finally {
                _loading.value = false
            }
        }
    }
}