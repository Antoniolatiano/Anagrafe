package com.example.antonio.provaanagrafe;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.antonio.provaanagrafe.data.Todo;
import com.example.antonio.provaanagrafe.network.NetworkOperations;

/**
 * Created by Antonio on 04/02/2015.
 */
public class UserDialog {
    final Dialog dialog;
    final TextView MessaggioTV;
    final MainActivity context;
    //Utente userToModify = null;
    int position = -1;

    public UserDialog(final MainActivity context) {
        dialog = new Dialog(context);
        this.context = context;
        dialog.setContentView(R.layout.add_user_dialog);
        // set the custom dialog components - text, image and button
        MessaggioTV = (TextView) dialog.findViewById(R.id.MessaggioTV);
        Button AnnullaButton = (Button) dialog.findViewById(R.id.AnnullaButton);
        Button ConfermaButton = (Button) dialog.findViewById(R.id.confermaButton);
        AnnullaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ConfermaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MessaggioTV.getText().length() != 0) {
                    Intent i = new Intent(Assets.actionNetworkOperation);
                    i.putExtra("operation", NetworkOperations.EnumOperations.ADD_TODO);
                    i.putExtra("data", new Todo(MessaggioTV.getText().toString(), "prova android"));
                    context.sendBroadcast(i);
                }
            }
        });
    }

    public void showDialog() {
        MessaggioTV.setText("");
        dialog.setTitle("Aggiungi Utente");
        dialog.show();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        dialog.dismiss();
    }
/*
    public void showModUserDialog(Utente toMod, int position) {
        userToModify = toMod;
        this.position = position;
        NomeTV.setText(toMod.getNome());
        CognomeTV.setText(toMod.getCognome());
        dialog.setTitle("Modifica Utente");
        dialog.show();
    }*/
}
