package dev.vihang.firestore4k.internal

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap
import kotlinx.serialization.properties.encodeToMap
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Testcontainers
@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OperationsTest {

    private val usersCreated = mutableSetOf<User>()

    @Test
    fun `test add`() = runBlocking {
        val user = User().also(usersCreated::add)
        val userId = add("users", user)
        assertEquals(
            expected = user,
            actual = get("users/$userId")
        )
        val message = Message()
        val messageId = add("users/$userId/messages", message)
        assertEquals(
            expected = message,
            actual = get("users/$userId/messages/$messageId")
        )
    }

    @Test
    fun `test put`() = runBlocking {
        val user = User().also(usersCreated::add)
        put("users/${user.id}", user)
        assertEquals(
            expected = user,
            actual = get("users/${user.id}")
        )
        val message = Message()
        put("users/${user.id}/messages/${message.id}", message)
        assertEquals(
            expected = message,
            actual = get("users/${user.id}/messages/${message.id}")
        )
    }

    @Test
    fun `test getAll`() = runBlocking {
        val user = User().also(usersCreated::add)
        put("users/${user.id}", user)
        val messagesCreated = mutableSetOf<Message>()
        repeat(7) {
            val message = Message().also(messagesCreated::add)
            put("users/${user.id}/messages/${message.id}", message)
        }

        assertEquals(
            expected = usersCreated,
            actual = getAll<User>("users").toSet()
        )

        assertEquals(
            expected = messagesCreated,
            actual = getAll<Message>("users/${user.id}/messages").toSet()
        )
    }

    @Test
    fun `test delete document`() = runBlocking {
        deleteDocument("users/user1/messages/message1")
        deleteDocument("users/user1")
    }

    @Test
    fun `test delete collection`() = runBlocking {

        repeat(7) {
            val user = User().also(usersCreated::add)
            put("users/${user.id}", user)
            repeat(7) {
                add("users/${user.id}/messages", Message())
            }
        }

        val user = User().also(usersCreated::add)
        put("users/${user.id}", user)
        repeat(7) {
            add("users/${user.id}/messages", Message())
        }

        assertTrue(getAll<Message>("users/${user.id}/messages").isNotEmpty())
        deleteCollection("users/${user.id}/messages")
        assertTrue(getAll<Message>("users/${user.id}/messages").isEmpty())

        assertTrue(getAll<User>("users").isNotEmpty())
        deleteCollection("users")
        assertTrue(getAll<User>("users").isEmpty())

        usersCreated.clear()
    }

    @BeforeAll
    fun safetyCheck() {
        if (System.getenv("FIRESTORE_EMULATOR_HOST").isNullOrBlank()) {
            throw Exception("Preventing test to run on real GCP Firestore instead of firestore-emulator")
        }
    }

    companion object {

        @Container
        @JvmStatic
        val environment = KDockerComposeContainer(File("src/test/resources/docker-compose-test.yaml"))
            .withExposedService(
                "firestore-emulator",
                5173,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)),
            )
            .withLocalCompose(true)
    }
}
