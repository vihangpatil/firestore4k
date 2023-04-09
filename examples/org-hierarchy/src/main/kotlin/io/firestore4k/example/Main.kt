package io.firestore4k.example

import io.firestore4k.typed.FirestoreClient
import io.firestore4k.typed.div
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {

    val firestoreClient = FirestoreClient()

    // get all root collection
    firestoreClient.getAll(organizations)

    // get root document
    firestoreClient.get(organizations / OrgId("org1"))

    // get all sub collection
    firestoreClient.getAll(organizations / OrgId("org1") / departments)

    // get a sub collection document
    firestoreClient.get(organizations / OrgId("org1") / departments / DepartmentId("R&D"))

    // get all child collection
    firestoreClient.getAll(organizations / OrgId("org1") / departments / DepartmentId("R&D") / employees)

    // get a sub collection document
    firestoreClient.get(organizations / OrgId("org1") / departments / DepartmentId("R&D") / employees / EmployeeId("47"))

    Unit
}