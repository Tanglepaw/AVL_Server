/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vehicles_gps;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author brendanmcantosh
 */
public class TCP_Sender extends Thread {
    private List<VehData> All;
    private List<VehData> Active;

    private Socket socket;
    buildArrays build;
    Gson gson = new Gson();

    TCP_Sender(Socket socket, buildArrays build) {
        this.socket = socket;
        this.build = build;
        
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(30000);
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
            OutputStream output = socket.getOutputStream();  
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            
            System.out.println("Connected from ip: " + socket.getInetAddress());
            boolean stop = false;
            while(!stop)
            {
            String str = reader.readLine();
            if(str == null)
            {
                stop = true;
                System.out.println("Connection closed");
                continue;
            }
            
            
            //Parse request
            //If valid send all or available
            // all {"status":"all","vehicles"[]}
            //If invalad send {"status": "invalad","vehicles":[]}
            String send = null;
            if (str.equals("<AVL><vehicles>active</vehicles></AVL>")) //All
            {
                
                System.out.println("Active");
                //send = "{\"status\":\"active\",\"vehicles\":[";
                
                    Active = build.getActive();
                send = gson.toJson(Active) + "\n";
                continue;
            }// Active
            else if (str.equals("<AVL><vehicles>all</vehicles></AVL>"))
            {
                String Al = "active";
                send = gson.toJson(Al);
                System.out.println("All");
                All = build.getAll();
                send = gson.toJson(All) + "\n";
                System.out.println(send);
                out.write(str);
            }
            else
            {
                System.out.println("invalid XML");
                send = "{\"status\": \"invalid\",\"vehicles\":[]}\n";
                send = gson.toJson(str) + "\n";
                System.out.println(str);
                out.write(str);
                continue;
            }
                
            }
 
            
            
        } catch (IOException ex) {
            Logger.getLogger(TCP_Sender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
