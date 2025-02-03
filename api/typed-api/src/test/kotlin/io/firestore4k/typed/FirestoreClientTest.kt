package io.firestore4k.typed

import com.google.cloud.firestore.FirestoreOptions
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FirestoreClientTest {

    private lateinit var firestoreClient: FirestoreClient

    private val usersCreated = mutableSetOf<User>()

    @Test
    fun `test add`() = runBlocking {
        val user = User().also(usersCreated::add)
        val userId = UserId(firestoreClient.add(users, user))
        assertEquals(
            expected = user,
            actual = firestoreClient.get(users / userId)
        )
        val message = Message()
        val messageId = MessageId(firestoreClient.add(users / userId / messages, message))
        assertEquals(
            expected = message,
            actual = firestoreClient.get(users / userId / messages / messageId)
        )
    }

    @Test
    fun `test put`() = runBlocking {
        val user = User().also(usersCreated::add)
        firestoreClient.put(users / UserId(user.id), user)
        assertEquals(
            expected = user,
            actual = firestoreClient.get(users / UserId(user.id))
        )
        val message = Message()
        firestoreClient.put(users / UserId(user.id) / messages / MessageId(message.id), message)
        assertEquals(
            expected = message,
            actual = firestoreClient.get(users / UserId(user.id) / messages / MessageId(message.id))
        )
    }

    @Test
    fun `test getAll`() = runBlocking {
        val user = User().also(usersCreated::add)
        firestoreClient.put(users / UserId(user.id), user)
        val messagesCreated = mutableSetOf<Message>()
        repeat(7) {
            val message = Message().also(messagesCreated::add)
            firestoreClient.put(users / UserId(user.id) / messages / MessageId(message.id), message)
        }

        assertEquals(
            expected = usersCreated,
            actual = firestoreClient.getAll(users).toSet()
        )

        assertEquals(
            expected = messagesCreated,
            actual = firestoreClient.getAll(users / UserId(user.id) / messages).toSet()
        )
    }

    @Test
    fun `test delete document`() = runBlocking {
        firestoreClient.delete(users / UserId("user1") / messages / MessageId("message1"))
        firestoreClient.delete(users / UserId("user1"))
    }

    @Test
    fun `test delete collection`() = runBlocking {

        repeat(7) {
            val user = User().also(usersCreated::add)
            firestoreClient.put(users / UserId(user.id), user)
            repeat(7) {
                firestoreClient.add(users / UserId(user.id) / messages, Message())
            }
        }

        val user = User().also(usersCreated::add)
        firestoreClient.put(users / UserId(user.id), user)
        repeat(7) {
            firestoreClient.add(users / UserId(user.id) / messages, Message())
        }

        assertTrue(firestoreClient.getAll<Message>(users / UserId(user.id) / messages).isNotEmpty())
        firestoreClient.deleteAll(users / UserId(user.id) / messages)
        assertTrue(firestoreClient.getAll<Message>(users / UserId(user.id) / messages).isEmpty())

        assertTrue(firestoreClient.getAll<User>(users).isNotEmpty())
        firestoreClient.deleteAll(users)
        assertTrue(firestoreClient.getAll<User>(users).isEmpty())

        usersCreated.clear()
    }

    @BeforeAll
    fun safetyCheck() {
        if (System.getenv("FIRESTORE_EMULATOR_HOST").isNullOrBlank()) {
            throw Exception("Preventing test to run on real GCP Firestore instead of firestore-emulator")
        }
        firestoreClient = FirestoreClient(
            FirestoreOptions
                .newBuilder()
                .setEmulatorHost("0.0.0.0:" + firestore.getMappedPort(5173))
                .build()
        )
    }

    companion object {

        @Container
        @JvmStatic
        private val firestore = GenericContainer(DockerImageName.parse("google/cloud-sdk:508.0.0-emulators"))
            .withExposedPorts(5173)
            .withCommand("gcloud", "beta", "emulators", "firestore", "start", "--host-port=0.0.0.0:5173")
    }
}
