package dev.vihang.example

import dev.vihang.firestore4k.typed.ChildOf
import dev.vihang.firestore4k.typed.Collection
import dev.vihang.firestore4k.typed.IdOf

@Collection("organizations")
data class Organization(
    val orgId: String,
    val orgName: String,
)

@IdOf("organizations")
@JvmInline
value class OrgId(private val value: String) {
    override fun toString(): String = value
}

@Collection("departments")
@ChildOf("organizations")
data class Department(
    val departmentId: String,
    val departmentName: String,
) {

    // Needed for DSL
    companion object
}

@IdOf("departments")
@JvmInline
value class DepartmentId(private val value: String) {
    override fun toString(): String = value
}

@Collection("employees")
@ChildOf("departments")
data class Employee(
    val employeeId: String,
) {

    // Needed for DSL
    companion object
}

@IdOf("employees")
@JvmInline
value class EmployeeId(private val value: String)  {
    override fun toString(): String = value
}