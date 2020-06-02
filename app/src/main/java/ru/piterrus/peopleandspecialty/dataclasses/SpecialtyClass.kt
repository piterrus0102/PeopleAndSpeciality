package ru.piterrus.peopleandspecialty.dataclasses

class SpecialtyClass(specialty_id: Int, specialty_name: String) {
    var specialty_id: Int
    var specialty_name: String

    init {
        this.specialty_id = specialty_id
        this.specialty_name = specialty_name
    }
}