/*
 * Copyright (c) 2004 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */

package org.w3c.dom;

public interface NamedNodeMap {

    public Node getNamedItem(String name);

    public Node setNamedItem(Node arg) throws DOMException;

    public Node removeNamedItem(String name) throws DOMException;
 
    public Node item(int index);
 
    public int getLength();

    public Node getNamedItemNS(String namespaceURI, String localName)
                               throws DOMException;

    public Node setNamedItemNS(Node arg) throws DOMException;

    public Node removeNamedItemNS(String namespaceURI, String localName)
                                  throws DOMException;

}
