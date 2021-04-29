job("Release") {
    startOn {
        gitPush {
            enabled = false
        }
    }

    container("openjdk:11") {
        shellScript {
            content = """
              ./gradlew publish    
          """
        }
    }
}