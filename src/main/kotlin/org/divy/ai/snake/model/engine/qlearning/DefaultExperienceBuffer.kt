package org.divy.ai.snake.model.engine.qlearning
import java.util.*
import java.util.stream.IntStream


class DefaultExperienceBuffer<T>(private val experienceBufferSize: Int) : ExperienceBuffer<T> {
    private val trainingExperiences: LinkedList<T> = LinkedList()

    override fun selectExperiencesForTraining(): Set<T> {

        var currentSize = trainingExperiences.size

        while(currentSize > experienceBufferSize) {
            trainingExperiences.remove()
            currentSize--
        }

        val selectedExperiences: MutableSet<T> = mutableSetOf()
        for (i in IntStream.range(0, trainingExperiences.size / 2))
            selectedExperiences.add(trainingExperiences.random())
        return selectedExperiences
    }

    override fun add(experience: T) {
        if(!trainingExperiences.contains(experience)) {
            this.trainingExperiences.add(experience)
        }
    }
}
