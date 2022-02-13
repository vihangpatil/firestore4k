package io.firestore4k.typed

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PathCompositionTest {

    @Test
    fun `test root collection path`() {
        assertEquals(
            expected = RootCollection("users", User::class, UserId::class),
            actual = rootCollection("users")
        )
        assertEquals(
            expected = RootCollection("users", User::class, UserId::class),
            actual = users
        )
        assertEquals(
            expected = "users",
            actual = users.toString()
        )
    }

    @Test
    fun `test root collection's document path`() {
        assertEquals(
            expected = DocumentPath(RootCollectionPath(RootCollection("users", User::class, UserId::class)), UserId("user1")),
            actual = rootCollection<User, UserId>("users") / UserId("user1")
        )
        assertEquals(
            expected = DocumentPath(RootCollectionPath(RootCollection("users", User::class, UserId::class)), UserId("user1")),
            actual = users / UserId("user1")
        )
        assertEquals(
            expected = "users/user1",
            actual = (users / UserId("user1")).toString()
        )
    }

    @Test
    fun `test sub collection path`() {
        assertEquals(
            expected = SubCollectionPath(
                documentPath = DocumentPath(RootCollectionPath(RootCollection("users", User::class, UserId::class)), UserId("user1")),
                subCollection = SubCollection("messages", Message::class, RootCollection("users", User::class, UserId::class), MessageId::class)
            ),
            actual = rootCollection<User, UserId>("users") / UserId("user1") / (rootCollection<User, UserId>("users")).subCollection<Message, MessageId>("messages")
        )
        assertEquals(
            expected = SubCollectionPath(
                documentPath = DocumentPath(RootCollectionPath(RootCollection("users", User::class, UserId::class)), UserId("user1")),
                subCollection = SubCollection("messages", Message::class, RootCollection("users", User::class, UserId::class), MessageId::class)
            ),
            actual = users / UserId("user1") / messages
        )
        assertEquals(
            expected = "users/user1/messages",
            actual = (users / UserId("user1") / messages).toString()
        )
    }

    @Test
    fun `test sub collection's document path`() {
        assertEquals(
            expected = DocumentPath(
                SubCollectionPath(
                    documentPath = DocumentPath(RootCollectionPath(RootCollection("users", User::class, UserId::class)), UserId("user1")),
                    subCollection = SubCollection("messages", Message::class, RootCollection("users", User::class, UserId::class), MessageId::class)
                ),
                MessageId("message1")
            ),
            actual = rootCollection<User, UserId>("users") / UserId("user1") / rootCollection<User, UserId>("users").subCollection<Message, MessageId>("messages") / MessageId("message1")
        )
        assertEquals(
            expected = DocumentPath(
                SubCollectionPath(
                    documentPath = DocumentPath(RootCollectionPath(RootCollection("users", User::class, UserId::class)), UserId("user1")),
                    subCollection = SubCollection("messages", Message::class, RootCollection("users", User::class, UserId::class), MessageId::class)
                ),
                MessageId("message1")
            ),
            actual = users / UserId("user1") / messages / MessageId("message1")
        )
        assertEquals(
            expected = "users/user1/messages/message1",
            actual = (users / UserId("user1") / messages / MessageId("message1")).toString()
        )
    }

    @Test
    fun `test extract parent type`() {
        val messagePath = users / UserId("user1") / messages / MessageId("message1")

        assertEquals(
            expected = MessageId("message1"),
            actual = messagePath.documentId
        )

        val messagesPath = messagePath.collectionPath
        assertEquals(
            expected = users / UserId("user1") / messages,
            actual = messagesPath
        )

        val userPath = when (messagesPath) {
            is SubCollectionPath<*, *, * , *> -> messagesPath.documentPath
            is RootCollectionPath -> fail()
        }

        assertEquals(
            expected = users / UserId("user1"),
            actual = userPath
        )

        val usersCollection = when (val usersPath = userPath.collectionPath) {
            is RootCollectionPath -> usersPath.rootCollection
            is SubCollectionPath<*, *, *, *> -> fail()
        }

        assertEquals(
            expected = users,
            actual = usersCollection
        )
    }
}