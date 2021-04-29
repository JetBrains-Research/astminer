job("Test") {
    container("openjdk:11") {
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

    container("openjdk:11") {
        env["PUBLISH_USER"] = Secrets("publish_user")
        env["PUBLISH_PASSWORD"] = Secrets("publish_password")

        shellScript {
            content = """
              ./gradlew build publish    
          """
        }
    }
}