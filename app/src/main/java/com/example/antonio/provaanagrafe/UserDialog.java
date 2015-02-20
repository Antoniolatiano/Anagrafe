package com.example.antonio.provaanagrafe;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Antonio on 04/02/2015.
 */
public class UserDialog {
    final Dialog dialog;
    final TextView NomeTV;
    final TextView CognomeTV;
    final MainActivity context;
    Utente userToModify = null;
    int position = -1;

    public UserDialog(final MainActivity context) {
        dialog = new Dialog(context);
        this.context = context;
        dialog.setContentView(R.layout.add_user_dialog);
        // set the custom dialog components - text, image and button
        NomeTV = (TextView) dialog.findViewById(R.id.NomeTV);
        CognomeTV = (TextView) dialog.findViewById(R.id.CognomeTV);
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
                if (NomeTV.getText().length() != 0 && CognomeTV.getText().length() != 0) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int result;
                                if (userToModify != null) {
                                    result = context.utils.modificaUtente(userToModify, new Utente(NomeTV.getText().toString(), CognomeTV.getText().toString()));
                                } else {
                                    result = context.utils.aggiungiUtente(NomeTV.getText().toString(), CognomeTV.getText().toString());
                                }
                                switch (result) {
                                    case HTTPUtils.operazione_effettuata:
                                        if (userToModify == null) {
                                            context.MakeToast("Utente aggiunto", Toast.LENGTH_SHORT);
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    context.AddUser(new Utente(NomeTV.getText().toString(), CognomeTV.getText().toString()));
                                                }
                                            });
                                        } else {
                                            context.MakeToast("Utente modificato", Toast.LENGTH_SHORT);
                                            context.assets.ottieniListaUtenti().set(position, new Utente(NomeTV.getText().toString(), CognomeTV.getText().toString()));
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    context.UpdateTable();
                                                }
                                            });
                                        }
                                        userToModify = null;
                                        position = -1;
                                        dialog.dismiss();
                                        break;
                                    case HTTPUtils.utente_già_registrato:
                                        context.MakeToast("Utente già registrato", Toast.LENGTH_SHORT);
                                        break;
                                    case HTTPUtils.errore_modifica_utente:
                                        context.MakeToast("Errore nella modifica dell'utente", Toast.LENGTH_SHORT);
                                        break;
                                    default:
                                        break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            }
        });
    }

    public void showDialog() {
        NomeTV.setText("");
        CognomeTV.setText("");
        dialog.setTitle("Aggiungi Utente");
        dialog.show();
    }

    public void showModUserDialog(Utente toMod, int position) {
        userToModify = toMod;
        this.position = position;
        NomeTV.setText(toMod.getNome());
        CognomeTV.setText(toMod.getCognome());
        dialog.setTitle("Modifica Utente");
        dialog.show();
    }
}
