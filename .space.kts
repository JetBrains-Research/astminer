job("Release") {
    startOn {
        gitPush {
            enabled = false
        }
    }

    container("openjdk:11") {
        env["PUBLISH_USER"] = Secrets("PUBLISH_USER")
        env["PUBLISH_PASSWORD"] = Secrets("PUBLISH_PASSWORD")
        
        shellScript {
            content = """
              ./gradlew publish    
          """
        }
    }
}