
/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.transforms.implementations;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;


/**
 * Implements the <CODE>http://www.w3.org/2000/09/xmldsig#base64</CODE> decoding
 * transform.
 *
 * <p>The normative specification for base64 decoding transforms is
 * <A HREF="http://www.w3.org/TR/2001/CR-xmldsig-core-20010419/#ref-MIME">[MIME]</A>.
 * The base64 Transform element has no content. The input
 * is decoded by the algorithms. This transform is useful if an
 * application needs to sign the raw data associated with the encoded
 * content of an element. </p>
 *
 * <p>This transform requires an octet stream for input.
 * If an XPath node-set (or sufficiently functional alternative) is
 * given as input, then it is converted to an octet stream by
 * performing operations logically equivalent to 1) applying an XPath
 * transform with expression self::text(), then 2) taking the string-value
 * of the node-set. Thus, if an XML element is identified by a barename
 * XPointer in the Reference URI, and its content consists solely of base64
 * encoded character data, then this transform automatically strips away the
 * start and end tags of the identified element and any of its descendant
 * elements as well as any descendant comments and processing instructions.
 * The output of this transform is an octet stream.</p>
 *
 * @author Christian Geuer-Pollmann
 * @see org.apache.xml.security.utils.Base64
 */
public class TransformBase64Decode extends TransformSpi {

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_BASE64_DECODE;

   /**
    * Method engineGetURI
    *
    *
    */
   protected String engineGetURI() {
      return TransformBase64Decode.implementedTransformURI;
   }

   //J-
   public boolean wantsOctetStream ()   { return true; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return true; }
   public boolean returnsNodeSet ()     { return false; }
   //J+

   /**
    * Method enginePerformTransform
    *
    * @param input
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws IOException, CanonicalizationException,
                  TransformationException, InvalidCanonicalizerException {

	 try {
      if (input.isOctetStream() || isTextNodeSet(input.getNodeSet())) {
         try {
            byte[] base64Bytes = input.getBytes();
            byte[] decodedBytes = Base64.decode(base64Bytes);

            return new XMLSignatureInput(
               new ByteArrayInputStream(decodedBytes));
         } catch (Base64DecodingException ex) {
            throw new TransformationException("empty", ex);
         }
      } else {
		  try {
            Document doc =
               DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                  input.getOctetStream());
            DocumentTraversal dt = ((DocumentTraversal) doc);
            Node rootNode = (Node) doc;

            // we accept all nodes
            NodeFilter nodefilter = new AlwaysAcceptNodeFilter();
            TreeWalker treewalker = dt.createTreeWalker(rootNode,
                                                        NodeFilter.SHOW_ALL,
                                                        nodefilter, true);
            StringBuffer sb = new StringBuffer();

            process(treewalker, sb);

            byte[] decodedBytes = Base64.decode(sb.toString());
			
            return new XMLSignatureInput(
               new ByteArrayInputStream(decodedBytes));
		  } catch (ParserConfigurationException e) {
			  throw new TransformationException("c14n.Canonicalizer.Exception",
												e);
		  } catch (SAXException e) {
			  throw new TransformationException("SAX exception", e);
		  } catch (Base64DecodingException ex) {
			  throw new TransformationException("empty", ex);
		  }
      }
	 } catch (ParserConfigurationException e) {
		 throw new TransformationException("c14n.Canonicalizer.Exception",
										   e);
	 } catch (SAXException e) {
		 throw new TransformationException("SAX exception", e);
	 }
   }

   /**
    * Method process
    *
    * @param treewalker
    * @param sb
    */
   private void process(TreeWalker treewalker, StringBuffer sb) {

      Node currentNode = treewalker.getCurrentNode();

      if (currentNode.getNodeType() == Node.TEXT_NODE) {
         sb.append(((Text) currentNode).getData());
      }

      for (Node node1 = treewalker.firstChild(); node1 != null;
              node1 = treewalker.nextSibling()) {
         process(treewalker, sb);
      }

      treewalker.setCurrentNode(currentNode);
   }

   /**
	* Method to take a set of nodes and check whether any are "non-text"
	*/

   private boolean isTextNodeSet(Set s) {

	   boolean isText = true;

	   Iterator it = s.iterator();
	   while (it.hasNext() && isText) {
				
		   Node n = (Node) it.next();
		   if (n.getNodeType() != Node.TEXT_NODE)
			   isText = false;
	   }

	   return isText;
   }

   /**
    * This {@link NodeFilter} always returns <code>true</code>
    *
    * @author Christian Geuer-Pollmann
    */
   public class AlwaysAcceptNodeFilter implements NodeFilter {

      /**
       * Method acceptNode
       *
       * @param n
       *
       */
      public short acceptNode(Node n) {
         return NodeFilter.FILTER_ACCEPT;
      }
   }
}
