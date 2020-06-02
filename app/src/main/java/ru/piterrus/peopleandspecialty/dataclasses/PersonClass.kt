package ru.piterrus.peopleandspecialty.dataclasses

class PersonClass(f_name: String,
                  l_name: String,
                  birthday: String?,
                  avatr_url: String,
                  specialty: ArrayList<SpecialtyClass>) {

    var f_name: String
    var l_name: String
    var birthday: String? = null
    var avatr_url: String
    var specialty: ArrayList<SpecialtyClass>

    init {
        this.f_name = f_name
        this.l_name = l_name
        this.birthday = birthday
        this.avatr_url = avatr_url
        this.specialty = specialty
    }
}