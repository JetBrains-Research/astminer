job("Test") {
    container("ubuntu") {
        shellScript {
            content = """
              apt-get update && apt-get install -y openjdk-8-jdk g++
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
              apt-get update && apt-get install -y openjdk-8-jdk build-essential
              ./gradlew build publish    
          """
        }
    }
}