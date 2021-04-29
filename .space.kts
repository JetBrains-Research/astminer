job("Release") {
    startOn {
        gitPush {
            enabled = false
        }
    }

    container("openjdk:11") {
        env["publish-user"] = Secrets("publish-user")
        env["publish-password"] = Secrets("publish-password")

        shellScript {
            content = """
              echo ${'$'}test-secret
              ./gradlew publish    
          """
        }
    }
}