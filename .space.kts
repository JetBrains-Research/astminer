job("Release") {
    startOn {
        gitPush {
            branchFilter {
                +"refs/tags/*"
            }
        }
    }

    container(image="voudy/astminer:0.8.0") {
        env["PUBLISH_USER"] = Secrets("publish_user")
        env["PUBLISH_PASSWORD"] = Secrets("publish_password")

        shellScript {
            content = """
              ./gradlew test publish    
          """
        }
    }
}
