package com.example.prueba;

public class usuarios {
    String userName, email, urlFoto, urlFotoFirestore, token;

    public usuarios() {}

    public usuarios(String userName, String email, String urlFoto, String urlFotoFirestore, String token) {
        this.userName = userName;
        this.email = email;
        this.urlFoto = urlFoto;
        this.urlFotoFirestore = urlFotoFirestore;
        this.token = token;
    }

    public String getUrlFotoFirestore() {
        return urlFotoFirestore;
    }

    public void setUrlFotoFirestore(String urlFotoFirestore) {
        this.urlFotoFirestore = urlFotoFirestore;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
