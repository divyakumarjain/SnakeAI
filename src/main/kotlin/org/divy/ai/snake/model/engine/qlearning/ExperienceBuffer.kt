package org.divy.ai.snake.model.engine.qlearning

interface ExperienceBuffer<T> {
    fun selectExperiencesForTraining(): Set<T>
    fun add(experience: T)
}
