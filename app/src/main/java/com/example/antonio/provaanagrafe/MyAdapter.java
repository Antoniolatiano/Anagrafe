package com.example.antonio.provaanagrafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.antonio.provaanagrafe.data.Todo;

import java.util.ArrayList;

/**
 * Created by Antonio on 07/02/2015.
 */
public class MyAdapter extends ArrayAdapter<Todo> {

    public MyAdapter(Context context) {
        super(context, 0, new ArrayList<Todo>());
    }

    @Override
    public View getView(int position, View View, ViewGroup parent) {
        Todo todo = getItem(position);
        MyViewHolder viewHolder;
        if (View == null) {
            viewHolder = new MyViewHolder();
            View = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
            viewHolder.firstLine = (TextView) View.findViewById(R.id.first_line);
            View.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) View.getTag();
        }
        viewHolder.firstLine.setText(todo.getMessage() + " " + todo.getUserID());
        return View;
    }

    public class MyViewHolder {
        TextView firstLine;
    }
}
