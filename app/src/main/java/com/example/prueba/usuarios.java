package com.example.prueba;

public class usuarios {
    String userName, email, urlFoto, token;

    public usuarios(String userName, String email, String urlFoto, String token) {
        this.userName = userName;
        this.email = email;
        this.urlFoto = urlFoto;
        this.token = token;
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
