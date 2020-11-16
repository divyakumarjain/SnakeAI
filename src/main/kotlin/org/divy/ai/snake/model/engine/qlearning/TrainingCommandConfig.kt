package org.divy.ai.snake.model.engine.qlearning

import com.github.ajalt.clikt.parameters.options.OptionDelegate
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import org.divy.ai.snake.application.command.AbstractLearningCommand
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class TrainingCommandConfig<T>(private val delegate: OptionDelegate<T>) :
    ReadWriteProperty<AbstractLearningCommand, T> {
    private var _changeListener: ChangeListener? = null
    private var value: T? = null

    operator fun provideDelegate(thisRef: AbstractLearningCommand, prop: KProperty<*>): ReadWriteProperty<AbstractLearningCommand, T> {
        this.delegate.provideDelegate(thisRef, prop)
        thisRef.trainingParam[(delegate as OptionWithValues<*, *, *>).names.first()] = prop
        return this
    }

    override fun getValue(thisRef: AbstractLearningCommand, property: KProperty<*>): T {
        return if (value != null) {
            value!!
        } else
            this.delegate.getValue(thisRef, property)
    }

    override fun setValue(thisRef: AbstractLearningCommand, property: KProperty<*>, value: T) {
        this.value = value
        _changeListener?.listen(value)
    }

    fun changeListener(listener: ChangeListener): TrainingCommandConfig<T> {
        this._changeListener = listener
        return this
    }
}

fun <T> OptionDelegate<T>.modifiable(): TrainingCommandConfig<T> {
    return TrainingCommandConfig(this)
}

interface ChangeListener {
    fun <T> listen(value: T)
}


