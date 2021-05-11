job("Test") {
    container(image="voudy/astminer") {
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

    container(image="voudy/astminer") {
        env["PUBLISH_USER"] = Secrets("publish_user")
        env["PUBLISH_PASSWORD"] = Secrets("publish_password")

        shellScript {
            content = """
              ./gradlew test publish    
          """
        }
    }
}