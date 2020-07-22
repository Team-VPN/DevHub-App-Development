package com.teamvpn.devhub.ModelClass

class Users {
    private var uid: String = ""
    private var username: String = ""
    private var profile: String = ""
    private var cover: String = ""
    private var status: String = ""
    private var search: String = ""
    private var github: String = ""
    private var linkedin: String = ""
    private var stackof: String = ""
    private var image_url: String = ""

    constructor()


    constructor(
        uid: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String,
        github: String,
        linkedin: String,
        stackof: String,
        image_url: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.github = github
        this.linkedin = linkedin
        this.stackof = stackof
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
    // ****Profile******
    fun getProfile(): String?{
        return profile
    }
    fun setProfile (profile: String){
        this.profile = profile
    }
    // break
    fun getCover(): String?{
        return cover
    }
    fun setCover (cover: String){
        this.cover = cover

    }
    // break
    fun getStatus(): String?{
        return status
    }
    fun setStatus (status: String){
        this.status = status

    }
    // break
    fun getSearch(): String?{
        return search
    }
    fun setSearch (search: String){
        this.search = search

    }
    // break
    fun getGithub(): String?{
        return github
    }
    fun setGithub (github: String){
        this.github = github

    }
    // break
    fun getLinkedin(): String?{
        return linkedin
    }
    fun setLinkedin (linkedin: String){
        this.linkedin = linkedin

    }
    // break
    fun getStackof(): String?{
        return stackof
    }
    fun setStackof (stackof: String){
        this.stackof = stackof
    }
    // break
    fun getImageOff(): String?{
        return image_url
    }
    fun setImageOff (image_url: String){
        this.image_url = image_url
    }
    // break


}