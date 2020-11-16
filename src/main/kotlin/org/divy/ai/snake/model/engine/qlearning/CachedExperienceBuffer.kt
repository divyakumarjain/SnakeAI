package org.divy.ai.snake.model.engine.qlearning


class CachedExperienceBuffer<T>(private val experienceBufferSize: Int) : ExperienceBuffer<T> {
    private val buffer: ExperienceBuffer<T> = DefaultExperienceBuffer(experienceBufferSize)

    override fun selectExperiencesForTraining(): Set<T> {
        return buffer.selectExperiencesForTraining()
    }

    override fun add(experience: T) {
        return buffer.add(experience)
    }
}
