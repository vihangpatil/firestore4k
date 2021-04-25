package dev.vihang.firestore4k.internal

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import org.slf4j.LoggerFactory

val firestoreClient: Firestore by lazy {

    val logger = LoggerFactory.getLogger("dev.vihang.firestore4k.internal.FirestoreClientKt")
    val firestoreEmulatorHost = System.getenv("FIRESTORE_EMULATOR_HOST")
    if (firestoreEmulatorHost == null) {
        logger.info("Connecting to GCP Firestore")
    } else {
        logger.warn("Connecting to Firestore emulator at $firestoreEmulatorHost")
    }
    FirestoreOptions
        .getDefaultInstance()
        .service
}