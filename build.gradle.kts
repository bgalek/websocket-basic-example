plugins {
    application
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.java-websocket:Java-WebSocket:1.5.1")
}

application {
    mainClass.set("websocket.java.App")
}
