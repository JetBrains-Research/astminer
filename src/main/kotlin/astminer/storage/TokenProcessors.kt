package astminer.storage

import astminer.cli.separateToken
import astminer.common.DEFAULT_TOKEN
import astminer.common.model.Node
import astminer.common.normalizeToken

typealias TokenProcessor = (Node) -> String

val splitTokenProcessor: TokenProcessor = { node -> separateToken(node.getToken()) }

val identityTokenProcessor: TokenProcessor = { node -> node.getToken() }

val code2vecTokenProcessor: TokenProcessor = { node -> normalizeToken(node.getToken(), DEFAULT_TOKEN) }
