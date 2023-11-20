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
package netscape.ldap.controls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import netscape.ldap.LDAPControl;
import netscape.ldap.LDAPException;
import netscape.ldap.ber.stream.BERElement;
import netscape.ldap.ber.stream.BERInteger;
import netscape.ldap.ber.stream.BEROctetString;
import netscape.ldap.ber.stream.BERSequence;
import netscape.ldap.client.JDAPBERTagDecoder;

/**
 * Represents an LDAP v3 server control that specifies a simple pagd result
 * manipulation, which allows your LDAP client to get entries in multiple chunks
 * (The OID for this control is 1.2.840.113556.1.4.319).
 * <P>
 *
 * To use paged search you create a "paged search" control that specifies
 * the page size and the cookie from last search.  You include the control in a
 * search request. When a search is performed only a page is returned to the client
 * and a new search has to be performed to access the following elements.
 * <P>
 *
 *
 * When constructing an <CODE>LDAPPagedResultsControl</CODE> object,
 * you can specify the following information:
 * <P>
 *
 * <UL>
 * <LI>the size of the page to be returned
 * <LI>the cookie to keep track of the previous page. This is <CODE>null</CODE>
 * for the first page and the server returned value for the following.
 * </UL>
 * <P>
 *
 * For example:
 * <PRE>
 * ...
 *      LDAPConnection ld = new LDAPConnection();
 * 
 *      try {
 *          // Connect to server.
 *          ld.connect(3,3, hostname, portnumber, "", "" );
 *
 *          LDAPPagedResultsControl pagecon = new LDAPPagedResultsControl(false, 3);
 *          // Set the search constraints to use that control.
 *          LDAPSearchConstraints cons = new LDAPSearchConstraints();
 *          cons.setBatchSize(1);
 *          cons.setServerControls(pagecon);
 * 
 *          // Start the paged search.
 *          String cookie = null;
 *          int pag = 1;
 *          do{
 *              LDAPSearchResults res = ld.search(baseDn,
 *                      LDAPv3.SCOPE_SUB, filter, null, false, cons);
 * 
 *              // Loop through the incoming results.
 *              while (res.hasMoreElements()) {
 *                  LDAPEntry entry = res.next();
 *                  System.out.println("DN: " + entry.getDN());
 *              }
 *              for (LDAPControl c: res.getResponseControls()){
 *                  if(c instanceof LDAPPagedResultsControl resC){
 *                      cookie = resC.getCookie();
 *                      System.out.println("The control for pag " + pag + " return a total or " + resC.getPageSize() + " and cookie " + resC.getCookie());
 *                      if(cookie!=null){
 *                          pagecon = new LDAPPagedResultsControl(false, 3, cookie);
 *                          cons.setServerControls(pagecon);
 *                      }
 *                  }
 *              }
 *              pag++;
 *          } while (cookie != null && !cookie.isEmpty());
 *      } catch (Exception e) {
 *          e.printStackTrace();
 *      }
 * </PRE>
 *
 *
 *
 * @see netscape.ldap.LDAPControl
 * 
 * @author Marco Fargetta <mfargett@redhat.com>
 */
public class LDAPPagedResultsControl extends LDAPControl {
    public static final String PAGEDSEARCH  = "1.2.840.113556.1.4.319";
    private int pageSize;
    private String cookie;

    /**
     * Constructs an <CODE>LDAPPagedResultsControl</CODE> object
     * that specifies a paged search.
     * 
     * @param oid the oid of this control
     * @param critical <code>true</code> if this control is critical to the search
     * @param value the value associated with this control
     * 
     * @see netscape.ldap.LDAPcontrol
     * 
     * @throws LDAPException
     * @throws IOException If value contains an invalid BER sequence.
     * 
     * @see netscape.ldap.LDAPControl#register
     */
    public LDAPPagedResultsControl(String oid, boolean critical, byte[] vals)
        throws LDAPException, IOException {
        super(oid, critical, vals);
        if(!oid.equals(PAGEDSEARCH)) {
            throw new LDAPException("oid must be LDAPPagedResultsControl.PAGEDSEARCH",
            LDAPException.PARAM_ERROR);
        }

        ByteArrayInputStream inStream = new ByteArrayInputStream( vals );
        JDAPBERTagDecoder decoder = new JDAPBERTagDecoder();
        int[] nRead = { 0 };

        /* A sequence */        
        BERSequence seq = (BERSequence) BERElement.getElement(decoder, inStream, nRead);
        pageSize = ((BERInteger)seq.elementAt(0)).getValue();
        if(seq.size() == 1) {
            return;
        }
        BEROctetString t = (BEROctetString)seq.elementAt(1);
        byte[] cookie = t.getValue();
        this.cookie = cookie == null ? null : new String(t.getValue(), StandardCharsets.UTF_8);
    }

    /**
     * Constructs an <CODE>LDAPPagedResultsControl</CODE> object without a cookie.
     * 
     * This is equivalent to
     * <CODE>LDAPPagedResultsControl(critical, pageSize, null)</CODE>
     *
     * @param critical <code>true</code> if this control is critical to the search
     * @param pageSize the number of entries to be returned with the following
     * search request
     * 
     * @see netscape.ldap.LDAPControl
     */
    public LDAPPagedResultsControl(boolean critical, int pageSize) {
        this(critical, pageSize, null);
    }
    
    /**
     * Constructs an <CODE>LDAPPagedResultsControl</CODE>.
     * 
     * @param critical <code>true</code> if this control is critical to the search
     * @param pageSize the number of entries to be returned with the following
     * search request
     * @param cookie The cookie to access the next entries. This is an opaque value
     * returned by the server or <CODE>null</CODE> for the initial search
     *
     * @see netscape.ldap.LDAPControl
     */
    public LDAPPagedResultsControl(boolean critical, int pageSize, String cookie) {
        super(PAGEDSEARCH, critical, null);
        this.pageSize = pageSize;
        this.cookie = cookie;
        m_value = generateNextPageValues();
    }

    /**
     * Encode the parameters as requested for the control
     *
     * @return Binary sequence for next page
     */
    private byte[] generateNextPageValues() {
        
        BERSequence seq = new BERSequence();
        seq.addElement(new BERInteger(pageSize));
        if(cookie != null) {
            seq.addElement(new BEROctetString(cookie));
        } else {
            seq.addElement(new BEROctetString(""));
        }
        BEROctetString controlValue = new BEROctetString(flattenBER(seq));
        return controlValue.getValue();
    }

    /**
     * Gets the page size for the search request.
     * 
     * @return the number of entries to be returned when a search is requested to the server and
     * the number of entries available when returned from the server.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Gets the cookie for the following search request.
     * 
     * @return the cookie to use for the following request or null if all entries
     * have been returned.
     */
    public String getCookie() {
        return cookie;
    }
}
