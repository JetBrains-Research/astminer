package astminer.pipeline.branch

import astminer.common.model.LabeledResult
import astminer.common.model.LanguageHandler
import astminer.common.model.Node

/**
 * PipelineBranch is a part of the pipeline that encapsulate inside itself granularity based logic.
 * It accepts parsed files (LanguageHandler) and returns labeled results.
 */
interface PipelineBranch {
    /**
     * Extracts labeled results from LanguageHandler
     * May mutate the AST.
     * Should have no other side-effects
     */
    fun process(languageHandler: LanguageHandler<out Node>): List<LabeledResult<out Node>>
}
