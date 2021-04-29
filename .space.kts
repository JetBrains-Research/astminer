job("Test") {
    container("ubuntu") {
        shellScript {
            content = """
              ./gradlew test    
          """
        }
    }
}

job("Release") {
    startOn {
        gitPush {
            enabled = false
        }
    }

    container("ubuntu") {
        env["PUBLISH_USER"] = Secrets("publish_user")
        env["PUBLISH_PASSWORD"] = Secrets("publish_password")

        shellScript {
            content = """
              ./gradlew build publish    
          """
        }
    }
}