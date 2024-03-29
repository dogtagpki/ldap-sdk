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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Creates an SSL socket connection to an LDAP Server. This class is provided
 * by the package in which the SSL socket does not extend Socket object.
 * The class internally provides a wrapper to convert the SSL socket extending
 * the Object class to the one extending the Socket class.
 * This factory class implements the <CODE>LDAPSocketFactory</CODE> interface.
 * <P>
 *
 * To use this class, pass the instance of this factory object to the
 * <CODE>LDAPConnection</CODE> constructor.
 *
 * @version 1.0
 * @see LDAPSocketFactory
 * @see LDAPConnection#LDAPConnection(netscape.ldap.LDAPSocketFactory)
 */
public class LDAPSSLSocketWrapFactory
             implements LDAPSSLSocketFactoryExt, java.io.Serializable {

    static final long serialVersionUID = -4171548771815037740L;

    /**
     * The constructor with the specified package for security
     * @param className the name of a class which has an implementation
     * of the SSL Socket extending Object class
     */
    public LDAPSSLSocketWrapFactory(String className) {
        this.packageName = className;
    }

    /**
     * The constructor with the specified package for security and the
     * specified cipher suites.
     * @param className the name of a class which has an implementation
     * of the SSL Socket extending Object class
     * @param cipherSuites the cipher suites
     */
    public LDAPSSLSocketWrapFactory(String className, Object cipherSuites) {
        this.packageName = className;
        this.cipherSuites = cipherSuites;
    }

    /**
     * Returns socket to the specified host name and port number.
     * @param host the host to connect to
     * @param port the port number
     * @return the socket to the host name and port number as passed in.
     * @exception LDAPException A socket to the specified host and port
     * could not be created.
     */
    @Override
    public Socket makeSocket(String host, int port) throws LDAPException {

        LDAPSSLSocket s = null;

        try {
            if (cipherSuites == null)
                s = new LDAPSSLSocket(host, port, packageName);
            else
                s = new LDAPSSLSocket(host, port, packageName,
                  cipherSuites);
            return s;
        } catch (Exception e) {
            System.err.println("Exception: "+e.toString());
            throw new LDAPException("Failed to create SSL socket",
              LDAPException.CONNECT_ERROR);
        }
    }

    /**
     * Returns <code>true</code> if client authentication is to be used.
     * @return <code>true</code> if client authentication is enabled;
     * <code>false</code>if client authentication is disabled.
     */
    @Override
    public boolean isClientAuth() {
        return clientAuth;
    }

    /**
     * <B>(Not implemented yet)</B> <BR>
     * Enables client authentication for an application running in
     * a java VM which provides transparent certificate database management.
     * Calling this method has no effect after makeSocket() has been
     * called.
     * @exception LDAPException Since this method is not yet implemented,
     * calling this method throws an exception.
     */
    public void enableClientAuth() throws LDAPException {
        throw new LDAPException("Client Authentication is not implemented yet.");
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
     * Indicates if client authentication is on.
     */
    private boolean clientAuth = false;

    /**
     * Name of class implementing SSLSocket.
     */
    private String packageName = null;

    /**
     * The cipher suites
     */
    private transient Object cipherSuites = null;
}

// LDAPSSLSocket class wraps the implementation of the SSL socket
class LDAPSSLSocket extends Socket {

    public LDAPSSLSocket(String host, int port, String packageName)
      throws LDAPException {
        super();
        this.packageName = packageName;
        try {
            // instantiate the SSLSocketFactory implementation, and
            // find the right constructor
            Class<?> c = Class.forName(packageName);
            Constructor<?> m = c.getConstructor(String.class, int.class);
            this.socket = m.newInstance(host, port);
        } catch (NoSuchMethodException e) {
            throw new LDAPException("No appropriate constructor in " +
                    packageName, LDAPException.PARAM_ERROR);
        } catch (ClassNotFoundException e) {
            throw new LDAPException("Class " + packageName + " not found",
              LDAPException.OTHER);
        } catch (Exception e) {
            throw new LDAPException("Failed to create SSL socket",
              LDAPException.CONNECT_ERROR);
        }
    }

    public LDAPSSLSocket(String host, int port, String packageName,
      Object cipherSuites) throws LDAPException {
        super();
        this.packageName = packageName;

        try {
            // instantiate the SSLSocketFactory implementation, and
            // find the right constructor
            Class<?> c = Class.forName(packageName);
            if (cipherSuites == null)
                throw new LDAPException("Cipher Suites is required");
            Constructor<?> m = c.getConstructor(String.class, int.class, cipherSuites.getClass());
            this.socket = m.newInstance(host, port, cipherSuites);
        } catch (NoSuchMethodException e) {
            throw new LDAPException("No appropriate constructor in " +
                    packageName, LDAPException.PARAM_ERROR);
        } catch (ClassNotFoundException e) {
            throw new LDAPException("Class " + packageName + " not found",
              LDAPException.OTHER);
        } catch (Exception e) {
            throw new LDAPException("Failed to create SSL socket",
              LDAPException.CONNECT_ERROR);
        }
    }

    @Override
    public InputStream getInputStream() {
        try {
            Object obj = invokeMethod(socket, "getInputStream", null);
            return (InputStream)obj;
        } catch (LDAPException e) {
            printDebug(e.toString());
        }

        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            Object obj = invokeMethod(socket, "getOutputStream", null);
            return (OutputStream)obj;
        } catch (LDAPException e) {
            printDebug(e.toString());
        }

        return null;
    }

    @Override
    public synchronized void close() throws IOException {
        try {
            invokeMethod(socket, "close", null);
        } catch (LDAPException e) {
            printDebug(e.toString());
        }
    }

    public void close(boolean wait) {
        try {
            Object[] args = new Object[1];
            args[0] = wait;
            invokeMethod(socket, "close", args);
        } catch (LDAPException e) {
            printDebug(e.toString());
        }
    }

    @Override
    public InetAddress getInetAddress() {
        try {
            Object obj = invokeMethod(socket, "getInetAddress", null);
            return (InetAddress)obj;
        } catch (LDAPException e) {
            printDebug(e.toString());
        }

        return null;
    }

    @Override
    public int getLocalPort() {
        try {
            Object obj = invokeMethod(socket, "getLocalPort", null);
            return ((Integer)obj).intValue();
        } catch (LDAPException e) {
            printDebug(e.toString());
        }

        return -1;
    }

    @Override
    public int getPort() {
        try {
            Object obj = invokeMethod(socket, "getPort", null);
            return ((Integer)obj).intValue();
        } catch (LDAPException e) {
           printDebug(e.toString());
        }

        return -1;
    }

    private Object invokeMethod(Object obj, String name, Object[] args) throws
      LDAPException {
        try {
            Method m = getMethod(name);
            if (m != null) {
                return (m.invoke(obj, args));
            }
        } catch (Exception e) {
            throw new LDAPException("Invoking "+name+": "+
              e.toString(), LDAPException.PARAM_ERROR);
        }

        return null;
    }

    private Method getMethod(String name) throws
      LDAPException {
        try {
            Method method = null;
            if ((method = methodLookup.get(name)) != null)
                return method;

            Class<?> c = Class.forName(packageName);
            Method[] m = c.getMethods();
            for (int i = 0; i < m.length; i++ ) {
                if (m[i].getName().equals(name)) {
                    methodLookup.put(name, m[i]);
                    return m[i];
                }
            }
            throw new LDAPException("Method " + name + " not found in " +
              packageName);
        } catch (ClassNotFoundException e) {
            throw new LDAPException("Class "+ packageName + " not found");
        }
    }

    private void printDebug(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }

    private static final boolean DEBUG = true;
    private Object socket;
    private Hashtable<String, Method> methodLookup = new Hashtable<>();
    private String packageName = null;
}

