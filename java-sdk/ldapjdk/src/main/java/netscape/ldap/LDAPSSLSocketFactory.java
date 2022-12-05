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
 * Portions created by the Initial Developer are Copyright (C) 1999
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
package netscape.ldap;

import java.lang.reflect.Constructor;
import java.net.Socket;

/**
 * Creates an SSL socket connection to an LDAP Server.  This class
 * implements the <CODE>LDAPSSLSocketFactoryExt</CODE> interface.
 * <P>
 *
 * To construct an object of this class, you need to specify the
 * name of a class that implements the <CODE>javax.net.ssl.SSLSocket</CODE>
 * interface.  If you do not specify a class name, the class
 * <CODE>netscape.net.SSLSocket</CODE> is used by default.  This
 * class is included with Netscape Communicator 4.05 and up.
 * <P>
 *
 * If you are using a Java VM that provides certificate database
 * management (such as Netscape Communicator), you can authenticate
 * your client to a secure LDAP server by using certificates.
 * <P>
 *
 * @version 1.0
 * @see LDAPSSLSocketFactoryExt
 * @see LDAPConnection#LDAPConnection(netscape.ldap.LDAPSocketFactory)
 */
public class LDAPSSLSocketFactory
             implements LDAPSSLSocketFactoryExt, java.io.Serializable {

    static final long serialVersionUID = -3331456736649381427L;

    /**
     * Indicates if client authentication is on.
     */
    private boolean clientAuth = false;
    /**
     * Name of class implementing SSLSocket.
     */
    private String packageName = "netscape.net.SSLSocket";

    /**
     * The cipher suites
     */
    private transient Object cipherSuites = null;

    /**
     * Constructs an <CODE>LDAPSSLSocketFactory</CODE> object using
     * the default SSL socket implementation,
     * <CODE>netscape.net.SSLSocket</CODE>. (This class is provided
     * with Netscape Communicator 4.05 and higher.)
     */
    public LDAPSSLSocketFactory() {
    }

    /**
     * Constructs an <CODE>LDAPSSLSocketFactory</CODE> object using
     * the default SSL socket implementation,
     * <CODE>netscape.net.SSLSocket</CODE>. (This class is provided
     * with Netscape Communicator 4.05 and up.)
     * @param clientAuth <CODE>true</CODE> if certificate-based client
     * authentication is desired. By default, client authentication is
     * not used.
     */
    public LDAPSSLSocketFactory(boolean clientAuth) {
        this.clientAuth = clientAuth;
    }

    /**
     * Constructs an <CODE>LDAPSSLSocketFactory</CODE> object using
     * the specified class. The class must implement the interface
     * <CODE>javax.net.ssl.SSLSocket</CODE>.
     * @param className the name of a class implementing
     * the <CODE>javax.net.ssl.SSLSocket</CODE> interface.
     * Pass <code>null</code> for this parameter to use the
     * default SSL socket implementation,
     * <CODE>netscape.net.SSLSocket</CODE>, which is included with
     * Netscape Communicator 4.05 and higher.
     */
    public LDAPSSLSocketFactory(String className) {
        this.packageName = className;
    }

    /**
     * Constructs an <CODE>LDAPSSLSocketFactory</CODE> object using
     * the specified class. The class must implement the interface
     * <CODE>javax.net.ssl.SSLSocket</CODE>.
     * @param className the name of a class implementing
     * the <CODE>javax.net.ssl.SSLSocket</CODE> interface.
     * Pass <code>null</code> for this parameter to use the
     * default SSL socket implementation,
     * <CODE>netscape.net.SSLSocket</CODE>, which is included with
     * Netscape Communicator 4.05 and higher.
     * @param clientAuth <CODE>true</CODE> if certificate-based client
     * authentication is desired. By default, client authentication is
     * not used.
     */
    public LDAPSSLSocketFactory(String className, boolean clientAuth) {
        this.packageName = className;
        this.clientAuth = clientAuth;
    }

    /**
     * The constructor with the specified package for security and the specified
     * cipher suites.
     * @param className the name of a class implementing the interface
     * <CODE>javax.net.ssl.SSLSocket</CODE>.
     * Pass <code>null</code> for this parameter to use the
     * default SSL socket implementation,
     * <CODE>netscape.net.SSLSocket</CODE>, which is included with
     * Netscape Communicator 4.05 and higher.
     * @param cipherSuites the cipher suites to use for SSL connections
     */
    public LDAPSSLSocketFactory(String className, Object cipherSuites) {
        this.packageName = className;
        this.cipherSuites = cipherSuites;
    }

    /**
     * The constructor with the specified package for security and the specified
     * cipher suites.
     * @param className the name of a class implementing the interface
     * <CODE>javax.net.ssl.SSLSocket</CODE>.
     * Pass <code>null</code> for this parameter to use the
     * default SSL socket implementation,
     * <CODE>netscape.net.SSLSocket</CODE>, which is included with
     * Netscape Communicator 4.05 and higher.
     * @param cipherSuites the cipher suites to use for SSL connections
     * @param clientAuth <CODE>true</CODE> if certificate-based client
     * authentication is desired. By default, client authentication is
     * not used.
     */
    public LDAPSSLSocketFactory(String className, Object cipherSuites,
      boolean clientAuth) {
        this.packageName = className;
        this.cipherSuites = cipherSuites;
        this.clientAuth = clientAuth;
    }

    /**
     * Enables certificate-based client authentication for an
     * application. The application must be running in a Java VM
     * that provides transparent certificate database management
     * (for example, Netscape Communicator's Java VM).
     * Call this method before you call <CODE>makeSocket</CODE>.
     * @see netscape.ldap.LDAPSSLSocketFactory#isClientAuth
     * @see netscape.ldap.LDAPSSLSocketFactory#makeSocket
     * Note: enableClientAuth() is deprecated. This method is replaced
     * by any one of the following constructors:
     * <p>
     * <CODE>LDAPSSLSocketFactory(boolean)</CODE>
     * <CODE>LDAPSSLSocketFactory(java.lang.String, boolean)</CODE>
     * <CODE>LDAPSSLSocketFactory(java.lang.String, java.lang.Object, boolean)</CODE>
     */
    public void enableClientAuth() {
        this.clientAuth = true;
    }


    /**
     * <B>This method is currently not implemented</B>.
     * Enables client authentication for an application that uses
     * an external (file-based) certificate database.
     * Call this method before you call <CODE>makeSocket</CODE>.
     * @param certdb the pathname for the certificate database
     * @param keydb the pathname for the private key database
     * @param keypwd the password for the private key database
     * @param certnickname the alias for the certificate
     * @param keynickname the alias for the key
     * @see netscape.ldap.LDAPSSLSocketFactory#isClientAuth
     * @see netscape.ldap.LDAPSSLSocketFactory#makeSocket
     * @exception LDAPException Since this method is not yet implemented,
     * calling this method throws an exception.
     * Note: <CODE>enableClientAuth(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)</CODE> is deprecated.
     * This method is replaced by any one of the following constructors:
     * <p>
     * <CODE>LDAPSSLSocketFactory(boolean)</CODE>
     * <CODE>LDAPSSLSocketFactory(java.lang.String, boolean)</CODE>
     * <CODE>LDAPSSLSocketFactory(java.lang.String, java.lang.Object, boolean)</CODE>
     */
    public void enableClientAuth(String certdb, String keydb, String keypwd,
      String certnickname, String keynickname) throws LDAPException {
        throw new LDAPException("Client auth not supported now");
    }

    /**
     * Returns <code>true</code> if client authentication is enabled.
     * @see netscape.ldap.LDAPSSLSocketFactory
     */
    @Override
    public boolean isClientAuth() {
        return clientAuth;
    }

    /**
     * Returns the name of the class that implements SSL sockets for this factory.
     *
     * @return the name of the class that implements SSL sockets for this factory.
     */
    public String getSSLSocketImpl() {
        return packageName;
    }

    /**
     * Returns the suite of ciphers used for SSL connections made through
     * sockets created by this factory.
     *
     * @return the suite of ciphers used.
     */
    @Override
    public Object getCipherSuites() {
        return cipherSuites;
    }

    /**
     * Returns a socket to the LDAP server with the specified
     * host name and port number.
     * @param host the host to connect to
     * @param port the port number
     * @return the socket to the host name and port number.
     * @exception LDAPException A socket to the specified host and port
     * could not be created.
     * @see netscape.ldap.LDAPSSLSocketFactory
     */
    @Override
    public Socket makeSocket(String host, int port)
      throws LDAPException {

        if (clientAuth) {
            try {
                /* Check if running in Communicator; if so, enable client
                   auth */
                String[] types = { "java.lang.String" };
                java.lang.reflect.Method m =
                    DynamicInvoker.getMethod(
                        "netscape.security.PrivilegeManager",
                        "enablePrivilege",
                        types );
                if (m != null) {
                    Object[] args = new Object[1];
                    args[0] = "ClientAuth";
                    m.invoke( null, args);
                }
            } catch (Exception e) {
                String msg = "LDAPSSLSocketFactory.makeSocket: invoking " +
                    "enablePrivilege: " + e.toString();
                throw new LDAPException(msg, LDAPException.PARAM_ERROR);
            }
        }

        try {
            /* Instantiate the SSLSocketFactory implementation, and
               find the right constructor */
            Class<?> c = Class.forName(packageName);
            if (cipherSuites == null) {
                Constructor<?> m = c.getConstructor(String.class, int.class);
                return (Socket) m.newInstance(host, port);
            }
            Constructor<?> m = c.getConstructor(String.class, int.class, cipherSuites.getClass());
            return (Socket) m.newInstance(host, port, cipherSuites);
        } catch (NoSuchMethodException e) {
            throw new LDAPException("No appropriate constructor in " +
                    packageName, LDAPException.PARAM_ERROR);
        } catch (ClassNotFoundException e) {
            throw new LDAPException("Class " + packageName + " not found",
                                  LDAPException.PARAM_ERROR);
        } catch (Exception e) {
            throw new LDAPException("Failed to create SSL socket",
                                  LDAPException.CONNECT_ERROR);
        }
    }
}

