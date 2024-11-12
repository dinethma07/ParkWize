package com.example.parkwizproject.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class alertService {
    public static void showAlert(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message for the alert dialog
        builder.setTitle(title);
        builder.setMessage(message);

        // Set the positive button with an action
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Action to take when OK is pressed (dismiss dialog)
                dialog.dismiss();
            }
        });

        // Optional: Set a negative button if needed
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Action to take when Cancel is pressed (dismiss dialog)
                dialog.dismiss();
            }
        });

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
