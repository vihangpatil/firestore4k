package io.firestore4k.example

import io.firestore4k.typed.div
import io.firestore4k.typed.get
import io.firestore4k.typed.getAll
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {

    // get all root collection
    getAll(organizations)

    // get root document
    get(organizations / OrgId("org1"))

    // get all sub collection
    getAll(organizations / OrgId("org1") / departments)

    // get a sub collection document
    get(organizations / OrgId("org1") / departments / DepartmentId("R&D"))

    // get all child collection
    getAll(organizations / OrgId("org1") / departments / DepartmentId("R&D") / employees)

    // get a sub collection document
    get(organizations / OrgId("org1") / departments / DepartmentId("R&D") / employees / EmployeeId("47"))

    Unit
}