/* -*- Mode: C++; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2000
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package netscape.ldap.factory;

import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.mozilla.jss.CryptoManager;
import org.mozilla.jss.crypto.AlreadyInitializedException;
import org.mozilla.jss.ssl.javax.JSSSocket;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSocketFactory;
import netscape.ldap.LDAPTLSSocketFactory;

/**
 * Creates an SSL socket connection to a server, using the Netscape/Mozilla
 * JSS package.
 * This class implements the <CODE>LDAPSocketFactory</CODE>
 * interface.
 * <P>
 * By default, the factory uses "secmod.db", "key*.db" and "cert*.db"
 * databases in the current directory. If you need to override this default
 * setting, then you should use the constructor <CODE>JSSSocketFactory(certdbDir)</CODE>.
 *
 * @version 1.1
 * @see LDAPSocketFactory
 * @see LDAPConnection#LDAPConnection(netscape.ldap.LDAPSocketFactory)
 */

public class JSSSocketFactory implements Serializable, LDAPTLSSocketFactory {

    static final long serialVersionUID = -6926469178017736903L;

    /**
     * Constructs a new <CODE>JSSSocketFactory</CODE>, initializing the
     * JSS security system if it has not already been initialized.
     * <p>
     * The current directory is assumed to be the certificate database directory.
     *
     * @exception LDAPException on initialization error
     * @see netscape.ldap.factory.JSSSocketFactory#JSSSocketFactory(java.lang.String)
     */
    public JSSSocketFactory() throws LDAPException{
        initialize(".");
    }

    /**
     * Constructs a new <CODE>JSSSocketFactory</CODE>, initializing the
     * JSS security system if it has not already been initialized.
     *
     * @param certdbDir The full path, relative or absolute, of the certificate
     * database directory
     * @exception LDAPException on initialization error
     */
    public JSSSocketFactory( String certdbDir ) throws LDAPException{
        initialize( certdbDir );
    }

    /**
     * Initialize the JSS security subsystem.
     * <P>
     * This method allows you to override the current directory as the
     * default certificate database directory. The directory is expected
     * to contain <CODE>secmod.db</CODE>, <CODE>key*.db</CODE> and
     * <CODE>cert*.db</CODE> files as the security module database, key database
     * and certificate database respectively.
     * <P>
     * The method may be called only once, before the first instance of
     * <CODE>JSSSocketFactory</CODE> is created. When creating the first
     * instance, the constructor will automatically initialize the JSS
     * security subsystem using the defaults, unless it is already initialized.
     * <P>
     * @param certdbDir The full path, relative or absolute, of the certificate
     * database directory.
     * @exception LDAPException on initialization error
     * @see netscape.ldap.factory.JSSSocketFactory#JSSSocketFactory(String)
     */
    public static void initialize( String certdbDir ) throws LDAPException {
        try {
            CryptoManager.initialize( certdbDir );
        } catch (AlreadyInitializedException e) {
            // This is ok
        } catch (Exception e) {
            throw new LDAPException("Failed to initialize JSSSocketFactory: "
                                    + e.getMessage(), LDAPException.OTHER);
        }
    }

    /**
     * Creates an SSL socket
     *
     * @param host Host name or IP address of SSL server
     * @param port Port numbers of SSL server
     * @return A socket for an encrypted session
     * @exception LDAPException on error creating socket
     */
    public Socket makeSocket( String host, int port ) throws LDAPException {
        JSSSocket socket = null;
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("NssX509", "Mozilla-JSS");
            KeyManager[] kms = kmf.getKeyManagers();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("NssX509", "Mozilla-JSS");
            TrustManager[] tms = tmf.getTrustManagers();

            SSLContext ctx = SSLContext.getInstance("TLS", "Mozilla-JSS");
            ctx.init(kms, tms, null);

            SSLSocketFactory socketFactory = ctx.getSocketFactory();

            socket = (JSSSocket) socketFactory.createSocket(host, port);
            socket.forceHandshake();

        }
        catch (UnknownHostException e) {
            throw new LDAPException("JSSSocketFactory.makeSocket - Unknown host: " + host,
                                    LDAPException.CONNECT_ERROR);

        }
        catch (Exception e) {
            throw new LDAPException("JSSSocketFactory.makeSocket " +
                                    host + ":" + port + ", " + e.getMessage(),
                                    LDAPException.CONNECT_ERROR);
        }

        return socket;
    }

    /**
     * Creates an SSL socket layered over an existing socket.
     *
     * Used for the startTLS implementation (RFC2830).
     *
     * @param s An existing non-SSL socket
     * @return A SSL socket layered over the input socket
     * @exception LDAPException on error creating socket
     * @since LDAPJDK 4.17
     */
    public Socket makeSocket(Socket s) throws LDAPException {
        JSSSocket socket = null;
        String host = s.getInetAddress().getHostName();
        int port = s.getPort();
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("NssX509", "Mozilla-JSS");
            KeyManager[] kms = kmf.getKeyManagers();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("NssX509", "Mozilla-JSS");
            TrustManager[] tms = tmf.getTrustManagers();

            SSLContext ctx = SSLContext.getInstance("TLS", "Mozilla-JSS");
            ctx.init(kms, tms, null);

            SSLSocketFactory socketFactory = ctx.getSocketFactory();

            socket = (JSSSocket) socketFactory.createSocket(host, port);
            socket.forceHandshake();

        } catch (Exception e) {
            throw new LDAPException("JSSSocketFactory - start TLS, " + e.getMessage(),
                                    LDAPException.TLS_NOT_SUPPORTED);
        }

        return socket;
    }
}
