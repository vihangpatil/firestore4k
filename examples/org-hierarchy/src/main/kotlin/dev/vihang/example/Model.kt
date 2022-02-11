package dev.vihang.example

import dev.vihang.firestore4k.typed.ChildOf
import dev.vihang.firestore4k.typed.Collection
import dev.vihang.firestore4k.typed.IdOf

@Collection("organizations")
@kotlinx.serialization.Serializable
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
@kotlinx.serialization.Serializable
data class Department(
    val departmentId: String,
    val departmentName: String,
)

@IdOf("departments")
@JvmInline
value class DepartmentId(private val value: String) {
    override fun toString(): String = value
}

@Collection("employees")
@ChildOf("departments")
@kotlinx.serialization.Serializable
data class Employee(
    val employeeId: String,
)

@IdOf("employees")
@JvmInline
value class EmployeeId(private val value: String)  {
    override fun toString(): String = value
}