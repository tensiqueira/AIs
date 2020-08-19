package org.pelizzari.test;

import org.pelizzari.db.DBConnection;

import java.sql.Connection;

/**
 * @author Siqueira
 */

public class TestConnection {

    public static void main(String args[]){
        Connection con = DBConnection.getCon();
    }

}
