package dev.vihang.firestore4k.dynamic

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PathCompositionTest {

    @Test
    fun `test root collection path`() {
        assertEquals(
            expected = CollectionPath("users"),
            actual = collection("users")
        )
        assertEquals(
            expected = CollectionPath("users"),
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
            expected = DocumentPath(CollectionPath("users"), "user1"),
            actual = collection("users") / "user1"
        )
        assertEquals(
            expected = DocumentPath(CollectionPath("users"), "user1"),
            actual = users / "user1"
        )
        assertEquals(
            expected = "users/user1",
            actual = (users / "user1").toString()
        )
    }

    @Test
    fun `test sub collection path`() {
        assertEquals(
            expected = CollectionPath(DocumentPath(CollectionPath("users"), "user1"), "messages"),
            actual = collection("users") / "user1" / collection("messages")
        )
        assertEquals(
            expected = CollectionPath(DocumentPath(CollectionPath("users"), "user1"), "messages"),
            actual = users / "user1" / messages
        )
        assertEquals(
            expected = "users/user1/messages",
            actual = (users / "user1" / messages).toString()
        )
    }

    @Test
    fun `test sub collection's document path`() {
        assertEquals(
            expected = DocumentPath(
                CollectionPath(DocumentPath(CollectionPath("users"), "user1"), "messages"),
                "message1"
            ),
            actual = collection("users") / "user1" / collection("messages") / "message1"
        )
        assertEquals(
            expected = DocumentPath(
                CollectionPath(DocumentPath(CollectionPath("users"), "user1"), "messages"),
                "message1"
            ),
            actual = users / "user1" / messages / "message1"
        )
        assertEquals(
            expected = "users/user1/messages/message1",
            actual = (users / "user1" / messages / "message1").toString()
        )
    }

    @Test
    fun `test extract parent type`() {
        val messagePath = users / "user1" / messages / "message1"

        assertEquals(
            expected = "message1",
            actual = messagePath.documentId
        )

        val messagesPath = messagePath.collectionPath
        assertEquals(
            expected = users / "user1" / messages,
            actual = messagesPath
        )

        val userPath = messagesPath.documentPath
        assertEquals(
            expected = users / "user1",
            actual = userPath
        )

        val usersPath = userPath?.collectionPath
        assertEquals(
            expected = users,
            actual = usersPath
        )
    }
}