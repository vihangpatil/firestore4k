package dev.vihang.example

import dev.vihang.firestore4k.typed.div
import dev.vihang.firestore4k.typed.get
import dev.vihang.firestore4k.typed.getAll
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