package astminer.storage

import astminer.common.TOKEN_DELIMITER
import org.junit.Test

internal class CompressingKtTest {
    @Test
    fun `When processing non bamboo trees nothing changes`() {
        val tree = tree {
            typeLabel = "root"
            child {
                typeLabel = "child one"
                originalToken = "someToken"
            }
            child {
                typeLabel = "child 2"
                originalToken = "also some token"
            }
        }
        assertTreesEquals(tree, tree.structurallyNormalized())
    }

    @Test
    fun `When processing simple bamboo only one node should remain`() {
        val bamboo = tree {
            typeLabel = "root"
            child {
                typeLabel = "hello"
                child {
                    typeLabel = "world"
                    child {
                        typeLabel = "I"
                        child {
                            typeLabel = "am"
                            child {
                                typeLabel = "bamboo"
                                originalToken = "someVeryImportantToken"
                            }
                        }
                    }
                }
            }
        }

        val expected = tree {
            typeLabel = listOf("root", "hello", "world", "I", "am", "bamboo").joinToString(TOKEN_DELIMITER)
            originalToken = "someVeryImportantToken"
        }

        assertTreesEquals(expected, bamboo.structurallyNormalized())
    }

    @Test
    fun `When processing non leaf token containing node it shouldn't be compressed`() {
        val tree = tree {
            typeLabel = "root"
            child {
                typeLabel = "someFunction"
                originalToken = "helloWorld"
                child {
                    typeLabel = "body"
                    child {
                        typeLabel = "anotherFunctionCall"
                        child {
                            typeLabel = "helloWorldCall"
                        }
                    }
                }
            }
        }

        val expected = tree {
            typeLabel = "root"
            child {
                typeLabel = "someFunction"
                child {
                    typeLabel = GENERATED_NODE
                    originalToken = "helloWorld"
                }
                child {
                    typeLabel = listOf("body", "anotherFunctionCall", "helloWorldCall").joinToString(TOKEN_DELIMITER)
                }
            }
        }

        assertTreesEquals(expected, tree.structurallyNormalized())
    }
}
