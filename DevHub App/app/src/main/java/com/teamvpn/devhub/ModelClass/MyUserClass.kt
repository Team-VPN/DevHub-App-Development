package com.teamvpn.devhub.ModelClass


class MyUserClass {
    private var uid: String = ""
    private var username: String = ""
    private var fullname: String = ""
    private var sex: String = ""
    private var dob: String = ""
    private var phoneNumber: String = ""
    private var email: String = ""
    private var github:String=""
    private var skills: MutableList<String> = mutableListOf<String>()
    private var image_url: String = ""

    constructor()


    constructor(
            uid: String,
            username: String,
            fullname:String,
            sex:String,
            dob:String,
            phoneNumber:String,
            email:String,
            github:String,
            skills:MutableList<String>,
            image_url: String
    ) {
        this.uid = uid
        this.username = username
        this.fullname = fullname
        this.sex = sex
        this.dob = dob
        this.phoneNumber = phoneNumber
        this.email = email
        this.github=github
        this.skills = skills
        this.image_url= image_url
    }
    //******UID*******
    fun getUID(): String?{
        return uid
    }
    fun setUID (uid: String){
        this.uid = uid
    }
    //******USERNAME*******
    fun getUserName(): String?{
        return username
    }
    fun setUserName (username: String){
        this.username = username
    }
    //******FULLNAME*******
    fun getFullName(): String?{
        return fullname
    }
    fun setFullName (fullname: String){
        this.fullname = fullname
    }
    //******SEX*******
    fun getSex(): String?{
        return sex
    }

    //******USERNAME*******
    fun getDOB(): String?{
        return dob
    }

    //******PHONENUMBER*******
    fun getPhoneNumber():String?{
        return phoneNumber
    }
    fun setPhoneNumber(phoneNumber: String){
        this.phoneNumber = phoneNumber
    }

    //********email**********
    fun setEmail(email:String){
        this.email = email
    }
    fun getEmail():String?{
        return email
    }

    //******Github*********
    fun setgithub(github: String){
        this.github=github
    }
    fun getgithub():String?{
        return github
    }

    //******skills**********
    fun setSkills(skills:MutableList<String>){
        this.skills = skills
    }
    fun getSkills():MutableList<String>?{
        return skills
    }

    //*******image url**********
    fun setImageUrl(image_url:String){
        this.image_url = image_url
    }
    fun getImageUrl():String?{
        return image_url
    }
}