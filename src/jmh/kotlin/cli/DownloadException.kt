package cli

import java.lang.RuntimeException

class DownloadException(message: String): RuntimeException(message) {}