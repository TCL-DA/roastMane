package com.cropster.roastingintelligence.remote;

import jakarta.ws.rs.client.Client;

public interface Authenticator {
   void open(Client var1);

   void invalidate();

   void close();
}
