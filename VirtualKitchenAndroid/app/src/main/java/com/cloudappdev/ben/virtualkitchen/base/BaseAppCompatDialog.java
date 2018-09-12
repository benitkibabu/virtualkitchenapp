package com.cloudappdev.ben.virtualkitchen.base;

import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.widget.Toast;

public class BaseAppCompatDialog extends AppCompatDialogFragment {
    public void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
    public boolean validateEmail(String str){
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
        }
    }
}
