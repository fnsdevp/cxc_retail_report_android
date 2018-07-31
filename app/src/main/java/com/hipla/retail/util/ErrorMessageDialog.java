package com.hipla.retail.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorMessageDialog {

    private Context context;
    private static ErrorMessageDialog errorMessageDialog;
    private static Context prevContext;

    private ErrorMessageDialog(Context context) {
        this.context = context;
    }

    public static ErrorMessageDialog getInstant(Context context) {
        if (errorMessageDialog == null) {
            prevContext = context;
            errorMessageDialog = new ErrorMessageDialog(context);
        }

        if (prevContext != context){
            errorMessageDialog = null;
            errorMessageDialog = new ErrorMessageDialog(context);
        }
        prevContext = context;
        return errorMessageDialog;
    }

    public void show(String msg) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Hipla Retail");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(msg);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                dialog.dismiss();
                return;
            }
        });
        dialog.show();
    }

    public void show(String msg,String hdr) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(hdr);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(msg);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                dialog.dismiss();
                return;
            }
        });
        dialog.show();
    }

}
