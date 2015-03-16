package com.example.antonio.provaanagrafe.network;

import com.example.antonio.provaanagrafe.data.Todo;

import java.io.Serializable;

/**
 * Created by Antonio on 12/03/2015.
 */

public class NetworkOperations {
    private EnumOperations op;
    private Todo data;

    public NetworkOperations(EnumOperations op, Todo data) {
        this.op = op;
        this.data = data;
    }

    public EnumOperations getOperation() {
        return op;
    }

    public Todo getData() {
        return data;
    }

    public enum EnumOperations implements Serializable {
        UPDATE, UPDATE_FROM_SERVICE, TIMESTAMP_UPDATE, ADD_TODO, REMOVE_TODO;
    }
}

