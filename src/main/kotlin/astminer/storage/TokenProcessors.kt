package astminer.storage

import astminer.cli.separateToken
import astminer.common.DEFAULT_TOKEN
import astminer.common.model.Node
import astminer.common.normalizeToken

/**
 * A function that should calculate a node's token.
 */
typealias TokenProcessor = (Node) -> String

val splitTokenProcessor: TokenProcessor = { node -> separateToken(node.getToken()) }

/**
 * Returns the original unchanged token.
 * Works like the identity function id: x --> x, hence the name.
 */
val identityTokenProcessor: TokenProcessor = { node -> node.getToken() }

/**
 * Processes the token according to the original code2vec implementation in order to match their behavior.
 */
val code2vecTokenProcessor: TokenProcessor = { node -> normalizeToken(node.getToken(), DEFAULT_TOKEN) }
