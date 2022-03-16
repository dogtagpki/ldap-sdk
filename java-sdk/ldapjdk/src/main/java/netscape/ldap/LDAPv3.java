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

/**
 * Specifies additional features available in version 3 of the
 * LDAP protocol.
 *
 * @version 1.0
 */
@SuppressWarnings("removal")
public interface LDAPv3 extends LDAPv2 {

    /**
     * The default port number for LDAP servers.  You can specify
     * this identifier when calling the <CODE>LDAPConnection.connect</CODE>
     * method to connect to an LDAP server.
     * @see netscape.ldap.LDAPConnection#connect
     */
    public final static int DEFAULT_PORT = 389;

    /**
     * Option specifying how aliases are dereferenced.
     * <P>
     *
     * This option can have one of the following values:
     * <UL>
     * <LI><A HREF="#DEREF_NEVER"><CODE>DEREF_NEVER</CODE></A>
     * <LI><A HREF="#DEREF_FINDING"><CODE>DEREF_FINDING</CODE></A>
     * <LI><A HREF="#DEREF_SEARCHING"><CODE>DEREF_SEARCHING</CODE></A>
     * <LI><A HREF="#DEREF_ALWAYS"><CODE>DEREF_ALWAYS</CODE></A>
     * </UL>
     * <P>
     *
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int DEREF = 2;

    /**
     * Option specifying the maximum number of search results to
     * return.
     * <P>
     *
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int SIZELIMIT = 3;

    /**
     * Option specifying the maximum number of milliseconds to
     * wait for an operation to complete.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int TIMELIMIT = 4;

    /**
     * Option specifying the maximum number of milliseconds the
     * server should spend returning search results before aborting
     * the search.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int SERVER_TIMELIMIT = 5;

    /**
     * Option specifying whether or not referrals to other LDAP
     * servers are followed automatically.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     * @see netscape.ldap.LDAPRebind
     * @see netscape.ldap.LDAPRebindAuth
     */
    public static final int REFERRALS = 8;

    /**
     * Option specifying the object containing the method for
     * getting authentication information (the distinguished name
     * and password) used during a referral.  For example, when
     * referred to another LDAP server, your client uses this object
     * to obtain the DN and password.  Your client authenticates to
     * the LDAP server using this DN and password.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     * @see netscape.ldap.LDAPRebind
     * @see netscape.ldap.LDAPRebindAuth
     */
    public static final int REFERRALS_REBIND_PROC = 9;

    /**
     * Option specifying the maximum number of referrals to follow
     * in a sequence when requesting an LDAP operation.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int REFERRALS_HOP_LIMIT   = 10;

    /**
     * Option specifying the object containing the method for
     * authenticating to the server.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     * @see netscape.ldap.LDAPBind
     */
    public static final int BIND = 13;

    /**
     * Option specifying the version of the LDAP protocol
     * used by your client when interacting with the LDAP server.
     * If no version is set, the default version is 2.  If you
     * are planning to use LDAP v3 features (such as controls
     * or extended operations), you should set this version to 3
     * or specify version 3 as an argument to the <CODE>authenticate</CODE>
     * method of the <CODE>LDAPConnection</CODE> object.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     * @see netscape.ldap.LDAPConnection#authenticate(int, java.lang.String, java.lang.String)
     */
    public static final int PROTOCOL_VERSION = 17;

    /**
     * Option specifying the number of results to return at a time.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int BATCHSIZE = 20;


    /*
     * Valid options for Scope
     */

    /**
     * Specifies that the scope of a search includes
     * only the base DN (distinguished name).
     * @see netscape.ldap.LDAPConnection#search(java.lang.String, int, java.lang.String, java.lang.String[], boolean, netscape.ldap.LDAPSearchConstraints)
     */
    public static final int SCOPE_BASE = 0;

    /**
     * Specifies that the scope of a search includes
     * only the entries one level below the base DN (distinguished name).
     * @see netscape.ldap.LDAPConnection#search(java.lang.String, int, java.lang.String, java.lang.String[], boolean, netscape.ldap.LDAPSearchConstraints)   */
    public static final int SCOPE_ONE = 1;

    /**
     * Specifies that the scope of a search includes
     * the base DN (distinguished name) and all entries at all levels
     * beneath that base.
     * @see netscape.ldap.LDAPConnection#search(java.lang.String, int, java.lang.String, java.lang.String[], boolean, netscape.ldap.LDAPSearchConstraints)   */
    public static final int SCOPE_SUB = 2;


    /*
     * Valid options for Dereference
     */

    /**
     * Specifies that aliases are never dereferenced.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int DEREF_NEVER = 0;

    /**
     * Specifies that aliases are dereferenced when searching the
     * entries beneath the starting point of the search (but
     * not when finding the starting entry).
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int DEREF_SEARCHING = 1;

    /**
     * Specifies that aliases are dereferenced when finding the
     * starting point for the search (but not when searching
     * under that starting entry).
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int DEREF_FINDING = 2;

    /**
     * Specifies that aliases are always dereferenced.
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int DEREF_ALWAYS = 3;

    /**
     * Connects and authenticates to the LDAP server using the specified version of the
     * LDAP protocol.
     * @param version requested version of the LDAP protocol: currently 2 or 3
     * @param host hostname of the LDAP server
     * @param port port number of the LDAP server. To specify the
     * default port, use <CODE>DEFAULT_PORT</CODE>.
     * @param dn distinguished name to use for authentication
     * @param passwd password for authentication
     * @exception LDAPException Failed to connect and authenticate to the server.
     */
    public void connect(int version, String host, int port, String dn,
      String passwd) throws LDAPException;

    /**
     * Authenticates to the LDAP server (to which the object is currently
     * connected) using the specified name, password, and version
     * of the LDAP protocol. If the server does not support the requested
     * protocol version, an exception is thrown.  If the object has been
     * disconnected from an LDAP server, this method attempts to reconnect
     * to the server. If the object had already authenticated, the old
     * authentication is discarded.
     * @param version requested LDAP protocol version: currently 2 or 3.
     * @param dn if non-null and non-empty, specifies that the
     * connection and all operations through it should
     * authenticate with dn as the distinguished name
     * @param passwd if non-null and non-empty, specifies that the
     * connection and all operations through it should
     * authenticated with passwd as password
     * @exception LDAPException Failed to authenticate to the LDAP server.
     */
    public void authenticate(int version,
                             String dn,
                             String passwd)
                             throws LDAPException;

    /**
     * Authenticates to the LDAP server (to which the object is currently
     * connected) using the specified name, password, and version of the
     * LDAP protocol. If the server does not support the requested
     * version of the protocol, an exception is thrown.  If the
     * object has been disconnected from an LDAP server, this method
     * attempts to reconnect to the server. If the object had already
     * authenticated, the old authentication is discarded.
     * @param version requested LDAP protocol version: currently 2 or 3.
     * @param dn if non-null and non-empty, specifies that the
     * connection and all operations through it should authenticate
     * with dn as the distinguished name
     * @param passwd if non-null and non-empty, specifies that the
     * connection and all operations through it should authenticate
     * with passwd as password
     * @exception LDAPException Failed to authenticate to the LDAP server.
     */
    public void bind(int version,
                     String dn,
                     String passwd)
                     throws LDAPException;

    /**
     * Performs an extended operation on the directory. Extended operations
     * are part of version 3 of the LDAP protocol.
     * <P>
     *
     * @param op LDAPExtendedOperation object specifying the OID of the
     * extended operation and the data to use in the operation
     * @exception LDAPException Failed to execute the operation
     * @return LDAPExtendedOperation object representing the extended response
     * returned by the server.
     * @see LDAPExtendedOperation
     */
    public LDAPExtendedOperation extendedOperation( LDAPExtendedOperation op )
                                     throws LDAPException;

    /**
     * Renames and moves an entry in the directory.
     * @param DN original distinguished name (DN) for the entry
     * @param newRDN new relative distinguished name (RDN) for the entry
     * @param newParentDN distinguished name of the new parent entry of the
     * specified entry
     * @param deleteOldRDN specifies whether or not to remove the old RDN
     * when renaming and moving the entry. If <code>true</code>, the old RDN will be deleted.
     * @exception LDAPException Failed to rename the specified entry.
     */
    public void rename( String DN, String newRDN, String newParentDN,
      boolean deleteOldRDN ) throws LDAPException;

    /**
     * Renames and moves an entry in the directory.
     * @param DN original distinguished name (DN) for the entry
     * @param newRDN new relative distinguished name (RDN) for the entry
     * @param newParentDN distinguished name of the new parent entry of the
     * specified entry
     * @param deleteOldRDN specifies whether or not to remove the old RDN
     * when renaming and moving the entry. If <code>true</code>, the old RDN will be deleted.
     * @param cons the constraints set for the rename operation
     * @exception LDAPException Failed to rename the specified entry.
     */
    public void rename( String DN, String newRDN, String newParentDN,
      boolean deleteOldRDN, LDAPConstraints cons ) throws LDAPException;

    /**
     * Returns an array of the latest controls (if any) from the server.
     * @return an array of the controls returned by an operation,
     * or <CODE>null</CODE> if none.
     * @see netscape.ldap.LDAPControl
     */
    public LDAPControl[] getResponseControls();

    /**
     * Option specifying client controls for LDAP operations. These
     * controls are interpreted by the client and are not passed
     * to the LDAP server.
     * @see netscape.ldap.LDAPControl
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int CLIENTCONTROLS   = 11;

    /**
     * Option specifying server controls for LDAP operations. These
     * controls are passed to the LDAP server. They may also be returned by
     * the server.
     * @see netscape.ldap.LDAPControl
     * @see netscape.ldap.LDAPConnection#getOption
     * @see netscape.ldap.LDAPConnection#setOption
     */
    public static final int SERVERCONTROLS   = 12;

    /**
     * Attribute type that you can specify in the LDAPConnection
     * search method if you don't want to retrieve any of the
     * attribute types for entries found by the search.
     * @see netscape.ldap.LDAPConnection#search
     */
    public static final String NO_ATTRS = "1.1";

    /**
     * Attribute type that you can specify in the LDAPConnection
     * search method if you want to retrieve all attribute types.
     * You can use this if you want to retrieve all attributes in
     * addition to an operational attribute.  For example:
     * <P>
     *
     * <PRE>
     * ...
     * String [] MY_ATTRS = { LDAPv3.ALL_USER_ATTRS, "modifiersName",
     *     "modifyTimestamp" };
     * LDAPSearchResults res = ld.search( MY_SEARCHBASE,
     *     LDAPConnection.SCOPE_SUB, MY_FILTER, MY_ATTRS, false, cons );
     * ...
     * </PRE>
     * @see netscape.ldap.LDAPConnection#search
     */
    public static final String ALL_USER_ATTRS = "*";

}
