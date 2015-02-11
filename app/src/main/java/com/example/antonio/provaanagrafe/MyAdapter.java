package com.example.antonio.provaanagrafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Antonio on 07/02/2015.
 */
public class MyAdapter extends ArrayAdapter<Utente> {

    public MyAdapter(Context context) {
        super(context, 0, new ArrayList<Utente>());
    }

    @Override
    public View getView(int position, View View, ViewGroup parent) {
        Utente utente = getItem(position);
        MyViewHolder viewHolder;
        if (View == null) {
            viewHolder = new MyViewHolder();
            View = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
            viewHolder.firstLine = (TextView) View.findViewById(R.id.first_line);
            View.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) View.getTag();
        }
        viewHolder.firstLine.setText(utente.getNome() + " " + utente.getCognome());
        return View;
    }

    public class MyViewHolder {
        TextView firstLine;
    }
}
