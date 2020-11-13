package com.example.prueba;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    public final String miToken = FirebaseInstanceId.getInstance().getToken();
}
