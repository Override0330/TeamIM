package com.override0330.teamim.model.bean

import androidx.databinding.ObservableField

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class TaskStep (val taskItem: ObservableField<TaskItem>,
                val stepId: ObservableField<Int>,
                val pieceList: ObservableField<Piece>)